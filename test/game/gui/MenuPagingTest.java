package game.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Menu;
import edu.monash.fit2099.engine.positions.GameMap;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * Drives the engine's real paged menu through the graphical adapter.
 *
 * <p>The engine shows 26 actions per page, and its page-turn action creates a
 * console display of its own. These tests check that long menus still work in
 * the window without any change to the engine.</p>
 */
@Timeout(30)
class MenuPagingTest {

    /** An action that reports its own name when executed. */
    private static final class Named extends Action {
        private final String name;
        private final String hotkey;

        Named(String name, String hotkey) {
            this.name = name;
            this.hotkey = hotkey;
        }

        @Override
        public String execute(Actor actor, GameMap map) {
            return name;
        }

        @Override
        public String menuDescription(Actor actor) {
            return "does " + name;
        }

        @Override
        public String hotkey() {
            return hotkey;
        }
    }

    private static Menu longMenu() {
        ActionList actions = new ActionList();
        actions.add(new Named("north", "8"));
        for (int index = 0; index < 60; index++) {
            actions.add(new Named("thing " + index, null));
        }
        return new Menu(actions);
    }

    /** Runs the menu as the engine would: show it, then execute the choice. */
    private static String play(GameBridge bridge) {
        GuiDisplay display = new GuiDisplay(bridge);
        bridge.mapDrawStarted();
        return longMenu().showMenu(null, display).execute(null, null);
    }

    @Test
    void actionOnALaterPageIsReachedWithTheNextPageKey() throws Exception {
        PrintStream out = System.out;
        InputStream in = System.in;
        FakeGame game = new FakeGame();
        GameBridge bridge = new GameBridge();
        bridge.connect(game, game.snapshots::add);
        try (ConsoleRedirect redirect = new ConsoleRedirect(bridge)) {
            CompletableFuture<String> result = CompletableFuture.supplyAsync(() -> play(bridge));

            GameSnapshot first = game.next();
            assertEquals(1, first.page());
            assertEquals(3, first.totalPages());
            assertNotNull(first.entryFor('>'));
            assertNull(first.entryFor('<'));
            assertEquals("does north", first.entryFor('8').text());
            // The hotkey action takes one of the 26 slots, so a..y are things 0..24.
            assertEquals("does thing 0", first.entryFor('a').text());

            bridge.submit('>');
            GameSnapshot second = game.next();
            assertEquals(2, second.page());
            assertNotNull(second.entryFor('<'));
            assertEquals("does thing 25", second.entryFor('a').text());

            bridge.submit('b');
            assertEquals("thing 26", result.get(10, TimeUnit.SECONDS));
        }
        assertSame(out, System.out);
        assertSame(in, System.in);
    }

    @Test
    void movementKeyStillWorksFromALaterPage() throws Exception {
        FakeGame game = new FakeGame();
        GameBridge bridge = new GameBridge();
        bridge.connect(game, game.snapshots::add);
        try (ConsoleRedirect redirect = new ConsoleRedirect(bridge)) {
            CompletableFuture<String> result = CompletableFuture.supplyAsync(() -> play(bridge));

            game.next();
            bridge.submit('>');
            GameSnapshot second = game.next();
            assertEquals(2, second.page());
            assertNull(second.entryFor('8'));

            // The bridge turns back to the first page and then presses 8,
            // exactly as a console player would.
            bridge.submit('8');
            assertEquals("north", result.get(10, TimeUnit.SECONDS));
        }
    }
}
