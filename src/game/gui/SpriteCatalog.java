package game.gui;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * Chooses which sprite represents each thing on the map.
 *
 * <p>The console game shows every tile as one character, and some gameplay
 * depends on that: a disguised Cave Mimic prints a Geode's character, and a
 * hidden Spider Web prints the character of whatever lies beneath it. The
 * catalog keeps those secrets. Each known name is registered with the
 * character it normally shows. When a thing currently shows a different
 * character, it is drawn as whatever that character normally means, never as
 * itself.</p>
 *
 * <p>This class has no dependency on Swing or on the game engine, so the rule
 * can be unit tested directly.</p>
 */
public final class SpriteCatalog {

    /** Prefix of the fallback identifier used for things with no artwork. */
    public static final String GLYPH_PREFIX = "glyph:";

    private static final Pattern HEALTH_SUFFIX = Pattern.compile(" \\(-?\\d+/-?\\d+\\)$");

    private record Entry(String id, char symbol) {
    }

    /**
     * How an actor should be drawn.
     *
     * @param sprite    sprite identifier
     * @param disguised true when the actor is posing as an object and must be
     *                  drawn exactly like one
     */
    public record ActorLook(String sprite, boolean disguised) {
    }

    private final Map<String, Entry> actors = new LinkedHashMap<>();
    private final Map<String, Entry> items = new LinkedHashMap<>();
    private final Map<String, Entry> grounds = new LinkedHashMap<>();
    private final Map<Character, String> actorBySymbol = new LinkedHashMap<>();
    private final Map<Character, String> itemBySymbol = new LinkedHashMap<>();
    private final Map<Character, String> groundBySymbol = new LinkedHashMap<>();

    /**
     * Creates the catalog for Stack Queue Valley.
     */
    public SpriteCatalog() {
        actor("Cow", 'C', "actor.cow");
        actor("Chicken", 'c', "actor.chicken");
        actor("Merchant", '$', "actor.merchant");
        actor("Bat", 'b', "actor.bat");
        actor("Slime", 'S', "actor.slime");
        actor("Cave Ghost", 'G', "actor.ghost");
        actor("Nest Spider", 's', "actor.spider");
        actor("Spiderling", 'z', "actor.spiderling");
        actor("Cave Mimic", 'M', "actor.mimic");
        actor("Skeleton Bat", 'B', "actor.skeletonbat");
        actor("Zombie Slime", 'Z', "actor.zombieslime");
        actor("Wraith", 'V', "actor.wraith");
        actor("PrismFox", 'x', "actor.fox");
        actor("SanctuaryDeer", 'd', "actor.deer");
        actor("TimeRabbit", 'r', "actor.rabbit");

        item("Geode", 'G', "item.geode");
        item("Hoe", 'h', "item.hoe");
        item("Watering Can", 'w', "item.wateringcan");
        item("Pickaxe", 'x', "item.pickaxe");
        item("Sword", 't', "item.sword");
        item("Parsnip", 'p', "item.parsnip");
        item("Tomato", 'o', "item.tomato");
        item("Melon", 'm', "item.melon");
        item("Strawberry", 's', "item.strawberry");
        item("Rotten Strawberry", 's', "item.rottenstrawberry");
        item("Cauliflower", 'c', "item.cauliflower");
        item("Egg", '0', "item.egg");
        item("Milk", 'M', "item.milk");
        item("Stone", 's', "item.stone");
        item("Copper Ore", 'O', "item.copperore");
        item("Diamond", 'D', "item.diamond");
        item("Alien Device", 'A', "item.aliendevice");
        item("Fire", 'f', "item.fire");
        item("Spider Web", ':', "item.web");
        item("Jam", 'J', "item.jam");
        item("Omelette", 'e', "item.omelette");
        item("Harvest Feast", 'H', "item.feast");
        item("Meteorite Ore", '*', "item.meteorite");
        item("Starlite Ore", '+', "item.starlite");
        item("Cosmic Parsnip", 'p', "item.cosmicparsnip");
        item("Cosmic Tomato", 't', "item.cosmictomato");
        item("Cosmic Cauliflower", 'u', "item.cosmiccauliflower");
        item("Cosmic Melon", 'm', "item.cosmicmelon");
        item("Cosmic Strawberry", 'x', "item.cosmicstrawberry");
        item("Astral Blessing", '!', "item.astralblessing");
        item("Cosmic Radiation", 'i', "item.radiation");
        item("Gravitational Distortion", 'Q', "item.distortion");

        ground("Dirt", '.', "ground.dirt");
        ground("Soil", ',', "ground.soil");
        ground("Puddle", '~', "ground.puddle");
        ground("Rock", '#', "ground.rock");
        ground("Copper Node", 'N', "ground.coppernode");
        ground("Ladder", 'H', "ground.ladder");
        ground("Teleportation Circle", '*', "ground.circle");
        ground("Preserves Jar", 'J', "ground.preservesjar");
        ground("Drying Rack", 'R', "ground.dryingrack");
        ground("Cooking Station", 'K', "ground.cookingstation");
        ground("Observatory", 'O', "ground.observatory");
        ground("Spider Nest", '&', "ground.nest");
        ground("Parsnip Crop", 'P', "ground.parsnipcrop");
        ground("Tomato Crop", 'T', "ground.tomatocrop");
        ground("Strawberry Crop", 'S', "ground.strawberrycrop");
        ground("Cauliflower Crop", 'F', "ground.cauliflowercrop");
        ground("Melon Crop", 'W', "ground.meloncrop");
        ground("Strawberry Seedling", '\'', "ground.seedling");
        ground("Strawberry Sprout", 'v', "ground.strawberrysprout");
        ground("Cauliflower Sprout", 'y', "ground.cauliflowersprout");
        ground("Melon Sprout", 't', "ground.melonsprout");
        ground("Cosmic Parsnip Crop", 'p', "ground.cosmicparsnipcrop");
        ground("Cosmic Tomato Crop", 't', "ground.cosmictomatocrop");
        ground("Cosmic Cauliflower Crop", 'f', "ground.cosmiccauliflowercrop");
        ground("Cosmic Melon Crop", 'w', "ground.cosmicmeloncrop");
        ground("Cosmic Strawberry Crop", 's', "ground.cosmicstrawberrycrop");
        ground("Starlite Soil", '_', "ground.starlitesoil");
        ground("Impact Crater", '^', "ground.crater");
        ground("Remains", '%', "ground.remains");
        ground("Mire", '=', "ground.mire");
        ground("Blight", ';', "ground.blight");
        ground("Haunted Ground", '?', "ground.haunted");
    }

    private void actor(String name, char symbol, String id) {
        actors.put(name, new Entry(id, symbol));
        actorBySymbol.putIfAbsent(symbol, id);
    }

    private void item(String name, char symbol, String id) {
        items.put(name, new Entry(id, symbol));
        itemBySymbol.putIfAbsent(symbol, id);
    }

    private void ground(String name, char symbol, String id) {
        grounds.put(name, new Entry(id, symbol));
        groundBySymbol.putIfAbsent(symbol, id);
    }

    /**
     * Chooses the sprite for a ground.
     *
     * @param name   the ground's name
     * @param symbol the character it currently shows
     * @return a sprite identifier, never {@code null}
     */
    public String ground(String name, char symbol) {
        Entry entry = grounds.get(name);
        if (entry != null) {
            return entry.id();
        }
        String bySymbol = groundBySymbol.get(symbol);
        return bySymbol != null ? bySymbol : GLYPH_PREFIX + symbol;
    }

    /**
     * Chooses the sprite for an item lying on the map.
     *
     * @param name   the item's name
     * @param symbol the character it currently shows
     * @return a sprite identifier, or {@code null} when the item is concealing
     *         itself and must not be drawn
     */
    public String itemOnMap(String name, char symbol) {
        Entry entry = items.get(name);
        if (entry != null) {
            return entry.symbol() == symbol ? entry.id() : null;
        }
        return unknownItem(name, symbol);
    }

    /**
     * Chooses the sprite for an item the player is carrying. Carried items
     * are never concealed from their owner.
     *
     * @param name   the item's name
     * @param symbol the character it shows
     * @return a sprite identifier, never {@code null}
     */
    public String itemInInventory(String name, char symbol) {
        Entry entry = items.get(name);
        return entry != null ? entry.id() : unknownItem(name, symbol);
    }

    private String unknownItem(String name, char symbol) {
        if (name.startsWith("Preserved ")) {
            return "item.preserved";
        }
        if (name.startsWith("Dried ")) {
            return "item.dried";
        }
        if (name.startsWith("Sword")) {
            return "item.sword";
        }
        String bySymbol = itemBySymbol.get(symbol);
        return bySymbol != null ? bySymbol : GLYPH_PREFIX + symbol;
    }

    /**
     * Chooses how to draw an actor.
     *
     * @param label  the actor's label, with or without a trailing health readout
     * @param symbol the character the actor currently shows
     * @return the sprite and whether the actor is disguised as an object
     */
    public ActorLook actor(String label, char symbol) {
        String name = HEALTH_SUFFIX.matcher(label).replaceFirst("");
        if (symbol == '@' && name.startsWith("Farmer")) {
            return new ActorLook("actor.farmer", false);
        }
        Entry entry = actors.get(name);
        if (entry != null && entry.symbol() == symbol) {
            return new ActorLook(entry.id(), false);
        }
        if (entry != null) {
            // A known creature showing someone else's character is in disguise.
            String object = itemBySymbol.get(symbol);
            if (object == null) {
                object = groundBySymbol.get(symbol);
            }
            if (object != null) {
                return new ActorLook(object, true);
            }
        }
        String bySymbol = actorBySymbol.get(symbol);
        return new ActorLook(bySymbol != null ? bySymbol : GLYPH_PREFIX + symbol, false);
    }

    /**
     * Lists every sprite identifier the catalog can return, apart from glyph
     * fallbacks.
     *
     * @return the identifiers in alphabetical order
     */
    public Set<String> spriteIds() {
        Set<String> ids = new TreeSet<>();
        ids.add("actor.farmer");
        ids.add("item.preserved");
        ids.add("item.dried");
        actors.values().forEach(entry -> ids.add(entry.id()));
        items.values().forEach(entry -> ids.add(entry.id()));
        grounds.values().forEach(entry -> ids.add(entry.id()));
        return Collections.unmodifiableSet(ids);
    }
}
