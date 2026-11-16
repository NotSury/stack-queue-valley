package game.gui;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.StackQueueValleyWorld;
import game.capabilities.FarmerStatistic;
import game.gui.GameSnapshot.InventoryItem;
import game.gui.GameSnapshot.MenuEntry;
import game.gui.GameSnapshot.PlayerView;
import game.gui.GameSnapshot.TileView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The real Stack Queue Valley world, with the ability to describe itself to a
 * graphical interface.
 *
 * <p>This class adds no gameplay. It inherits the world unchanged and only
 * reads the maps, the player and the items that the engine already tracks, so
 * the window shows actual game state instead of parsed console text.</p>
 */
public final class GuiWorld extends StackQueueValleyWorld implements GameBridge.StateReader {

    private final SpriteCatalog catalog;
    private GameMap lastMap;
    private PlayerView lastPlayer;

    /**
     * Creates the world.
     *
     * @param display the display the engine will print to and read from
     * @param catalog chooses the sprite for each visible thing
     */
    public GuiWorld(Display display, SpriteCatalog catalog) {
        super(display);
        this.catalog = Objects.requireNonNull(catalog);
    }

    @Override
    public String playerLabel() {
        return players.isEmpty() ? "" : players.get(0).toString();
    }

    @Override
    public GameSnapshot read(List<MenuEntry> menu, int page, int totalPages, List<String> events,
                             boolean awaitingInput, boolean gameOver, int turn) {
        Actor player = players.isEmpty() ? null : players.get(0);
        Location here = player != null && actorLocations.contains(player)
                ? actorLocations.locationOf(player)
                : null;

        GameMap map = here != null ? here.map() : lastMap;
        if (map == null && !gameMaps.isEmpty()) {
            map = gameMaps.get(0);
        }
        lastMap = map;

        if (here != null) {
            lastPlayer = describePlayer(player, here.x(), here.y());
        } else if (player != null && lastPlayer != null) {
            // The player has left the map (the game is over): keep the last
            // position but show the final statistics.
            lastPlayer = describePlayer(player, lastPlayer.x(), lastPlayer.y());
        }

        List<Integer> xs = new ArrayList<>();
        List<Integer> ys = new ArrayList<>();
        if (map != null) {
            map.getXRange().forEach(xs::add);
            map.getYRange().forEach(ys::add);
        }
        TileView[][] tiles = new TileView[xs.size()][ys.size()];
        for (int column = 0; column < xs.size(); column++) {
            for (int row = 0; row < ys.size(); row++) {
                tiles[column][row] = describeTile(map.at(xs.get(column), ys.get(row)), player);
            }
        }

        return new GameSnapshot(
                map == null ? "" : map.toString(),
                xs.size(),
                ys.size(),
                tiles,
                lastPlayer,
                menu,
                page,
                totalPages,
                events,
                awaitingInput,
                gameOver,
                turn);
    }

    /**
     * Describes one tile the way the console would show it: the actor if
     * there is one, otherwise the top visible item, otherwise the ground.
     * The ground is always included so the map has no holes, and the item
     * under the player is included because the player may pick it up.
     */
    private TileView describeTile(Location location, Actor player) {
        Ground ground = location.getGround();
        String groundSprite = catalog.ground(ground.toString(), ground.getDisplayChar());

        String actorSprite = null;
        String itemSprite = null;
        boolean isPlayer = false;
        boolean occupied = location.containsAnActor();

        if (occupied) {
            Actor actor = location.getActor();
            isPlayer = actor == player;
            SpriteCatalog.ActorLook look = catalog.actor(actor.toString(), actor.getDisplayChar());
            if (look.disguised()) {
                itemSprite = look.sprite();
            } else {
                actorSprite = look.sprite();
            }
        }

        if (!occupied || isPlayer) {
            List<Item> items = location.getItems();
            for (int index = items.size() - 1; index >= 0 && itemSprite == null; index--) {
                Item item = items.get(index);
                itemSprite = catalog.itemOnMap(item.toString(), item.getDisplayChar());
            }
        }

        return new TileView(groundSprite, itemSprite, actorSprite, isPlayer);
    }

    private PlayerView describePlayer(Actor player, int x, int y) {
        List<InventoryItem> inventory = new ArrayList<>();
        for (Item item : player.getInventory().getItems()) {
            String name = item.toString();
            inventory.add(new InventoryItem(name, catalog.itemInInventory(name, item.getDisplayChar())));
        }

        List<String> statuses = new ArrayList<>();
        for (Status status : player.statuses()) {
            statuses.add(statusName(status));
        }

        String label = player.toString();
        int cut = label.lastIndexOf(" (");
        String name = cut > 0 ? label.substring(0, cut) : label;

        return new PlayerView(
                name,
                statistic(player, ActorStatistics.HEALTH, false),
                statistic(player, ActorStatistics.HEALTH, true),
                statistic(player, ActorStatistics.STAMINA, false),
                statistic(player, ActorStatistics.STAMINA, true),
                statistic(player, FarmerStatistic.COINS, false),
                List.copyOf(inventory),
                List.copyOf(statuses),
                x,
                y);
    }

    private static int statistic(Actor actor, Enum<?> name, boolean maximum) {
        if (!actor.hasStatistic(name)) {
            return 0;
        }
        return maximum ? actor.getMaximumStatistic(name) : actor.getStatistic(name);
    }

    /**
     * Names a status effect for the status bar. Statuses that describe
     * themselves are shown as written; the rest are named after their class.
     */
    private static String statusName(Status status) {
        String text = String.valueOf(status);
        if (!text.contains("@")) {
            return text;
        }
        String simple = status.getClass().getSimpleName().replaceFirst("Status$", "");
        return simple.replaceAll("(?<=[a-z])(?=[A-Z])", " ");
    }
}
