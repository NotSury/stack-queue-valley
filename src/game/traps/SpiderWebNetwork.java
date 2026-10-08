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
import java.util.Comparator;

/**
 * Manages a creature's limited network of web traps.
 * Stores a fixed nest location, validates placement and
 * activates nearby owned webs.
 * Randomness controls only the initial visibility of a web.
 * Placement eligibility and active counts ignore visibility.
 * Records the latest web trigger as an alert with an increasing
 * event id; the alert outlives the web that raised it.
 *
 * @author Surya Muni Peragas 35651628
 * @author Lee Le Si 34599223
 * @version 1.1.0
 */
public class SpiderWebNetwork implements WebNetwork, WebTriggerRecorder {

    private static final int MAXIMUM_ACTIVE_WEBS = 6;
    private static final int WEB_LIFETIME_TURNS = Web.MAXIMUM_LIFETIME;
    /**
     * Number of guard webs kept next to the nest.
     */
    private static final int GUARD_WEBS = 2;

    /**
     * Distance from the nest at which guard webs sit.
     */
    private static final int GUARD_RADIUS = 1;

    /**
     * Map ticks a temporary reveal lasts: covers the Farmer's next turn
     * whether the spider acts before or after him in a round.
     */
    private static final int TEMPORARY_REVEAL_TICKS = 2;

    private final Actor owner;
    private final Location nestLocation;
    private final List<Web> webs = new ArrayList<>();
    private final Random visibilityRandom = new Random();

    /**
     * Most recent trigger that has not been cleared, or null.
     */
    private WebAlert latestAlert;

    /**
     * Identifier given to the next trigger event; only ever increases.
     */
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
    public boolean canPlaceAt(Location location) {
        return countActiveWebs() < MAXIMUM_ACTIVE_WEBS && isValidSite(location);
    }

    /**
     * Checks every placement rule except the active web limit.
     * Requires safe, unoccupied, plantable ground on the nest's map,
     * not the nest tile itself, and no existing web on the tile.
     *
     * @param location the proposed placement location
     * @return true if the site itself is valid
     */
    private boolean isValidSite(Location location) {
        if (location.map() != nestLocation.map()
                || location == nestLocation
                || !owner.isConscious()
                || !nestLocation.map().contains(owner)
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

        addWeb(location);
        return true;

    }

    /**
     * Creates an owned web at a location and records it in this network.
     * Each new web has a fifty-percent chance of starting hidden.
     *
     * @param location the location to place the web on
     */
    private void addWeb(Location location) {
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

    /**
     * Returns how many more webs can be placed before the limit.
     *
     * @return the number of free web slots, never below zero
     */
    @Override
    public int getRemainingCapacity(){
        return Math.max(0, MAXIMUM_ACTIVE_WEBS - countActiveWebs());
    }

    /**
     * Places an entry web, replacing the shortest-lived web at the limit.
     * The site is validated first, so nothing is removed for an invalid
     * site. Guard webs are never replaced.
     *
     * @param location the proposed placement location
     * @return true if a web was placed
     */
    @Override
    public boolean placeEntryWeb(Location location){
        removeInactiveRecords();

        if(!isValidSite(location)) {
            return false;
        }

        if (countActiveWebs() >= MAXIMUM_ACTIVE_WEBS) {
            Web oldest = findShortestLivedReplaceable();
            if (oldest == null) {
                return false;
            }
            oldest.dismantle();
            removeInactiveRecords();
        }
        addWeb(location);
        return true;
    }

    /**
     * Keeps two guard webs on the tiles directly next to the nest.
     * Tiles are tried in a fixed order (top row first, then left to
     * right), so the result is deterministic.
     *
     * @return the number of guard webs placed
     */
    @Override
    public int maintainGuardWebs(){
        removeInactiveRecords();

        int guards = countGuardWebs();
        int placed = 0;

        List <Location> sites = new ArrayList<>(nestLocation.getNearbyLocations(GUARD_RADIUS));
        sites.sort(Comparator.comparing(Location::y).thenComparing(Location::x));

        for (Location site : sites) {
            if (guards + placed >= GUARD_WEBS){
                break;
            }
            if (GridDistance.between(site, nestLocation) == GUARD_RADIUS
                && placeEntryWeb(site)){
                placed++;
            }
        }
        return placed;
    }

    /**
     * Extends the lifespan of owned webs near a location, up to eight turns.
     *
     * @param origin the centre of the reinforcement area
     * @param radius how far from the origin webs are reinforced
     * @param turns the turns added to each web
     * @return the number of webs whose lifespan increased
     */
    @Override
    public int reinforceNearby(Location origin, int radius, int turns){
        if (origin.map() != nestLocation.map()) {
            return 0;
        }
        removeInactiveRecords();

        int reinforced = 0;
        for (Web web : webs) {
            if (GridDistance.between(origin, web.getLocation()) <= radius && web.reinforce(turns)>0){
                reinforced++;
            }
        }
        return reinforced;
    }


    /**
     * Reveals every owned web through the Farmer's next turn and extends
     * each lifespan, up to eight turns.
     *
     * @param turns the turns added to each web
     * @return the number of webs revealed
     */
    @Override
    public int revealAndReinforceAll(int turns) {
        removeInactiveRecords();

        for (Web web : webs) {
            web.revealTemporarily(TEMPORARY_REVEAL_TICKS);
            web.reinforce(turns);
        }
        return webs.size();
    }

    /**
     * Checks whether a web is a guard web, i.e. directly next to the nest.
     *
     * @param web the web to check
     * @return true if the web is next to the nest
     */
    private boolean isGuardWeb(Web web){
        return GridDistance.between(web.getLocation(), nestLocation) == GUARD_RADIUS;
    }

    /**
     * Counts active guard webs.
     *
     * @return the number of active webs next to the nest
     */
    private int countGuardWebs(){
        int count = 0;
        for (Web web : webs) {
            if (web.isActive() && isGuardWeb(web)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Finds the active non-guard web with the fewest turns left.
     * Ties go to the web placed first.
     *
     * @return the web to replace, or null if every active web is a guard
     */
    private Web findShortestLivedReplaceable(){
        Web shortest = null;
        for (Web web : webs) {
            if (!web.isActive() || isGuardWeb(web)) {
                continue;
            }
            if (shortest == null || web.getRemainingTurns() <shortest.getRemainingTurns()) {
                shortest = web;
            }
        }
        return shortest;
    }

    /**
     * Stores a new alert for a web that has just caught an actor.
     * Each call gets a larger event id than the one before.
     *
     * @param location the location of the triggered web
     */
    @Override
    public void recordTrigger(Location location) {
        latestAlert = new WebAlert(nextEventId, location);
        nextEventId++;
    }

    /**
     * Returns the most recent alert that has not been cleared.
     *
     * @return the latest alert, or null if there is none
     */
    @Override
    public WebAlert getLatestAlert() {
        return latestAlert;
    }

    /**
     * Clears the latest alert only if it is still the given event, so a
     * state finishing an old alert cannot erase a newer one.
     *
     * @param eventId the identifier of the investigated alert
     * @return true if the alert was cleared
     */
    @Override
    public boolean clearAlert(long eventId) {
        if  (latestAlert == null || latestAlert.getEventId() != eventId) {
            return false;
        }
        latestAlert = null;
        return true;
    }

    /**
     * Forgets webs that are no longer active.
     */
    private void removeInactiveRecords() {
        webs.removeIf(web -> !web.isActive());
    }
}
