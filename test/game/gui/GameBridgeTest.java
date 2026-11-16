package game.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * Tests how the bridge sorts the game's console output into a menu and an
 * event log, and how it hands keys back to the game.
 */
@Timeout(30)
class GameBridgeTest {

    private final FakeGame game = new FakeGame();
    private final GameBridge bridge = new GameBridge();

    /** Prints one normal player turn the way the game does. */
    private void printTurn() {
        bridge.mapDrawStarted();
        bridge.printed("Parsnip Crop grows!");
        bridge.printed(FakeGame.LABEL);
        bridge.printed("HP: 100");
        bridge.printed("SP: 20");
        bridge.printed("Inventory: [Hoe]");
        bridge.printed("Coins: 50");
        bridge.printed("");
        bridge.printed("Page 1/1");
        bridge.printed("8: " + FakeGame.LABEL + " moves North");
        bridge.printed("5: " + FakeGame.LABEL + " does nothing");
        bridge.printed("a: " + FakeGame.LABEL + " tills Dirt");
    }

    @Test
    void statusBlockIsDroppedAndMenuAndEventsAreSeparated() throws Exception {
        bridge.connect(game, game.snapshots::add);
        CompletableFuture<Character> key = CompletableFuture.supplyAsync(() -> {
            bridge.printed("Cow (20/20) moves North");
            printTurn();
            return bridge.nextKey();
        });

        GameSnapshot snapshot = game.next();

        assertTrue(snapshot.awaitingInput());
        assertEquals(1, snapshot.turn());
        assertEquals(List.of("Cow (20/20) moves North", "Parsnip Crop grows!"), snapshot.events());
        assertEquals(3, snapshot.menu().size());
        assertEquals("moves North", snapshot.entryFor('8').text());
        assertTrue(snapshot.entryFor('8').isMove());
        assertEquals("tills Dirt", snapshot.entryFor('a').text());
        assertFalse(snapshot.entryFor('a').isMove());
        assertNull(snapshot.entryFor('z'));

        assertTrue(bridge.submit('a'));
        assertEquals('a', key.get(10, TimeUnit.SECONDS));
    }

    @Test
    void keysThatAreNotOnTheMenuAreIgnored() throws Exception {
        bridge.connect(game, game.snapshots::add);
        CompletableFuture<Character> key = CompletableFuture.supplyAsync(() -> {
            printTurn();
            return bridge.nextKey();
        });
        game.next();

        bridge.submit('z');
        bridge.submit('6');
        bridge.submit('5');

        assertEquals('5', key.get(10, TimeUnit.SECONDS));
    }

    @Test
    void keysPressedWhileTheGameIsBusyAreRejected() {
        bridge.connect(game, game.snapshots::add);

        assertFalse(bridge.isAwaitingInput());
        assertFalse(bridge.submit('5'));
    }

    @Test
    void turnWithoutAMenuStillPublishesItsEvents() throws Exception {
        bridge.connect(game, game.snapshots::add);

        bridge.mapDrawStarted();
        bridge.printed("Farmer Bob (100/100) struggles against the web");
        bridge.mapDrawStarted();

        GameSnapshot snapshot = game.next();
        assertFalse(snapshot.awaitingInput());
        assertEquals(List.of("Farmer Bob (100/100) struggles against the web"), snapshot.events());
        assertTrue(snapshot.menu().isEmpty());
    }

    @Test
    void endOfGameIsPublished() throws Exception {
        bridge.connect(game, game.snapshots::add);

        bridge.printed("Game Over");
        bridge.gameEnded();

        GameSnapshot snapshot = game.next();
        assertNotNull(snapshot);
        assertTrue(snapshot.gameOver());
        assertFalse(snapshot.awaitingInput());
        assertEquals(List.of("Game Over"), snapshot.events());
    }
}
