package game.traps;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.GroundAbility;
import game.capabilities.WebAbility;
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
 * @version 1.3.0
 */
public class SpiderWebNetwork implements WebNetwork, WebTriggerRecorder {

    private static final int HIDDEN_WEB_CHANCE_PERCENT = 20;
    private static final int MAXIMUM_ACTIVE_WEBS = 6;
    private static final int WEB_LIFETIME_TURNS = Web.MAXIMUM_LIFETIME;

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
     * Requires safe, unoccupied, ground with the OPEN_GROUND ability on the nest's
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
     * Requires safe, unoccupied, ground with the OPEN_GROUND ability on the nest's map,
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
                || !location.getGround().hasAbility(GroundAbility.OPEN_GROUND)) {
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
     * Each new web has a twenty-percent chance of starting hidden.
     *
     * @param location the location to place the web on
     */
    private void addWeb(Location location) {
        boolean hidden = visibilityRandom.nextInt(100) < HIDDEN_WEB_CHANCE_PERCENT;

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
     * Returns how many more webs can be placed before the limit.
     *
     * @return the number of free web slots, never below zero
     */
    @Override
    public int getRemainingCapacity(){
        return Math.max(0, MAXIMUM_ACTIVE_WEBS - countActiveWebs());
    }

    /**
     * Checks whether an adjacent entry web can be placed without mutation.
     * At capacity, an active non-guard web must be available for replacement.
     *
     * @param origin the proposed placement centre
     * @return true when at least one adjacent entry placement can succeed
     */
    @Override
    public boolean canPlaceEntryWebs(Location origin) {
        if (origin.map() != nestLocation.map()) {
            return false;
        }

        if (countActiveWebs() >= MAXIMUM_ACTIVE_WEBS
                && findShortestLivedReplaceable() == null) {
            return false;
        }

        for (Location site : origin.getNearbyLocations(1)) {
            if (isValidSite(site)) {
                return true;
            }
        }

        return false;
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
     * Restores existing guard webs to their maximum lifetime and places
     * missing guards in coordinate order. Existing guards are preserved.
     *
     * @param guardWebs the desired minimum number of guard webs
     * @return the number of existing guards reinforced plus new guards placed
     */
    @Override
    public int maintainGuardWebs(int guardWebs) {
        removeInactiveRecords();

        int maintained = 0;

        for (Web web : webs) {
            if (isGuardWeb(web)
                    && web.reinforce(WEB_LIFETIME_TURNS) > 0) {
                maintained++;
            }
        }

        List<Location> sites = new ArrayList<>(
                nestLocation.getNearbyLocations(GUARD_RADIUS)
        );

        sites.sort(
                Comparator.comparingInt(Location::y)
                        .thenComparingInt(Location::x)
        );

        for (Location site : sites) {
            if (countGuardWebs() >= guardWebs) {
                break;
            }

            if (placeEntryWeb(site)) {
                maintained++;
            }
        }

        return maintained;
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
     * Reveals and reinforces owned webs. If none remain, first attempts
     * to establish two nest guards, then webs around the target.
     * If every placement is blocked, releases a silk pulse around the
     * target using the existing non-stacking one-turn binding mechanism.
     *
     * @param target the valid engaged Farmer
     * @param turns the lifetime increase for existing webs
     * @return a description of defensive placement, reveal or binding
     */
    @Override
    public String revealAndReinforceAll(Actor target, int turns) {
        removeInactiveRecords();

        if (target == null
                || !target.isConscious()
                || !nestLocation.map().contains(target)) {
            return owner
                    + " has no valid target for its defensive web effect.";
        }

        int placed = 0;

        if (webs.isEmpty()) {
            placed += maintainGuardWebs(2);

            if (webs.isEmpty()) {
                placed += placeEntryWebs(
                        nestLocation.map().locationOf(target),
                        2,
                        null
                );
            }
        }

        if (webs.isEmpty()) {
            Location centre = nestLocation.map().locationOf(target);

            List<Location> area = new ArrayList<>();
            area.add(centre);
            area.addAll(centre.getNearbyLocations(1));

            int caught = 0;

            for (Location tile : area) {
                if (tile.containsAnActor()
                        && WebTrapHandler.tryBind(tile.getActor())) {
                    caught++;
                }
            }

            return owner
                    + " releases a defensive silk pulse, binding "
                    + caught
                    + " Farmer(s) for one pending turn.";
        }

        int reinforced = 0;

        for (Web web : webs) {
            web.revealTemporarily(TEMPORARY_REVEAL_TICKS);

            if (web.reinforce(turns) > 0) {
                reinforced++;
            }
        }

        return owner
                + " establishes " + placed + " defensive web(s), reveals "
                + webs.size() + " web(s) and reinforces "
                + reinforced + ".";
    }

    /**
     * Places adjacent entry webs in a deterministic order.
     * A focus prefers tiles behind the direction from origin to focus.
     * Without a focus, uses row then column order, as the weaving planner does.
     *
     * @param origin the placement centre
     * @param count the maximum number of entry webs
     * @param focus the alert destination, or null for coordinate ordering
     * @return the number of entry webs placed
     */
    @Override
    public int placeEntryWebs(
            Location origin,
            int count,
            Location focus
    ) {
        if (origin.map() != nestLocation.map()) {
            return 0;
        }

        List<Location> sites = new ArrayList<>(
                origin.getNearbyLocations(1)
        );

        Comparator<Location> order =
                Comparator.comparingInt(Location::y)
                        .thenComparingInt(Location::x);

        if (focus != null && focus.map() == origin.map()) {
            int directionX = Integer.compare(
                    focus.x(),
                    origin.x()
            );

            int directionY = Integer.compare(
                    focus.y(),
                    origin.y()
            );

            order = Comparator.comparingInt((Location site) ->
                    (site.x() - origin.x()) * directionX
                            + (site.y() - origin.y()) * directionY
            ).thenComparing(order);
        }

        sites.sort(order);

        int placed = 0;

        for (Location site : sites) {
            if (placed >= count) {
                break;
            }

            if (placeEntryWeb(site)) {
                placed++;
            }
        }

        return placed;
    }

    /**
     * Checks whether a number of ordinary webs can still be placed.
     *
     * @param webs the number of webs needed
     * @return true if there are at least that many free slots
     */
    @Override
    public boolean hasCapacityFor(int webs) {
        return getRemainingCapacity() >= webs;
    }

    /**
     * Returns where the latest uncleared alert was raised.
     *
     * @return the alert location, or null if there is no alert
     */
    @Override
    public Location getAlertLocation() {
        return latestAlert == null ? null : latestAlert.getLocation();
    }

    /**
     * Returns the event id of the latest uncleared alert.
     *
     * @return the alert's event id, or 0 if there is no alert
     */
    @Override
    public long getAlertVersion() {
        return latestAlert == null ? 0 : latestAlert.getEventId();
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

    private Web findShortestLivedReplaceable() {
        Comparator<Web> order =
                Comparator.comparingInt(Web::getRemainingTurns)
                        .thenComparingInt(
                                web -> web.getLocation().y()
                        )
                        .thenComparingInt(
                                web -> web.getLocation().x()
                        );

        Web shortest = null;

        for (Web web : webs) {
            if (web.isActive()
                    && !isGuardWeb(web)
                    && (shortest == null
                    || order.compare(web, shortest) < 0)) {
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
