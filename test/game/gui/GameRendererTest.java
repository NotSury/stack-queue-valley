package game.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import game.gui.GameSnapshot.InventoryItem;
import game.gui.GameSnapshot.MenuEntry;
import game.gui.GameSnapshot.PlayerView;
import game.gui.GameSnapshot.TileView;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Tests the painting of the game screen and the key translation, without
 * opening a window.
 */
class GameRendererTest {

    private final GameRenderer renderer = new GameRenderer(new SpriteSheet());

    private static GameSnapshot snapshot(boolean awaiting, boolean gameOver) {
        TileView[][] tiles = new TileView[3][3];
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                tiles[x][y] = new TileView("ground.dirt", null, null, false);
            }
        }
        tiles[1][1] = new TileView("ground.soil", "item.parsnip", "actor.farmer", true);
        tiles[2][1] = new TileView("ground.dirt", null, "actor.cow", false);
        tiles[0][0] = new TileView("glyph:?", "item.geode", null, false);
        PlayerView player = new PlayerView("Farmer Bob", 80, 100, 10, 20, 250,
                List.of(new InventoryItem("Hoe", "item.hoe"), new InventoryItem("Egg", "item.egg"),
                        new InventoryItem("Egg", "item.egg")),
                List.of("Burning"), 1, 1);
        List<MenuEntry> menu = new ArrayList<>();
        if (awaiting) {
            menu.add(new MenuEntry('8', "moves North"));
            menu.add(new MenuEntry('5', "does nothing"));
            menu.add(new MenuEntry('>', "View page 2"));
            for (char key = 'a'; key <= 'x'; key++) {
                menu.add(new MenuEntry(key, "drops the item with a very long descriptive name number " + key));
            }
        }
        return new GameSnapshot("Inherited Farm", 3, 3, tiles, player, menu, 1, 2,
                List.of("Farmer Bob (80/100) moves North", "Cow (20/20) attacks Farmer Bob for 20 damage"),
                awaiting, gameOver, 4);
    }

    private BufferedImage paint(UiState state) {
        BufferedImage image = new BufferedImage(GameRenderer.WIDTH, GameRenderer.HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        renderer.paint(graphics, state);
        graphics.dispose();
        return image;
    }

    private Set<Character> hitKeys() {
        Set<Character> keys = new HashSet<>();
        renderer.hits().forEach(hit -> keys.add(hit.key()));
        return keys;
    }

    @Test
    void paintsBeforeTheGameHasStarted() {
        BufferedImage image = paint(new UiState());

        assertTrue(renderer.hits().isEmpty());
        assertNotEquals(image.getRGB(5, 5), image.getRGB(640, 360));
    }

    @Test
    void everyOfferedActionIsClickableWhileAwaitingInput() {
        UiState state = new UiState();
        state.accept(snapshot(true, false));

        paint(state);
        Set<Character> keys = hitKeys();

        assertTrue(keys.contains('8'));
        assertTrue(keys.contains('5'));
        assertTrue(keys.contains('>'));
        assertTrue(keys.contains('a'));
        assertFalse(keys.contains('2'), "a move the game did not offer must not be clickable");
        assertTrue(renderer.maxMenuScroll() > 0, "a long list must be scrollable");
    }

    @Test
    void nothingIsClickableWhileTheGameIsBusyOrOver() {
        UiState state = new UiState();
        state.accept(snapshot(false, false));
        paint(state);
        assertTrue(renderer.hits().isEmpty());

        state.accept(snapshot(false, true));
        paint(state);
        assertTrue(renderer.hits().isEmpty());
    }

    @Test
    void logKeepsEventsUnderATurnHeading() {
        UiState state = new UiState();
        state.accept(snapshot(true, false));

        assertEquals(3, state.log.size());
        assertTrue(state.log.get(0).header());
        assertEquals("Turn 4", state.log.get(0).text());
    }

    @Test
    void arrowsAndKeypadMapToTheEngineMovementKeys() {
        assertEquals('8', GameCanvas.keyForCode(KeyEvent.VK_UP, false));
        assertEquals('2', GameCanvas.keyForCode(KeyEvent.VK_DOWN, false));
        assertEquals('4', GameCanvas.keyForCode(KeyEvent.VK_LEFT, false));
        assertEquals('6', GameCanvas.keyForCode(KeyEvent.VK_RIGHT, false));
        assertEquals('7', GameCanvas.keyForCode(KeyEvent.VK_HOME, true));
        assertEquals(0, GameCanvas.keyForCode(KeyEvent.VK_HOME, false));
        assertEquals(0, GameCanvas.keyForCode(KeyEvent.VK_F1, false));
    }

    @Test
    void typedCharactersMapToEngineKeys() {
        assertEquals('5', GameCanvas.keyForChar(' '));
        assertEquals('a', GameCanvas.keyForChar('A'));
        assertEquals('9', GameCanvas.keyForChar('9'));
        assertEquals('>', GameCanvas.keyForChar('>'));
        assertEquals('<', GameCanvas.keyForChar(','));
        assertEquals(0, GameCanvas.keyForChar('\n'));
    }

    @Test
    void movementKeysFollowTheKeypadLayout() {
        assertEquals(List.of(0, -1), List.of(GameRenderer.direction('8')[0], GameRenderer.direction('8')[1]));
        assertEquals(List.of(1, 1), List.of(GameRenderer.direction('3')[0], GameRenderer.direction('3')[1]));
        assertEquals(List.of(-1, 0), List.of(GameRenderer.direction('4')[0], GameRenderer.direction('4')[1]));
    }
}
