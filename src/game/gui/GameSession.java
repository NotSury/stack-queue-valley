package game.gui;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Runs the real game on its own thread and exposes it to an interface as a
 * stream of snapshots and a way to send keys.
 *
 * <p>The session wires together the pieces of the adapter: the console
 * redirect, the display the engine prints to, and the world itself. The game
 * code that runs is exactly the code the console application runs.</p>
 */
public final class GameSession implements AutoCloseable {

    private final GameBridge bridge = new GameBridge();
    private final Consumer<GameSnapshot> listener;
    private final Consumer<Throwable> onFailure;
    private ConsoleRedirect redirect;
    private Thread thread;

    /**
     * Creates a session. Nothing runs until {@link #start()}.
     *
     * @param listener  receives each snapshot; called on the game thread
     * @param onFailure receives any unexpected failure of the game thread
     */
    public GameSession(Consumer<GameSnapshot> listener, Consumer<Throwable> onFailure) {
        this.listener = Objects.requireNonNull(listener);
        this.onFailure = Objects.requireNonNull(onFailure);
    }

    /**
     * Builds the world and starts the game loop on a background thread.
     */
    public synchronized void start() {
        if (thread != null) {
            throw new IllegalStateException("The session has already been started.");
        }
        // The redirect must be in place before any engine display exists,
        // because a display captures the standard input when it is created.
        redirect = new ConsoleRedirect(bridge);
        thread = new Thread(this::play, "stack-queue-valley-game");
        thread.setDaemon(true);
        thread.start();
    }

    private void play() {
        try {
            GuiWorld world = new GuiWorld(new GuiDisplay(bridge), new SpriteCatalog());
            bridge.connect(world, listener);
            world.initialise();
            world.run();
            bridge.gameEnded();
        } catch (Throwable failure) {
            if (!Thread.currentThread().isInterrupted()) {
                onFailure.accept(failure);
            }
        }
    }

    /**
     * Sends a key to the game, as if it had been typed at the console.
     *
     * @param key the key to send
     * @return true if the game was waiting for input and took the key
     */
    public boolean submit(char key) {
        return bridge.submit(key);
    }

    /**
     * Reports whether the game is waiting for the player.
     *
     * @return true while the game is blocked waiting for a key
     */
    public boolean isAwaitingInput() {
        return bridge.isAwaitingInput();
    }

    /**
     * Stops the game thread and restores the standard streams.
     */
    @Override
    public synchronized void close() {
        if (thread != null) {
            thread.interrupt();
            try {
                thread.join(2000);
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
            }
        }
        if (redirect != null) {
            redirect.close();
            redirect = null;
        }
    }
}
