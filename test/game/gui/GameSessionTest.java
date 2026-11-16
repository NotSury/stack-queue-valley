package game.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import game.gui.GameSnapshot.TileView;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * Runs the real Stack Queue Valley world through the graphical adapter,
 * without opening a window.
 */
@Timeout(60)
class GameSessionTest {

    private final BlockingQueue<GameSnapshot> snapshots = new LinkedBlockingQueue<>();
    private final AtomicReference<Throwable> failure = new AtomicReference<>();

    private GameSnapshot nextPrompt() throws InterruptedException {
        while (true) {
            GameSnapshot snapshot = snapshots.poll(20, TimeUnit.SECONDS);
            assertNull(failure.get(), "the game thread failed");
            assertNotNull(snapshot, "the game did not ask for input in time");
            if (snapshot.awaitingInput() || snapshot.gameOver()) {
                return snapshot;
            }
        }
    }

    @Test
    void realGameStartsOnTheFarmAndAdvancesWhenAKeyIsSent() throws Exception {
        PrintStream out = System.out;
        InputStream in = System.in;
        try (GameSession session = new GameSession(snapshots::add, failure::set)) {
            session.start();

            GameSnapshot first = nextPrompt();
            assertEquals("Inherited Farm", first.mapName());
            assertEquals(20, first.width());
            assertEquals(7, first.height());
            assertEquals(1, first.turn());
            assertEquals("Farmer Bob", first.player().name());
            assertEquals(first.player().maxHp(), first.player().hp());
            assertFalse(first.player().inventory().isEmpty());
            assertNotNull(first.entryFor('5'), "waiting is always offered");

            TileView own = first.tiles()[first.player().x()][first.player().y()];
            assertTrue(own.player());
            assertEquals("actor.farmer", own.actor());

            assertTrue(session.submit('5'));
            GameSnapshot second = nextPrompt();
            assertEquals(2, second.turn());
            assertTrue(second.events().stream().anyMatch(line -> line.contains("Farmer Bob")),
                    "the result of the player's action is logged");
        }
        assertSame(out, System.out, "standard output is restored");
        assertSame(in, System.in, "standard input is restored");
    }

    @Test
    void everyTileOfTheStartingMapHasArtwork() throws Exception {
        SpriteSheet sheet = new SpriteSheet();
        try (GameSession session = new GameSession(snapshots::add, failure::set)) {
            session.start();
            GameSnapshot first = nextPrompt();

            for (int x = 0; x < first.width(); x++) {
                for (int y = 0; y < first.height(); y++) {
                    TileView tile = first.tiles()[x][y];
                    for (String id : new String[] {tile.ground(), tile.item(), tile.actor()}) {
                        if (id != null) {
                            assertNotNull(sheet.get(id), "no artwork for " + id + " at " + x + "," + y);
                        }
                    }
                }
            }
        }
    }
}
