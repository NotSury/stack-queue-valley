package game.gui;

import game.gui.GameSnapshot.InventoryItem;
import game.gui.GameSnapshot.MenuEntry;
import game.gui.GameSnapshot.PlayerView;
import game.gui.GameSnapshot.TileView;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Paints the whole game screen onto a fixed 1280 by 720 canvas.
 *
 * <p>The renderer draws every panel itself instead of using stock Swing
 * widgets, which is what gives the window its own look. It only reads a
 * {@link UiState}; it never touches the game. While painting it records the
 * clickable regions so the canvas can turn mouse clicks into the same keys the
 * keyboard sends.</p>
 */
final class GameRenderer {

    /** Canvas width in logical pixels. */
    static final int WIDTH = 1280;
    /** Canvas height in logical pixels. */
    static final int HEIGHT = 720;

    /**
     * A clickable region.
     *
     * @param bounds the region in canvas coordinates
     * @param key    the key a click sends to the game
     */
    record Hit(Rectangle bounds, char key) {
    }

    private static final Rectangle HUD = new Rectangle(12, 10, 1256, 66);
    private static final Rectangle MAP = new Rectangle(12, 84, 1000, 380);
    private static final Rectangle VIEW = new Rectangle(32, 106, 960, 336);
    private static final Rectangle INVENTORY = new Rectangle(1020, 84, 248, 380);
    private static final Rectangle ACTIONS = new Rectangle(12, 472, 780, 238);
    private static final Rectangle LOG = new Rectangle(800, 472, 468, 238);

    private static final int TITLE_HEIGHT = 24;
    private static final int FRAME = 6;
    private static final int ROW_HEIGHT = 22;
    private static final int LOG_LINE_HEIGHT = 16;
    private static final int SLOT = 52;
    private static final int SLOT_GAP = 4;
    private static final int SLOT_COLUMNS = 4;

    private static final Color INK = new Color(0x3b2a1a);
    private static final Color INK_SOFT = new Color(0x7a6248);
    private static final Color PARCHMENT = new Color(0xf2e3bd);
    private static final Color PARCHMENT_DARK = new Color(0xe2cf9c);
    private static final Color WOOD = new Color(0xa8703a);
    private static final Color WOOD_LIGHT = new Color(0xd9a05b);
    private static final Color WOOD_DARK = new Color(0x7a4c25);
    private static final Color WOOD_TITLE = new Color(0x8b5a2b);
    private static final Color OUTLINE = new Color(0x3a2416);
    private static final Color CREAM = new Color(0xfff3d6);

    private static final Pattern DANGER = Pattern.compile(
            "(?i)attack|damage|hurt|burn|explod|poison|shock|dies|killed|unconscious|falls|bites|slams|strikes|steal");
    private static final Pattern GROWTH = Pattern.compile(
            "(?i)grows|drops a|spreads|harvest|plants|tills|waters|cook|heals|restores");

    private final SpriteSheet sheet;
    private final Font small;
    private final Font body;
    private final Font bold;
    private final Font heading;
    private final Font large;
    private final Font huge;

    private final List<Hit> hits = new ArrayList<>();
    private int maxLogScroll;
    private int maxMenuScroll;

    GameRenderer(SpriteSheet sheet) {
        this.sheet = sheet;
        String family = pickFamily();
        small = new Font(family, Font.PLAIN, 11);
        body = new Font(family, Font.PLAIN, 13);
        bold = new Font(family, Font.BOLD, 13);
        heading = new Font(family, Font.BOLD, 14);
        large = new Font(family, Font.BOLD, 18);
        huge = new Font(family, Font.BOLD, 54);
    }

    private static String pickFamily() {
        try {
            List<String> families = Arrays.asList(
                    GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
            for (String candidate : new String[] {"Consolas", "DejaVu Sans Mono", "Menlo"}) {
                if (families.contains(candidate)) {
                    return candidate;
                }
            }
        } catch (RuntimeException | Error unavailable) {
            // Fall through to the logical font, which always exists.
        }
        return Font.MONOSPACED;
    }

    /**
     * Returns the regions that were clickable in the last paint.
     *
     * @return clickable regions, topmost last
     */
    List<Hit> hits() {
        return hits;
    }

    /**
     * Returns the area of the event log, for routing the mouse wheel.
     *
     * @return the log panel bounds
     */
    Rectangle logArea() {
        return LOG;
    }

    /**
     * Returns the area of the action menu, for routing the mouse wheel.
     *
     * @return the action panel bounds
     */
    Rectangle actionsArea() {
        return ACTIONS;
    }

    /**
     * Returns how far the log could be scrolled in the last paint.
     *
     * @return the largest useful log scroll value
     */
    int maxLogScroll() {
        return maxLogScroll;
    }

    /**
     * Returns how far the action list could be scrolled in the last paint.
     *
     * @return the largest useful menu scroll value
     */
    int maxMenuScroll() {
        return maxMenuScroll;
    }

    /**
     * Paints one frame.
     *
     * @param g     graphics already scaled to the logical canvas
     * @param state what to show
     */
    void paint(Graphics2D g, UiState state) {
        hits.clear();
        g.setPaint(new GradientPaint(0, 0, new Color(0x3d2c3f), 0, HEIGHT, new Color(0x1f1826)));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        GameSnapshot snapshot = state.snapshot;
        paintHud(g, state);
        paintMap(g, state);
        paintInventory(g, state);
        paintActions(g, state);
        paintLog(g, state);

        if (snapshot == null) {
            paintBanner(g, "Stack Queue Valley", state.error == null ? "Loading the valley..." : state.error);
        } else if (state.error != null) {
            paintBanner(g, "The game stopped", state.error);
        } else if (snapshot.gameOver()) {
            paintBanner(g, "Game Over", "Close the window to leave the valley.");
        }
    }

    // ------------------------------------------------------------------
    // Status bar
    // ------------------------------------------------------------------

    private void paintHud(Graphics2D g, UiState state) {
        panel(g, HUD, null);
        GameSnapshot snapshot = state.snapshot;
        PlayerView player = snapshot == null ? null : snapshot.player();
        int x = HUD.x;
        int y = HUD.y;

        g.setColor(new Color(0x8ecf6a));
        g.fillRect(x + 12, y + 9, 48, 48);
        sprite(g, "actor.farmer", x + 12, y + 9, 48);
        g.setColor(OUTLINE);
        g.setStroke(new BasicStroke(2f));
        g.drawRect(x + 12, y + 9, 48, 48);
        g.setStroke(new BasicStroke(1f));

        String name = player == null ? "Stack Queue Valley" : player.name();
        text(g, name, x + 72, y + 29, large, INK);
        String place = snapshot == null ? "" : snapshot.mapName() + "  -  Turn " + snapshot.turn();
        text(g, place, x + 72, y + 49, body, INK_SOFT);

        int barX = x + 300;
        sprite(g, "icon.heart", barX, y + 8, 22);
        sprite(g, "icon.bolt", barX, y + 36, 22);
        if (player != null) {
            bar(g, barX + 28, y + 10, 230, 18, player.hp(), player.maxHp(),
                    new Color(0xe0483e), new Color(0xa32a35), "HP");
            bar(g, barX + 28, y + 38, 230, 18, player.sp(), player.maxSp(),
                    new Color(0x6fc04a), new Color(0x3f8a34), "SP");
            sprite(g, "icon.coin", x + 580, y + 17, 32);
            text(g, String.valueOf(player.coins()), x + 618, y + 41, large, INK);

            int pillX = x + 720;
            int limit = x + HUD.width - 170;
            List<String> statuses = player.statuses();
            for (int index = 0; index < statuses.size(); index++) {
                String label = statuses.get(index);
                int width = g.getFontMetrics(small).stringWidth(label) + 16;
                if (pillX + width > limit) {
                    text(g, "+" + (statuses.size() - index), pillX, y + 38, bold, INK_SOFT);
                    break;
                }
                pill(g, pillX, y + 22, width, label, new Color(0x7a4fb0), small);
                pillX += width + 6;
            }
        }

        String turnLabel;
        Color turnColor;
        if (snapshot == null) {
            turnLabel = "LOADING";
            turnColor = new Color(0x8b6f47);
        } else if (snapshot.gameOver()) {
            turnLabel = "GAME OVER";
            turnColor = new Color(0xa32a35);
        } else if (snapshot.awaitingInput()) {
            turnLabel = "YOUR TURN";
            turnColor = new Color(0x3f9b4a);
        } else {
            turnLabel = "WORKING" + ".".repeat((int) (state.tick % 4));
            turnColor = new Color(0xc98a1e);
        }
        pill(g, x + HUD.width - 150, y + 20, 134, turnLabel, turnColor, bold);
    }

    private void bar(Graphics2D g, int x, int y, int width, int height, int value, int maximum,
                     Color light, Color dark, String label) {
        g.setColor(OUTLINE);
        g.fillRect(x - 2, y - 2, width + 4, height + 4);
        g.setColor(new Color(0x5a3a2a));
        g.fillRect(x, y, width, height);
        int filled = maximum <= 0 ? 0 : (int) Math.round(width * Math.max(0, Math.min(1.0, value / (double) maximum)));
        g.setColor(dark);
        g.fillRect(x, y, filled, height);
        g.setColor(light);
        g.fillRect(x, y, filled, height - 6);
        g.setColor(new Color(255, 255, 255, 70));
        g.fillRect(x, y + 2, filled, 3);
        String caption = label + " " + value + "/" + maximum;
        FontMetrics metrics = g.getFontMetrics(bold);
        int textX = x + (width - metrics.stringWidth(caption)) / 2;
        text(g, caption, textX + 1, y + height - 4, bold, new Color(0, 0, 0, 150));
        text(g, caption, textX, y + height - 5, bold, Color.WHITE);
    }

    private void pill(Graphics2D g, int x, int y, int width, String label, Color color, Font font) {
        g.setColor(OUTLINE);
        g.fillRoundRect(x - 2, y - 2, width + 4, 26, 10, 10);
        g.setColor(color);
        g.fillRoundRect(x, y, width, 22, 8, 8);
        g.setColor(new Color(255, 255, 255, 50));
        g.fillRoundRect(x + 2, y + 2, width - 4, 8, 6, 6);
        FontMetrics metrics = g.getFontMetrics(font);
        text(g, label, x + (width - metrics.stringWidth(label)) / 2, y + 16, font, Color.WHITE);
    }

    // ------------------------------------------------------------------
    // Map
    // ------------------------------------------------------------------

    private void paintMap(Graphics2D g, UiState state) {
        panel(g, MAP, null);
        g.setColor(OUTLINE);
        g.fillRect(VIEW.x - 3, VIEW.y - 3, VIEW.width + 6, VIEW.height + 6);

        GameSnapshot snapshot = state.snapshot;
        if (snapshot == null || snapshot.width() == 0 || snapshot.height() == 0) {
            g.setColor(new Color(0x2c2738));
            g.fill(VIEW);
            return;
        }

        boolean mines = isMines(snapshot.mapName());
        int scale = Math.max(1, Math.min(4, Math.min(
                VIEW.width / (snapshot.width() * PixelArt.SIZE),
                VIEW.height / (snapshot.height() * PixelArt.SIZE))));
        int tile = PixelArt.SIZE * scale;
        int originX = VIEW.x + (VIEW.width - snapshot.width() * tile) / 2;
        int originY = VIEW.y + (VIEW.height - snapshot.height() * tile) / 2;

        Shape oldClip = g.getClip();
        g.clip(VIEW);

        int firstColumn = -((originX - VIEW.x) / tile) - 1;
        int firstRow = -((originY - VIEW.y) / tile) - 1;
        for (int column = firstColumn; originX + column * tile < VIEW.x + VIEW.width; column++) {
            for (int row = firstRow; originY + row * tile < VIEW.y + VIEW.height; row++) {
                boolean inside = column >= 0 && row >= 0 && column < snapshot.width() && row < snapshot.height();
                if (!inside) {
                    g.drawImage(sheet.backdrop(mines, column, row),
                            originX + column * tile, originY + row * tile, tile, tile, null);
                }
            }
        }
        if (originX > VIEW.x || originY > VIEW.y) {
            g.setColor(new Color(0, 0, 0, 90));
            g.fillRect(originX - 4, originY - 4, snapshot.width() * tile + 8, snapshot.height() * tile + 8);
        }

        for (int x = 0; x < snapshot.width(); x++) {
            for (int y = 0; y < snapshot.height(); y++) {
                TileView view = snapshot.tiles()[x][y];
                int px = originX + x * tile;
                int py = originY + y * tile;
                g.drawImage(sheet.floor(mines, x, y), px, py, tile, tile, null);
                sprite(g, view.ground(), px, py, tile);
            }
        }

        g.setColor(new Color(0, 0, 0, 22));
        for (int x = 1; x < snapshot.width(); x++) {
            g.fillRect(originX + x * tile, originY, 1, snapshot.height() * tile);
        }
        for (int y = 1; y < snapshot.height(); y++) {
            g.fillRect(originX, originY + y * tile, snapshot.width() * tile, 1);
        }

        PlayerView player = snapshot.player();
        if (snapshot.awaitingInput() && player != null && player.x() >= 0) {
            paintMoveHints(g, state, originX, originY, tile);
        }

        for (int y = 0; y < snapshot.height(); y++) {
            for (int x = 0; x < snapshot.width(); x++) {
                TileView view = snapshot.tiles()[x][y];
                int px = originX + x * tile;
                int py = originY + y * tile;
                if (view.item() != null) {
                    sprite(g, view.item(), px, py, tile);
                }
                if (view.actor() != null) {
                    int bob = (state.tick + x + y) % 2 == 0 ? 0 : -scale;
                    g.setColor(new Color(0, 0, 0, 60));
                    g.fillOval(px + tile / 5, py + tile - tile / 5, tile * 3 / 5, tile / 6);
                    sprite(g, view.actor(), px, py + bob, tile);
                }
                if (view.player()) {
                    paintPlayerMarker(g, state, px, py, tile);
                }
            }
        }

        g.setClip(oldClip);
    }

    private static boolean isMines(String mapName) {
        String lower = mapName.toLowerCase();
        return lower.contains("mine") || lower.contains("cave");
    }

    private void paintMoveHints(Graphics2D g, UiState state, int originX, int originY, int tile) {
        GameSnapshot snapshot = state.snapshot;
        PlayerView player = snapshot.player();
        for (MenuEntry entry : snapshot.menu()) {
            if (!entry.isMove()) {
                continue;
            }
            int[] step = direction(entry.key());
            int x = player.x() + step[0];
            int y = player.y() + step[1];
            if (x < 0 || y < 0 || x >= snapshot.width() || y >= snapshot.height()) {
                continue;
            }
            Rectangle bounds = new Rectangle(originX + x * tile, originY + y * tile, tile, tile);
            boolean hover = hovered(state, bounds);
            g.setColor(new Color(255, 255, 255, hover ? 110 : 34));
            g.fillRect(bounds.x + 2, bounds.y + 2, tile - 4, tile - 4);
            if (hover) {
                g.setColor(new Color(255, 243, 214, 230));
                g.setStroke(new BasicStroke(2f));
                g.drawRect(bounds.x + 2, bounds.y + 2, tile - 4, tile - 4);
                g.setStroke(new BasicStroke(1f));
            }
            hits.add(new Hit(bounds, entry.key()));
        }
    }

    private void paintPlayerMarker(Graphics2D g, UiState state, int px, int py, int tile) {
        int alpha = 150 + (int) (90 * Math.sin(state.tick * 0.9));
        g.setColor(new Color(255, 232, 120, Math.max(40, Math.min(255, alpha))));
        int arm = tile / 4;
        int thick = Math.max(2, tile / 16);
        int right = px + tile - thick;
        int bottom = py + tile - thick;
        g.fillRect(px, py, arm, thick);
        g.fillRect(px, py, thick, arm);
        g.fillRect(px + tile - arm, py, arm, thick);
        g.fillRect(right, py, thick, arm);
        g.fillRect(px, bottom, arm, thick);
        g.fillRect(px, py + tile - arm, thick, arm);
        g.fillRect(px + tile - arm, bottom, arm, thick);
        g.fillRect(right, py + tile - arm, thick, arm);
    }

    /**
     * Returns the map step for a movement key, using the numeric keypad
     * layout the engine assigns to the eight directions.
     *
     * @param key a key from 1 to 9
     * @return the change in x and y
     */
    static int[] direction(char key) {
        return switch (key) {
            case '8' -> new int[] {0, -1};
            case '9' -> new int[] {1, -1};
            case '6' -> new int[] {1, 0};
            case '3' -> new int[] {1, 1};
            case '2' -> new int[] {0, 1};
            case '1' -> new int[] {-1, 1};
            case '4' -> new int[] {-1, 0};
            case '7' -> new int[] {-1, -1};
            default -> new int[] {0, 0};
        };
    }

    // ------------------------------------------------------------------
    // Inventory
    // ------------------------------------------------------------------

    private void paintInventory(Graphics2D g, UiState state) {
        PlayerView player = state.snapshot == null ? null : state.snapshot.player();
        int count = player == null ? 0 : player.inventory().size();
        panel(g, INVENTORY, "Inventory (" + count + ")");
        if (player == null) {
            return;
        }

        Map<String, Integer> counts = new LinkedHashMap<>();
        Map<String, String> spritesByName = new LinkedHashMap<>();
        for (InventoryItem item : player.inventory()) {
            counts.merge(item.name(), 1, Integer::sum);
            spritesByName.putIfAbsent(item.name(), item.sprite());
        }

        int left = INVENTORY.x + (INVENTORY.width - (SLOT_COLUMNS * SLOT + (SLOT_COLUMNS - 1) * SLOT_GAP)) / 2;
        int top = INVENTORY.y + FRAME + TITLE_HEIGHT + 10;
        int footer = INVENTORY.y + INVENTORY.height - 34;
        int rows = Math.max(1, (footer - top) / (SLOT + SLOT_GAP));
        int capacity = rows * SLOT_COLUMNS;

        String hoverName = null;
        int index = 0;
        List<String> names = new ArrayList<>(counts.keySet());
        for (int slot = 0; slot < capacity; slot++) {
            int x = left + (slot % SLOT_COLUMNS) * (SLOT + SLOT_GAP);
            int y = top + (slot / SLOT_COLUMNS) * (SLOT + SLOT_GAP);
            Rectangle bounds = new Rectangle(x, y, SLOT, SLOT);
            boolean filled = index < names.size();
            boolean hover = filled && hovered(state, bounds);
            g.setColor(OUTLINE);
            g.fillRect(x, y, SLOT, SLOT);
            g.setColor(hover ? new Color(0xfff3d6) : PARCHMENT_DARK);
            g.fillRect(x + 2, y + 2, SLOT - 4, SLOT - 4);
            g.setColor(new Color(0, 0, 0, 28));
            g.fillRect(x + 2, y + 2, SLOT - 4, 3);
            if (filled) {
                String name = names.get(index);
                boolean last = slot == capacity - 1 && names.size() > capacity;
                if (last) {
                    String more = "+" + (names.size() - capacity + 1);
                    FontMetrics metrics = g.getFontMetrics(large);
                    text(g, more, x + (SLOT - metrics.stringWidth(more)) / 2, y + 33, large, INK);
                } else {
                    sprite(g, spritesByName.get(name), x + 2, y + 2, SLOT - 4);
                    int stack = counts.get(name);
                    if (stack > 1) {
                        String label = String.valueOf(stack);
                        FontMetrics metrics = g.getFontMetrics(bold);
                        int width = metrics.stringWidth(label) + 6;
                        g.setColor(OUTLINE);
                        g.fillRect(x + SLOT - width - 2, y + SLOT - 17, width, 15);
                        text(g, label, x + SLOT - width + 1, y + SLOT - 5, bold, CREAM);
                    }
                    if (hover) {
                        hoverName = stack > 1 ? name + " x" + stack : name;
                    }
                }
                index++;
            }
        }

        String caption = hoverName != null ? hoverName : (names.isEmpty() ? "Nothing carried" : "Hover an item for its name");
        text(g, fit(g, caption, body, INVENTORY.width - 28), INVENTORY.x + 14, INVENTORY.y + INVENTORY.height - 16,
                hoverName != null ? bold : body, hoverName != null ? INK : INK_SOFT);
    }

    // ------------------------------------------------------------------
    // Action menu
    // ------------------------------------------------------------------

    private void paintActions(Graphics2D g, UiState state) {
        GameSnapshot snapshot = state.snapshot;
        String title = "Actions";
        if (snapshot != null && snapshot.totalPages() > 1) {
            title += "  -  page " + snapshot.page() + " of " + snapshot.totalPages();
        }
        panel(g, ACTIONS, title);

        int contentTop = ACTIONS.y + FRAME + TITLE_HEIGHT;
        int detailY = ACTIONS.y + ACTIONS.height - 14;
        String detail = "Arrow keys or numpad move.  Letter keys act.  Space waits.";
        Font detailFont = body;

        List<MenuEntry> menu = snapshot == null ? List.of() : snapshot.menu();
        boolean active = snapshot != null && snapshot.awaitingInput();

        // Page buttons live in the title bar.
        int buttonX = ACTIONS.x + ACTIONS.width - 16;
        for (char pageKey : new char[] {'>', '<'}) {
            MenuEntry entry = snapshot == null ? null : snapshot.entryFor(pageKey);
            if (entry != null) {
                String label = pageKey == '>' ? "Next >" : "< Prev";
                int width = g.getFontMetrics(bold).stringWidth(label) + 14;
                buttonX -= width;
                Rectangle bounds = new Rectangle(buttonX, ACTIONS.y + FRAME + 3, width, TITLE_HEIGHT - 6);
                boolean hover = hovered(state, bounds);
                g.setColor(hover ? CREAM : WOOD_LIGHT);
                g.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 6, 6);
                text(g, label, bounds.x + 7, bounds.y + 14, bold, OUTLINE);
                hits.add(new Hit(bounds, pageKey));
                buttonX -= 6;
                if (hover) {
                    detail = "[" + pageKey + "]  " + entry.text();
                    detailFont = bold;
                }
            }
        }

        // Direction pad.
        int padX = ACTIONS.x + 18;
        int padY = contentTop + 12;
        int button = 44;
        int gap = 4;
        char[][] layout = {{'7', '8', '9'}, {'4', '5', '6'}, {'1', '2', '3'}};
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                char key = layout[row][column];
                MenuEntry entry = snapshot == null ? null : snapshot.entryFor(key);
                boolean usable = active && entry != null;
                Rectangle bounds = new Rectangle(padX + column * (button + gap), padY + row * (button + gap),
                        button, button);
                boolean hover = usable && hovered(state, bounds);
                padButton(g, bounds, key, usable, hover);
                if (usable) {
                    hits.add(new Hit(bounds, key));
                    if (hover) {
                        detail = "[" + key + "]  " + entry.text();
                        detailFont = bold;
                    }
                }
            }
        }

        // Everything that is not movement, waiting or paging.
        List<MenuEntry> list = new ArrayList<>();
        for (MenuEntry entry : menu) {
            if (!entry.isMove() && !entry.isPaging() && entry.key() != '5') {
                list.add(entry);
            }
        }
        int listX = padX + 3 * button + 2 * gap + 22;
        int listTop = contentTop + 8;
        int listWidth = ACTIONS.x + ACTIONS.width - 18 - listX;
        int rows = Math.max(1, (detailY - 18 - listTop) / ROW_HEIGHT);
        int columns = list.size() > rows ? 2 : 1;
        int columnWidth = (listWidth - (columns - 1) * 12) / columns;
        int neededRows = (list.size() + columns - 1) / columns;
        maxMenuScroll = Math.max(0, neededRows - rows);
        int scroll = Math.max(0, Math.min(state.menuScroll, maxMenuScroll));

        if (list.isEmpty() && snapshot != null && !snapshot.gameOver()) {
            text(g, active ? "No other actions here." : "Waiting for the valley...",
                    listX, listTop + 16, body, INK_SOFT);
        }
        for (int index = 0; index < list.size(); index++) {
            int column = index / neededRowsOrOne(neededRows);
            int row = index % neededRowsOrOne(neededRows) - scroll;
            if (row < 0 || row >= rows) {
                continue;
            }
            MenuEntry entry = list.get(index);
            Rectangle bounds = new Rectangle(listX + column * (columnWidth + 12), listTop + row * ROW_HEIGHT,
                    columnWidth, ROW_HEIGHT - 2);
            boolean hover = active && hovered(state, bounds);
            if (hover) {
                g.setColor(new Color(0xfff8e4));
                g.fillRoundRect(bounds.x - 2, bounds.y, bounds.width + 4, bounds.height, 6, 6);
                g.setColor(WOOD);
                g.drawRoundRect(bounds.x - 2, bounds.y, bounds.width + 4, bounds.height, 6, 6);
                detail = "[" + entry.key() + "]  " + entry.text();
                detailFont = bold;
            }
            g.setColor(active ? WOOD_TITLE : new Color(0xb9a27c));
            g.fillRoundRect(bounds.x, bounds.y + 1, 20, 18, 5, 5);
            String keyLabel = String.valueOf(entry.key()).toUpperCase();
            FontMetrics metrics = g.getFontMetrics(bold);
            text(g, keyLabel, bounds.x + (20 - metrics.stringWidth(keyLabel)) / 2, bounds.y + 15, bold, CREAM);
            text(g, fit(g, capitalise(entry.text()), body, columnWidth - 30), bounds.x + 28, bounds.y + 15,
                    body, active ? INK : INK_SOFT);
            if (active) {
                hits.add(new Hit(bounds, entry.key()));
            }
        }
        if (maxMenuScroll > 0) {
            String more = scroll < maxMenuScroll ? "scroll for more" : "end of list";
            FontMetrics metrics = g.getFontMetrics(small);
            text(g, more, ACTIONS.x + ACTIONS.width - 18 - metrics.stringWidth(more), detailY - 20, small, INK_SOFT);
        }

        g.setColor(PARCHMENT_DARK);
        g.fillRect(ACTIONS.x + FRAME + 1, detailY - 15, ACTIONS.width - 2 * FRAME - 2, 22);
        text(g, fit(g, detail, detailFont, ACTIONS.width - 40), ACTIONS.x + 18, detailY, detailFont,
                detailFont == bold ? INK : INK_SOFT);
    }

    private static int neededRowsOrOne(int neededRows) {
        return Math.max(1, neededRows);
    }

    private void padButton(Graphics2D g, Rectangle bounds, char key, boolean usable, boolean hover) {
        g.setColor(OUTLINE);
        g.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 8, 8);
        Color face = !usable ? new Color(0xcdb98e) : hover ? new Color(0xf4c27a) : WOOD_LIGHT;
        g.setColor(usable ? WOOD_DARK : new Color(0xb09a70));
        g.fillRoundRect(bounds.x + 2, bounds.y + 2, bounds.width - 4, bounds.height - 4, 6, 6);
        g.setColor(face);
        g.fillRoundRect(bounds.x + 2, bounds.y + 2, bounds.width - 4, bounds.height - 7, 6, 6);

        Color symbol = usable ? OUTLINE : new Color(0xa8946c);
        int centreX = bounds.x + bounds.width / 2;
        int centreY = bounds.y + bounds.height / 2 - 1;
        g.setColor(symbol);
        if (key == '5') {
            g.fillRect(centreX - 6, centreY - 8, 12, 2);
            g.fillRect(centreX - 6, centreY + 6, 12, 2);
            g.fill(new Polygon(new int[] {centreX - 5, centreX + 5, centreX},
                    new int[] {centreY - 5, centreY - 5, centreY}, 3));
            g.fill(new Polygon(new int[] {centreX - 5, centreX + 5, centreX},
                    new int[] {centreY + 5, centreY + 5, centreY}, 3));
        } else {
            int[] step = direction(key);
            double length = Math.hypot(step[0], step[1]);
            double dx = step[0] / length;
            double dy = step[1] / length;
            // An arrow with a shaft, so diagonal directions cannot be misread.
            double originX = centreX + 2;
            double originY = centreY + 3;
            double tipX = originX + dx * 11;
            double tipY = originY + dy * 11;
            double neckX = originX + dx * 2;
            double neckY = originY + dy * 2;
            double tailX = originX - dx * 10;
            double tailY = originY - dy * 10;
            Path2D arrow = new Path2D.Double();
            arrow.moveTo(tipX, tipY);
            arrow.lineTo(neckX - dy * 7, neckY + dx * 7);
            arrow.lineTo(neckX - dy * 2.5, neckY + dx * 2.5);
            arrow.lineTo(tailX - dy * 2.5, tailY + dx * 2.5);
            arrow.lineTo(tailX + dy * 2.5, tailY - dx * 2.5);
            arrow.lineTo(neckX + dy * 2.5, neckY - dx * 2.5);
            arrow.lineTo(neckX + dy * 7, neckY - dx * 7);
            arrow.closePath();
            g.fill(arrow);
        }
        text(g, String.valueOf(key), bounds.x + 5, bounds.y + 13, small, symbol);
    }

    // ------------------------------------------------------------------
    // Event log
    // ------------------------------------------------------------------

    private void paintLog(Graphics2D g, UiState state) {
        panel(g, LOG, "Event Log");
        int left = LOG.x + 14;
        int top = LOG.y + FRAME + TITLE_HEIGHT + 4;
        int bottom = LOG.y + LOG.height - 10;
        int width = LOG.width - 28;
        int visible = (bottom - top) / LOG_LINE_HEIGHT;
        int currentTurn = state.snapshot == null ? 0 : state.snapshot.turn();
        String playerName = state.snapshot == null || state.snapshot.player() == null
                ? null
                : state.snapshot.player().name();

        record Wrapped(String text, UiState.LogLine source, boolean first) {
        }

        FontMetrics metrics = g.getFontMetrics(body);
        List<Wrapped> lines = new ArrayList<>();
        for (UiState.LogLine line : state.log) {
            if (line.header()) {
                lines.add(new Wrapped(line.text(), line, true));
                continue;
            }
            boolean first = true;
            for (String part : wrap(line.text(), metrics, width - 10)) {
                lines.add(new Wrapped(part, line, first));
                first = false;
            }
        }

        maxLogScroll = Math.max(0, lines.size() - visible);
        int scroll = Math.max(0, Math.min(state.logScroll, maxLogScroll));
        int end = lines.size() - scroll;
        int start = Math.max(0, end - visible);

        if (lines.isEmpty()) {
            text(g, "What happens in the valley appears here.", left, top + 14, body, INK_SOFT);
        }
        int y = top + 12;
        for (int index = start; index < end; index++) {
            Wrapped wrapped = lines.get(index);
            UiState.LogLine source = wrapped.source();
            boolean old = source.turn() < currentTurn;
            if (source.header()) {
                int labelWidth = g.getFontMetrics(bold).stringWidth(wrapped.text());
                g.setColor(new Color(0xc9b282));
                g.fillRect(left, y - 5, width, 1);
                g.setColor(PARCHMENT);
                g.fillRect(left + 8, y - 11, labelWidth + 12, 13);
                text(g, wrapped.text(), left + 14, y, bold, WOOD_TITLE);
            } else {
                Color color = logColor(source.text(), playerName);
                if (old) {
                    color = blend(color, PARCHMENT, 0.45);
                }
                text(g, wrapped.text(), left + (wrapped.first() ? 0 : 10), y, body, color);
            }
            y += LOG_LINE_HEIGHT;
        }

        if (maxLogScroll > 0) {
            int trackTop = top;
            int trackHeight = bottom - top;
            int thumb = Math.max(16, trackHeight * visible / lines.size());
            int thumbY = trackTop + (trackHeight - thumb) * (maxLogScroll - scroll) / maxLogScroll;
            g.setColor(PARCHMENT_DARK);
            g.fillRect(LOG.x + LOG.width - 13, trackTop, 4, trackHeight);
            g.setColor(WOOD);
            g.fillRect(LOG.x + LOG.width - 13, thumbY, 4, thumb);
        }
    }

    private static Color logColor(String line, String playerName) {
        if (line.contains(" state: ")) {
            return new Color(0x7a3fb0);
        }
        if (DANGER.matcher(line).find()) {
            return new Color(0xb02a2a);
        }
        if (playerName != null && line.startsWith(playerName)) {
            return new Color(0x1f5fa8);
        }
        if (GROWTH.matcher(line).find()) {
            return new Color(0x2f7a34);
        }
        return INK;
    }

    // ------------------------------------------------------------------
    // Shared drawing helpers
    // ------------------------------------------------------------------

    private void paintBanner(Graphics2D g, String title, String subtitle) {
        // Only the map is covered, so the log still shows how the game ended.
        g.setColor(new Color(20, 14, 24, 150));
        g.fill(VIEW);
        Rectangle box = new Rectangle(VIEW.x + VIEW.width / 2 - 330, VIEW.y + VIEW.height / 2 - 90, 660, 180);
        panel(g, box, null);
        FontMetrics titleMetrics = g.getFontMetrics(huge);
        text(g, title, box.x + (box.width - titleMetrics.stringWidth(title)) / 2 + 3, box.y + 93, huge, WOOD_DARK);
        text(g, title, box.x + (box.width - titleMetrics.stringWidth(title)) / 2, box.y + 90, huge, new Color(0xa32a35));
        String shown = fit(g, subtitle, heading, box.width - 40);
        FontMetrics subMetrics = g.getFontMetrics(heading);
        text(g, shown, box.x + (box.width - subMetrics.stringWidth(shown)) / 2, box.y + 135, heading, INK);
    }

    private void panel(Graphics2D g, Rectangle bounds, String title) {
        int x = bounds.x;
        int y = bounds.y;
        int width = bounds.width;
        int height = bounds.height;
        g.setColor(new Color(0, 0, 0, 80));
        g.fillRect(x + 3, y + 4, width, height);
        g.setColor(OUTLINE);
        g.fillRect(x, y, width, height);
        g.setColor(WOOD);
        g.fillRect(x + 2, y + 2, width - 4, height - 4);
        g.setColor(WOOD_LIGHT);
        g.fillRect(x + 2, y + 2, width - 4, 2);
        g.fillRect(x + 2, y + 2, 2, height - 4);
        g.setColor(WOOD_DARK);
        g.fillRect(x + 2, y + height - 4, width - 4, 2);
        g.fillRect(x + width - 4, y + 2, 2, height - 4);
        g.setColor(OUTLINE);
        g.fillRect(x + FRAME - 1, y + FRAME - 1, width - 2 * FRAME + 2, height - 2 * FRAME + 2);
        g.setColor(PARCHMENT);
        g.fillRect(x + FRAME, y + FRAME, width - 2 * FRAME, height - 2 * FRAME);
        if (title != null) {
            g.setColor(WOOD_TITLE);
            g.fillRect(x + FRAME, y + FRAME, width - 2 * FRAME, TITLE_HEIGHT);
            g.setColor(new Color(255, 255, 255, 40));
            g.fillRect(x + FRAME, y + FRAME, width - 2 * FRAME, 2);
            g.setColor(new Color(0x5e3a1c));
            g.fillRect(x + FRAME, y + FRAME + TITLE_HEIGHT - 2, width - 2 * FRAME, 2);
            text(g, title, x + FRAME + 11, y + FRAME + 17, heading, new Color(0, 0, 0, 110));
            text(g, title, x + FRAME + 10, y + FRAME + 16, heading, CREAM);
        }
    }

    private void sprite(Graphics2D g, String id, int x, int y, int size) {
        if (id == null) {
            return;
        }
        BufferedImage image = sheet.get(id);
        if (image != null) {
            g.drawImage(image, x, y, size, size, null);
            return;
        }
        // No artwork: show the game's own character on a small tag.
        String glyph = id.startsWith(SpriteCatalog.GLYPH_PREFIX) && id.length() > SpriteCatalog.GLYPH_PREFIX.length()
                ? id.substring(SpriteCatalog.GLYPH_PREFIX.length())
                : "?";
        int inset = size / 6;
        g.setColor(OUTLINE);
        g.fillRoundRect(x + inset, y + inset, size - 2 * inset, size - 2 * inset, 8, 8);
        g.setColor(PARCHMENT);
        g.fillRoundRect(x + inset + 2, y + inset + 2, size - 2 * inset - 4, size - 2 * inset - 4, 6, 6);
        Font font = large.deriveFont(Font.BOLD, Math.max(10f, size * 0.45f));
        FontMetrics metrics = g.getFontMetrics(font);
        text(g, glyph, x + (size - metrics.stringWidth(glyph)) / 2,
                y + (size + metrics.getAscent()) / 2 - 3, font, INK);
    }

    private static void text(Graphics2D g, String value, int x, int y, Font font, Color color) {
        g.setFont(font);
        g.setColor(color);
        g.drawString(value, x, y);
    }

    private static boolean hovered(UiState state, Rectangle bounds) {
        return state.mouse != null && bounds.contains(state.mouse);
    }

    private static String capitalise(String value) {
        return value.isEmpty() ? value : Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    private static Color blend(Color from, Color to, double amount) {
        return new Color(
                (int) Math.round(from.getRed() + (to.getRed() - from.getRed()) * amount),
                (int) Math.round(from.getGreen() + (to.getGreen() - from.getGreen()) * amount),
                (int) Math.round(from.getBlue() + (to.getBlue() - from.getBlue()) * amount));
    }

    /**
     * Shortens text with an ellipsis so it fits a width.
     */
    private static String fit(Graphics2D g, String value, Font font, int maxWidth) {
        FontMetrics metrics = g.getFontMetrics(font);
        if (metrics.stringWidth(value) <= maxWidth) {
            return value;
        }
        String ellipsis = "...";
        int end = value.length();
        while (end > 0 && metrics.stringWidth(value.substring(0, end) + ellipsis) > maxWidth) {
            end--;
        }
        return value.substring(0, end).stripTrailing() + ellipsis;
    }

    /**
     * Breaks text into lines no wider than a width, preferring spaces.
     *
     * @param value    the text to wrap
     * @param metrics  metrics of the font it will be drawn in
     * @param maxWidth the widest a line may be
     * @return the wrapped lines, at least one
     */
    static List<String> wrap(String value, FontMetrics metrics, int maxWidth) {
        List<String> lines = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String word : value.split(" ")) {
            String candidate = current.length() == 0 ? word : current + " " + word;
            if (metrics.stringWidth(candidate) <= maxWidth || current.length() == 0) {
                current.setLength(0);
                current.append(candidate);
            } else {
                lines.add(current.toString());
                current.setLength(0);
                current.append(word);
            }
        }
        lines.add(current.toString());
        return lines;
    }
}
