package game.gui;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Small helpers for building 16 by 16 pixel sprites in code.
 *
 * <p>All artwork in the graphical game is original and generated at start-up,
 * either from character grids or from simple drawing rules. Nothing is loaded
 * from image files, so the project carries no third-party assets.</p>
 */
final class PixelArt {

    /** Width and height of every sprite, in pixels. */
    static final int SIZE = 16;

    private static final Map<Character, Integer> PALETTE = new HashMap<>();

    static {
        color('k', 0x1f1a24);
        color('e', 0x3b3046);
        color('w', 0xffffff);
        color('W', 0xd8dde6);
        color('g', 0x9aa3b2);
        color('G', 0x5d6577);
        color('r', 0xe0483e);
        color('R', 0xa32a35);
        color('o', 0xf08a2c);
        color('O', 0xb85a1e);
        color('y', 0xffd84a);
        color('Y', 0xd9a520);
        color('l', 0xa6e05a);
        color('n', 0x5cb53c);
        color('N', 0x2f7a34);
        color('b', 0x9fdcff);
        color('u', 0x4aa3ee);
        color('U', 0x2b62b8);
        color('p', 0xff9bb5);
        color('P', 0xa060d0);
        color('V', 0x5b3a8c);
        color('t', 0xf6c79a);
        color('T', 0xa86a3d);
        color('D', 0x6b4226);
        color('c', 0x6ff0e0);
        color('C', 0x2aa59a);
        color('s', 0xf0deb0);
        color('S', 0xc9a66b);
        color('m', 0xe455c8);
    }

    private PixelArt() {
    }

    private static void color(char key, int rgb) {
        PALETTE.put(key, 0xff000000 | rgb);
    }

    /**
     * Reports whether a character names a palette colour or transparency.
     *
     * @param key the character to check
     * @return true if the character may appear in a sprite grid
     */
    static boolean isPaletteKey(char key) {
        return key == '.' || PALETTE.containsKey(key);
    }

    /**
     * Builds a sprite from a grid of palette characters. A dot is transparent.
     * Short rows and missing rows are treated as transparent.
     *
     * @param grid sixteen lines of sixteen characters
     * @return the sprite
     */
    static BufferedImage fromGrid(String grid) {
        BufferedImage image = blank();
        String[] rows = grid.strip().split("\\R");
        for (int y = 0; y < Math.min(SIZE, rows.length); y++) {
            String row = rows[y].strip();
            for (int x = 0; x < Math.min(SIZE, row.length()); x++) {
                Integer argb = PALETTE.get(row.charAt(x));
                if (argb != null) {
                    image.setRGB(x, y, argb);
                }
            }
        }
        return image;
    }

    /**
     * Creates an empty, fully transparent sprite.
     *
     * @return a blank sprite
     */
    static BufferedImage blank() {
        return new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Copies a sprite, swapping some colours for others.
     *
     * @param source the sprite to copy
     * @param pairs  palette characters in pairs: from, to, from, to
     * @return the recoloured copy
     */
    static BufferedImage recolor(BufferedImage source, int... pairs) {
        BufferedImage image = blank();
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                int argb = source.getRGB(x, y);
                for (int index = 0; index + 1 < pairs.length; index += 2) {
                    if (argb == (0xff000000 | pairs[index])) {
                        argb = 0xff000000 | pairs[index + 1];
                        break;
                    }
                }
                image.setRGB(x, y, argb);
            }
        }
        return image;
    }

    /**
     * Returns the opaque colour a palette character stands for.
     *
     * @param key a palette character
     * @return the colour as RGB
     */
    static int rgb(char key) {
        return PALETTE.get(key) & 0xffffff;
    }

    /**
     * Copies a sprite with its colours shifted towards violet and cyan and a
     * few sparkles added, for the cosmic variants of produce and crops.
     *
     * @param source   the sprite to copy
     * @param keepRows number of rows at the bottom to leave unchanged
     * @return the shifted copy
     */
    static BufferedImage cosmic(BufferedImage source, int keepRows) {
        BufferedImage image = blank();
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                int argb = source.getRGB(x, y);
                if ((argb >>> 24) == 0 || y >= SIZE - keepRows) {
                    image.setRGB(x, y, argb);
                    continue;
                }
                float[] hsb = Color.RGBtoHSB((argb >> 16) & 0xff, (argb >> 8) & 0xff, argb & 0xff, null);
                if (hsb[2] < 0.2f) {
                    image.setRGB(x, y, argb);
                    continue;
                }
                float hue = (hsb[0] + 0.52f) % 1f;
                float saturation = Math.min(1f, hsb[1] * 0.9f + 0.25f);
                image.setRGB(x, y, 0xff000000 | (Color.HSBtoRGB(hue, saturation, hsb[2]) & 0xffffff));
            }
        }
        Random random = new Random(source.getRGB(7, 7) * 31L + keepRows);
        int placed = 0;
        for (int attempt = 0; attempt < 60 && placed < 4; attempt++) {
            int x = 1 + random.nextInt(SIZE - 2);
            int y = 1 + random.nextInt(SIZE - 2 - keepRows);
            if ((image.getRGB(x, y) >>> 24) != 0 && (image.getRGB(x, y) & 0xffffff) != rgb('k')) {
                image.setRGB(x, y, 0xffffffff);
                placed++;
            }
        }
        return image;
    }

    /**
     * Fills a sprite with a base colour and scatters darker and lighter
     * specks over it. The same seed always gives the same tile.
     *
     * @param base   base colour
     * @param dark   colour of dark specks
     * @param light  colour of light specks
     * @param specks number of specks of each kind
     * @param seed   seed for the speck positions
     * @return the tile
     */
    static BufferedImage speckled(int base, int dark, int light, int specks, long seed) {
        BufferedImage image = blank();
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                image.setRGB(x, y, 0xff000000 | base);
            }
        }
        Random random = new Random(seed);
        for (int index = 0; index < specks; index++) {
            image.setRGB(random.nextInt(SIZE), random.nextInt(SIZE), 0xff000000 | dark);
            image.setRGB(random.nextInt(SIZE), random.nextInt(SIZE), 0xff000000 | light);
        }
        return image;
    }

    /**
     * Sets one pixel if it lies inside the sprite.
     *
     * @param image the sprite to draw on
     * @param x     x coordinate
     * @param y     y coordinate
     * @param argb  colour including alpha
     */
    static void plot(BufferedImage image, int x, int y, int argb) {
        if (x >= 0 && y >= 0 && x < SIZE && y < SIZE) {
            image.setRGB(x, y, argb);
        }
    }
}
