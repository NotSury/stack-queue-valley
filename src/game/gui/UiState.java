package game.gui;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Everything the window needs to remember between repaints. Only the Swing
 * thread reads or changes it.
 */
final class UiState {

    /** Most log lines kept before the oldest are dropped. */
    static final int MAX_LOG_LINES = 600;

    /**
     * One line of the event log.
     *
     * @param text   the message, or the turn label for a heading
     * @param turn   the turn the line belongs to
     * @param header true for a turn heading
     */
    record LogLine(String text, int turn, boolean header) {
    }

    /** The latest snapshot, or {@code null} before the game has started. */
    GameSnapshot snapshot;

    /** Event log, oldest first. */
    final List<LogLine> log = new ArrayList<>();

    /** How many wrapped lines the log is scrolled up from the newest. */
    int logScroll;

    /** How many rows the action list is scrolled down. */
    int menuScroll;

    /** Mouse position in canvas coordinates, or {@code null} when outside. */
    Point2D mouse;

    /** Counts animation ticks. */
    long tick;

    /** Text describing a problem that stopped the game, or {@code null}. */
    String error;

    /**
     * Stores a new snapshot and appends its messages to the log.
     *
     * @param next the snapshot to show
     */
    void accept(GameSnapshot next) {
        boolean newTurn = snapshot == null || next.turn() != snapshot.turn();
        if (!next.events().isEmpty()) {
            if (newTurn || log.isEmpty()) {
                log.add(new LogLine("Turn " + next.turn(), next.turn(), true));
            }
            for (String event : next.events()) {
                log.add(new LogLine(event, next.turn(), false));
            }
            while (log.size() > MAX_LOG_LINES) {
                log.remove(0);
            }
            logScroll = 0;
        }
        if (newTurn || snapshot == null || next.page() != snapshot.page()) {
            menuScroll = 0;
        }
        snapshot = next;
    }
}
