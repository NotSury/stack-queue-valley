package game.gui;

import game.gui.GameSnapshot.MenuEntry;
import game.gui.GameSnapshot.PlayerView;
import game.gui.GameSnapshot.TileView;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * A stand-in for the game world in adapter tests: a one-tile map and a player
 * called Farmer Bob. It also collects the snapshots a bridge publishes.
 */
final class FakeGame implements GameBridge.StateReader {

    static final String LABEL = "Farmer Bob (100/100)";

    final BlockingQueue<GameSnapshot> snapshots = new LinkedBlockingQueue<>();

    @Override
    public GameSnapshot read(List<MenuEntry> menu, int page, int totalPages, List<String> events,
                             boolean awaitingInput, boolean gameOver, int turn) {
        TileView[][] tiles = {{new TileView("ground.dirt", null, "actor.farmer", true)}};
        PlayerView player = new PlayerView("Farmer Bob", 100, 100, 20, 20, 50, List.of(), List.of(), 0, 0);
        return new GameSnapshot("Test Map", 1, 1, tiles, player, menu, page, totalPages, events,
                awaitingInput, gameOver, turn);
    }

    @Override
    public String playerLabel() {
        return LABEL;
    }

    /**
     * Waits for the next snapshot.
     *
     * @return the snapshot
     * @throws InterruptedException if the wait is interrupted
     */
    GameSnapshot next() throws InterruptedException {
        GameSnapshot snapshot = snapshots.poll(10, TimeUnit.SECONDS);
        if (snapshot == null) {
            throw new AssertionError("no snapshot was published within ten seconds");
        }
        return snapshot;
    }
}
