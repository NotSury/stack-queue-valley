package game.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests the generated pixel artwork.
 */
class SpriteSheetTest {

    private final SpriteSheet sheet = new SpriteSheet();

    @Test
    void everyHandDrawnGridIsSixteenBySixteenPaletteCharacters() {
        for (Map.Entry<String, String> entry : sheet.grids().entrySet()) {
            String[] rows = entry.getValue().strip().lines().map(String::strip).toArray(String[]::new);
            assertEquals(PixelArt.SIZE, rows.length, entry.getKey() + " row count");
            for (int row = 0; row < rows.length; row++) {
                assertEquals(PixelArt.SIZE, rows[row].length(), entry.getKey() + " row " + (row + 1));
                for (char key : rows[row].toCharArray()) {
                    assertTrue(PixelArt.isPaletteKey(key), entry.getKey() + " uses unknown colour " + key);
                }
            }
        }
    }

    @Test
    void everySpriteTheCatalogCanChooseHasArtwork() {
        for (String id : new SpriteCatalog().spriteIds()) {
            BufferedImage sprite = sheet.get(id);
            assertNotNull(sprite, "missing artwork for " + id);
            assertEquals(PixelArt.SIZE, sprite.getWidth());
            assertEquals(PixelArt.SIZE, sprite.getHeight());
        }
    }

    @Test
    void farmAndMinesHaveDifferentFloors() {
        assertNotEquals(sheet.floor(false, 0, 0).getRGB(0, 0), sheet.floor(true, 0, 0).getRGB(0, 0));
        assertNotEquals(sheet.backdrop(false, 0, 0).getRGB(0, 0), sheet.backdrop(true, 0, 0).getRGB(0, 0));
    }

    @Test
    void floorsExistForEveryTile() {
        for (int x = -3; x < 30; x++) {
            for (int y = -3; y < 12; y++) {
                assertNotNull(sheet.floor(false, x, y));
                assertNotNull(sheet.floor(true, x, y));
                assertNotNull(sheet.backdrop(false, x, y));
                assertNotNull(sheet.backdrop(true, x, y));
            }
        }
    }
}
