package game.gui;

import java.util.List;

/**
 * An immutable picture of the game at the moment the player is asked to act.
 *
 * <p>The game thread builds a snapshot while the game is paused, and the Swing
 * thread only ever reads snapshots. Live game objects never cross between the
 * two threads, so the window can repaint at any time without racing the game.</p>
 *
 * <p>A snapshot holds only what the player is allowed to see. Tiles carry sprite
 * identifiers chosen by {@link SpriteCatalog}, which already hides disguised
 * creatures and concealed traps.</p>
 *
 * @param mapName       name of the map the player is on
 * @param width         map width in tiles
 * @param height        map height in tiles
 * @param tiles         tiles indexed as {@code tiles[x][y]}
 * @param player        the player's status, or {@code null} before the first turn
 * @param menu          actions offered on the current menu page
 * @param page          current menu page, starting at 1
 * @param totalPages    number of menu pages
 * @param events        messages printed since the previous snapshot
 * @param awaitingInput whether the game is waiting for the player to choose
 * @param gameOver      whether the game has ended
 * @param turn          number of turns the player has been shown
 */
public record GameSnapshot(
        String mapName,
        int width,
        int height,
        TileView[][] tiles,
        PlayerView player,
        List<MenuEntry> menu,
        int page,
        int totalPages,
        List<String> events,
        boolean awaitingInput,
        boolean gameOver,
        int turn) {

    /**
     * What is visible on one tile, as sprite identifiers.
     *
     * @param ground sprite of the ground, never {@code null}
     * @param item   sprite of the visible item, or {@code null}
     * @param actor  sprite of the visible actor, or {@code null}
     * @param player whether the player stands here
     */
    public record TileView(String ground, String item, String actor, boolean player) {
    }

    /**
     * The player's status.
     *
     * @param name      display name
     * @param hp        current health
     * @param maxHp     maximum health
     * @param sp        current stamina
     * @param maxSp     maximum stamina
     * @param coins     coins carried
     * @param inventory items carried, in inventory order
     * @param statuses  names of active status effects
     * @param x         x coordinate on the map, or -1 when off the map
     * @param y         y coordinate on the map, or -1 when off the map
     */
    public record PlayerView(
            String name,
            int hp,
            int maxHp,
            int sp,
            int maxSp,
            int coins,
            List<InventoryItem> inventory,
            List<String> statuses,
            int x,
            int y) {
    }

    /**
     * One carried item.
     *
     * @param name   the item's name as the game reports it
     * @param sprite sprite identifier
     */
    public record InventoryItem(String name, String sprite) {
    }

    /**
     * One selectable action.
     *
     * @param key  the key the game assigned to the action
     * @param text the action's description, without the player's name
     */
    public record MenuEntry(char key, String text) {

        /**
         * Reports whether this entry is one of the eight movement directions.
         *
         * @return true for a movement action
         */
        public boolean isMove() {
            return key >= '1' && key <= '9' && key != '5' && text.startsWith("moves ");
        }

        /**
         * Reports whether this entry turns the menu page.
         *
         * @return true for the engine's next or previous page entries
         */
        public boolean isPaging() {
            return key == '<' || key == '>';
        }
    }

    /**
     * Finds the menu entry bound to a key.
     *
     * @param key the key to look up
     * @return the entry, or {@code null} when the key is not offered
     */
    public MenuEntry entryFor(char key) {
        for (MenuEntry entry : menu) {
            if (entry.key() == key) {
                return entry;
            }
        }
        return null;
    }
}
