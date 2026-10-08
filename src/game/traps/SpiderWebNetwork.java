package game.traps;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.WebAbility;
import game.interfaces.PlantableGround;
import game.interfaces.WebNetwork;
import game.items.Web;
import game.navigation.GridDistance;
import game.navigation.MovementSafety;
import game.interfaces.WebTriggerRecorder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Manages a creature's limited network of web traps.
 * Stores a fixed nest location, validates placement and
 * activates nearby owned webs.
 * Randomness controls only the initial visibility of a web.
 * Placement eligibility and active counts ignore visibility.
 */
public class SpiderWebNetwork implements WebNetwork, WebTriggerRecorder {

    private static final int MAXIMUM_ACTIVE_WEBS = 6;
    private static final int WEB_LIFETIME_TURNS = 10;

    private final Actor owner;
    private final Location nestLocation;
    private final List<Web> webs = new ArrayList<>();
    private final Random visibilityRandom = new Random();

    private WebAlert latestAlert;
    private long nextEventId = 1;

    /**
     * Creates a web network for one creature.
     * The owner must be placed on the nest's map before
     * any webs are placed.
     * @param owner the creature that owns the network
     * @param nestLocation the creature's fixed nest location
     */
    public SpiderWebNetwork(Actor owner, Location nestLocation) {
        this.owner = owner;
        this.nestLocation = nestLocation;
    }

    /**
     * Returns the fixed nest location.
     *
     * @return the nest location
     */
    @Override
    public Location getNestLocation() {
        return nestLocation;
    }

    /**
     * Counts active owned webs without changing the network.
     *
     * @return the number of active owned webs
     */
    @Override
    public int countActiveWebs() {
        int count = 0;

        for (Web web : webs) {
            if (web.isActive()) {
                count++;
            }
        }

        return count;
    }

    /**
     * Checks whether a location is eligible for placement.
     * Requires safe, unoccupied, plantable ground on the nest's
     * map. Rejects locations already containing a web and
     * enforces the active web limit.
     *
     * @param location the proposed placement location
     * @return true if a web can be placed
     */
    @Override
    public boolean canPlaceAt(Location location) {
        if (location.map() != nestLocation.map()
                || !owner.isConscious()
                || !nestLocation.map().contains(owner)
                || countActiveWebs() >= MAXIMUM_ACTIVE_WEBS
                || !location.canActorEnter(owner)
                || !MovementSafety.isSafe(location)
                || location.getGround()
                .asCapability(PlantableGround.class)
                .isEmpty()) {
            return false;
        }

        for (Item item : location.getItems()) {
            if (item.hasAbility(WebAbility.WEB_TRAP)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Places a web and registers it with this network.
     *
     * Each new web has a fifty-percent chance of starting hidden.
     * Visibility does not affect placement or state conditions.
     *
     * @param location the proposed placement location
     * @return true if a web was placed
     */
    @Override
    public boolean placeAt(Location location) {
        removeInactiveRecords();

        if (!canPlaceAt(location)) {
            return false;
        }

        boolean hidden = visibilityRandom.nextBoolean();

        Web web = new Web(
                owner,
                location,
                WEB_LIFETIME_TURNS,
                hidden,
                this
        );

        location.addItem(web);
        webs.add(web);

        return true;
    }

    /**
     * Sends a vibration through nearby active owned webs.
     * Reveals hidden webs and immediately attempts to catch
     * actors occupying them. Triggered webs are consumed.
     *
     * @param origin the centre of the activation area
     * @param radius the activation radius
     * @return a description of the effects, or an empty string
     */
    @Override
    public String activeNearbyWebs(Location origin, int radius) {
        if (origin.map() != nestLocation.map()) {
            return "";
        }

        removeInactiveRecords();

        int affectedWebs = 0;
        int revealedWebs = 0;
        StringBuilder catches = new StringBuilder();

        for (Web web : List.copyOf(webs)) {
            if (!web.isActive()
                    || GridDistance.between(
                    origin,
                    web.getLocation()
            ) > radius) {
                continue;
            }

            affectedWebs++;

            if (web.isHidden()) {
                revealedWebs++;
            }

            web.reveal();

            String result = web.trigger();

            if (!result.isEmpty()) {
                catches.append(result).append("\n");
            }
        }

        removeInactiveRecords();

        if (affectedWebs == 0) {
            return "";
        }

        String message = owner
                + " sends a vibration through "
                + affectedWebs + " web(s), revealing "
                + revealedWebs + " hidden web(s).";

        if (catches.length() > 0) {
            message += "\n" + catches.toString().stripTrailing();
        }

        return message;
    }

    @Override
    public void recordTrigger(Location location) {
        latestAlert = new WebAlert(nextEventId, location);
        nextEventId++;
    }

    @Override
    public WebAlert getLatestAlert() {
        return latestAlert;
    }

    @Override
    public boolean clearAlert(long eventId) {
        if  (latestAlert == null || latestAlert.getEventId() != eventId) {
            return false;
        }
        latestAlert = null;
        return true;
    }

    private void removeInactiveRecords() {
        webs.removeIf(web -> !web.isActive());
    }
}
