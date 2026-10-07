package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.traps.WebTrapHandler;
import game.capabilities.WebAbility;

import java.util.List;

/**
 * A non-portable web belonging to one creature.
 *
 * Catches one eligible actor and is consumed when triggered.
 * Unused webs expire after their configured lifetime.
 * Hidden webs display the item or ground beneath them.
 */
public class Web extends Item {

    private static final char VISIBLE_DISPLAY_CHAR = ':';

    private final Actor owner;
    private final Location location;

    private int remainingTurns;
    private boolean hidden;

    /**
     * Creates a web for placement at a fixed location.
     *
     * @param owner the creature that owns the web
     * @param location the location where the web will be placed
     * @param lifetimeTurns the number of map ticks before expiry
     * @param hidden whether the web starts hidden
     */
    public Web(Actor owner, Location location, int lifetimeTurns, boolean hidden) {
        super("Spider Web", VISIBLE_DISPLAY_CHAR);

        this.owner = owner;
        this.location = location;
        this.remainingTurns = lifetimeTurns;
        this.hidden = hidden;

        enableAbility(WebAbility.WEB_TRAP);
    }

    /**
     * Returns the web's placement location.
     *
     * @return the location containing this web
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Checks whether the web remains present and usable.
     * Webs stop being active when their owner dies or leaves
     * the web's map.
     *
     * @return true if the web remains active
     */
    public boolean isActive() {
        return remainingTurns > 0
                && location.getItems().contains(this)
                && owner.isConscious()
                && location.map().contains(owner);
    }

    /**
     * Checks whether the web is hidden.
     *
     * @return true if the web is hidden
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Makes the web visible.
     */
    public void reveal() {
        hidden = false;
    }

    /**
     * Attempts to catch the actor occupying this web.
     * Consumes the web only when a new binding is applied.
     * Immune actors and already bound actors do not trigger it.
     *
     * @return a description of the catch, or an empty string
     */
    public String trigger() {
        if (!isActive() || !location.containsAnActor()) {
            return "";
        }

        Actor target = location.getActor();

        if (!WebTrapHandler.tryBind(target)) {
            return "";
        }

        removeFromMap();

        return target + " is caught in a web at ("
                + location.x() + ", " + location.y() + ").";
    }

    /**
     * Checks for an occupying actor and advances the lifetime.
     * Expired webs and webs whose owner is no longer present
     * are removed.
     *
     * @param currentLocation the location containing the web
     */
    @Override
    public void tick(Location currentLocation) {
        if (!isActive()) {
            removeFromMap();
            return;
        }

        if (!trigger().isEmpty()) {
            return;
        }

        remainingTurns--;

        if (remainingTurns <= 0) {
            removeFromMap();
        }
    }

    /**
     * Displays the web or the visible content underneath it.
     * Hidden webs do not conceal other items or change
     * the underlying ground.
     *
     * @return the character used to draw this item
     */
    @Override
    public char getDisplayChar() {
        if (!hidden) {
            return VISIBLE_DISPLAY_CHAR;
        }

        List<Item> items = location.getItems();

        for (int index = items.size() - 1; index >= 0; index--) {
            Item item = items.get(index);

            if (item != this
                    && !item.hasAbility(WebAbility.WEB_TRAP)) {
                return item.getDisplayChar();
            }
        }

        return location.getGround().getDisplayChar();
    }

    private void removeFromMap() {
        remainingTurns = 0;
        location.removeItem(this);
    }
}
