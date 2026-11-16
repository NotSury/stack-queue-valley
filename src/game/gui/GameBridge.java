package game.gui;

import game.gui.GameSnapshot.MenuEntry;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Connects the unmodified console game to a user interface.
 *
 * <p>The game still believes it is talking to a console. Everything it prints
 * arrives here as lines, and every key it reads is supplied from here. The
 * bridge sorts the printed lines into three kinds:</p>
 * <ul>
 *   <li>the player's status block, which is discarded because the interface
 *       reads the real statistics instead;</li>
 *   <li>the engine menu ({@code Page 1/1} followed by {@code key: action}
 *       lines), which becomes the list of selectable actions;</li>
 *   <li>everything else, which becomes the event log.</li>
 * </ul>
 *
 * <p>Threading: the game runs on its own thread and blocks inside
 * {@link #nextKey()} while the player decides. The interface thread calls only
 * {@link #submit(char)} and {@link #isAwaitingInput()}. All other state belongs
 * to the game thread.</p>
 */
public final class GameBridge {

    /**
     * Reads the visible game state. Called on the game thread while the game
     * is paused, so it may touch live game objects safely.
     */
    public interface StateReader {

        /**
         * Builds a snapshot of the current game.
         *
         * @param menu          actions on the current menu page
         * @param page          current menu page
         * @param totalPages    number of menu pages
         * @param events        messages printed since the previous snapshot
         * @param awaitingInput whether the player is being asked to act
         * @param gameOver      whether the game has ended
         * @param turn          number of turns shown so far
         * @return the snapshot
         */
        GameSnapshot read(List<MenuEntry> menu, int page, int totalPages, List<String> events,
                          boolean awaitingInput, boolean gameOver, int turn);

        /**
         * Returns the text the game prefixes to each of the player's actions,
         * for example {@code Farmer Bob (100/100)}.
         *
         * @return the player's current label, or an empty string
         */
        String playerLabel();
    }

    private static final Pattern PAGE_HEADER = Pattern.compile("^Page (\\d+)/(\\d+)$");
    private static final int STATUS_BLOCK_LINES = 6;
    private static final String[] STATUS_PREFIXES = {"HP: ", "SP: ", "Inventory: ", "Coins: "};

    private enum Mode { EVENTS, AFTER_MAP, MENU }

    private final BlockingQueue<Character> input = new LinkedBlockingQueue<>();
    private final Deque<Character> queuedKeys = new ArrayDeque<>();
    private final List<String> events = new ArrayList<>();
    private final List<String> afterMap = new ArrayList<>();
    private final List<MenuEntry> menu = new ArrayList<>();
    private final List<Character> firstPageKeys = new ArrayList<>();

    private StateReader reader;
    private Consumer<GameSnapshot> listener = snapshot -> { };
    private Mode mode = Mode.EVENTS;
    private int page = 1;
    private int totalPages = 1;
    private int turn;
    private volatile boolean awaitingInput;

    /**
     * Sets where the bridge reads game state from and where it sends snapshots.
     *
     * @param reader   source of game state
     * @param listener receiver of snapshots; called on the game thread
     */
    public void connect(StateReader reader, Consumer<GameSnapshot> listener) {
        this.reader = Objects.requireNonNull(reader);
        this.listener = Objects.requireNonNull(listener);
    }

    // ------------------------------------------------------------------
    // Called by the game thread
    // ------------------------------------------------------------------

    /**
     * Receives text printed by the game. Text may hold several lines.
     *
     * @param text the printed text
     */
    public void printed(String text) {
        if (text == null) {
            return;
        }
        for (String line : text.split("\\R", -1)) {
            line(line);
        }
    }

    private void line(String line) {
        Matcher header = PAGE_HEADER.matcher(line);
        if (header.matches()) {
            keepEventsPrintedBeforeStatus();
            mode = Mode.MENU;
            page = Integer.parseInt(header.group(1));
            totalPages = Integer.parseInt(header.group(2));
            menu.clear();
            return;
        }
        switch (mode) {
            case MENU -> {
                MenuEntry entry = parseEntry(line);
                if (entry != null) {
                    menu.add(entry);
                } else {
                    addEvent(line);
                }
            }
            case AFTER_MAP -> afterMap.add(line);
            default -> addEvent(line);
        }
    }

    private MenuEntry parseEntry(String line) {
        if (line.length() < 3 || line.charAt(1) != ':' || line.charAt(2) != ' ') {
            return null;
        }
        String text = line.substring(3);
        String label = reader == null ? "" : reader.playerLabel();
        if (!label.isEmpty() && text.startsWith(label + " ")) {
            text = text.substring(label.length() + 1);
        }
        return new MenuEntry(line.charAt(0), text.trim());
    }

    private void addEvent(String line) {
        if (!line.isBlank()) {
            events.add(line.stripTrailing());
        }
    }

    /**
     * The player's status block is the last few lines printed before the
     * menu. Anything printed earlier in the same turn is a real event.
     */
    private void keepEventsPrintedBeforeStatus() {
        String label = reader == null ? "" : reader.playerLabel();
        int end = afterMap.size();
        int removed = 0;
        while (end > 0 && removed < STATUS_BLOCK_LINES && isStatusLine(afterMap.get(end - 1), label)) {
            end--;
            removed++;
        }
        for (int index = 0; index < end; index++) {
            addEvent(afterMap.get(index));
        }
        afterMap.clear();
    }

    private static boolean isStatusLine(String line, String label) {
        if (line.isBlank() || (!label.isEmpty() && line.equals(label))) {
            return true;
        }
        for (String prefix : STATUS_PREFIXES) {
            if (line.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Notes that the game has started drawing the map, which marks the start
     * of the player's turn.
     */
    public void mapDrawStarted() {
        if (mode == Mode.AFTER_MAP) {
            // The previous turn passed without a menu (for example while the
            // player was bound by a web), so show what happened during it.
            afterMap.forEach(this::addEvent);
            afterMap.clear();
            publish(false, false);
        }
        mode = Mode.AFTER_MAP;
        turn++;
        firstPageKeys.clear();
        queuedKeys.clear();
    }

    /**
     * Supplies the next key to the game, blocking until the player chooses.
     *
     * @return a key that is valid for the menu the game is showing
     */
    public char nextKey() {
        if (mode == Mode.AFTER_MAP) {
            afterMap.forEach(this::addEvent);
            afterMap.clear();
        }
        if (page == 1) {
            firstPageKeys.clear();
            menu.forEach(entry -> firstPageKeys.add(entry.key()));
        }
        if (queuedKeys.isEmpty()) {
            input.clear();
            awaitingInput = true;
            try {
                publish(true, false);
                while (queuedKeys.isEmpty()) {
                    queueKeysFor(input.take());
                }
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("The game was interrupted while waiting for input.");
            } finally {
                awaitingInput = false;
            }
        }
        mode = Mode.EVENTS;
        return queuedKeys.poll();
    }

    /**
     * Turns a requested key into the keys the engine needs. A key on the
     * current page is passed through. A first-page key requested from a later
     * page is reached by turning back, exactly as a console player would.
     */
    private void queueKeysFor(char key) {
        for (MenuEntry entry : menu) {
            if (entry.key() == key) {
                queuedKeys.add(key);
                return;
            }
        }
        if (page > 1 && firstPageKeys.contains(key)) {
            for (int back = page; back > 1; back--) {
                queuedKeys.add('<');
            }
            queuedKeys.add(key);
        }
    }

    /**
     * Notes that the game has finished and publishes a final snapshot.
     */
    public void gameEnded() {
        afterMap.forEach(this::addEvent);
        afterMap.clear();
        menu.clear();
        mode = Mode.EVENTS;
        publish(false, true);
    }

    private void publish(boolean awaiting, boolean gameOver) {
        if (reader == null) {
            return;
        }
        List<MenuEntry> shownMenu = awaiting ? List.copyOf(menu) : List.of();
        GameSnapshot snapshot = reader.read(shownMenu, page, totalPages, List.copyOf(events),
                awaiting, gameOver, turn);
        events.clear();
        listener.accept(snapshot);
    }

    // ------------------------------------------------------------------
    // Called by the interface thread
    // ------------------------------------------------------------------

    /**
     * Offers a key press to the game. Presses made while the game is busy are
     * ignored, and keys that match no action are ignored by the game thread.
     *
     * @param key the key the player chose
     * @return true if the game was waiting and the key was passed on
     */
    public boolean submit(char key) {
        if (!awaitingInput) {
            return false;
        }
        return input.offer(key);
    }

    /**
     * Reports whether the game is waiting for the player.
     *
     * @return true while the game is blocked waiting for a key
     */
    public boolean isAwaitingInput() {
        return awaitingInput;
    }
}
