package game.interfaces;

import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.actors.Actor;
/**
 * Manages the webs belonging to one creature.
 *
 * <p>Also exposes the latest web-trigger alert through
 * {@link WebAlertSource}.</p>
 *
 * @author Surya Muni Peragas 35651628
 * @author Lee Le Si 34599223
 * @version 1.1.0
 */
public interface WebNetwork extends WebAlertSource {

    /**
     * Returns the creature's fixed nest location.
     *
     * @return the nest location
     */
    Location getNestLocation();

    /**
     * Counts the creature's active webs.
     *
     * @return the number of active webs
     */
    int countActiveWebs();

    /**
     * Checks whether a web can be placed at a location.
     * Includes terrain, occupancy, hazards, existing webs
     * and the active web limit.
     *
     * @param location the proposed placement location
     * @return true if placement is allowed
     */
    boolean canPlaceAt(Location location);

    /**
     * Places an owned web if the location is eligible.
     *
     * @param location the proposed placement location
     * @return true if a web was placed
     */
    boolean placeAt(Location location);

    /**
     * Reveals nearby owned webs and triggers eligible occupied webs.
     *
     * Repeated trapping must not stack pending lost turns.
     *
     * @param origin the centre of the activation area
     * @param radius the activation radius
     * @return a description of the effects, or an empty string
     */
    String activeNearbyWebs(Location origin, int radius);

    /**
     * Returns how many more webs can be placed before the active web
     * limit is reached.
     *
     * @return the number of free web slots, never below zero
     */
    int getRemainingCapacity();

    /**
     * Places an entry web, replacing an old web if the limit is reached.
     *
     * <p>The site is checked first; if it is not valid, nothing is
     * replaced. At the limit, the owned web with the fewest turns left
     * is removed to make room. Guard webs next to the nest are never
     * replaced. Ground and other items on the tile are unchanged.</p>
     *
     * @param location the proposed placement location
     * @return true if a web was placed
     */
    boolean placeEntryWeb(Location location);

    /**
     * Keeps the given number of guard webs on the tiles directly next
     * to the nest.
     *
     * <p>Missing guard webs are placed as entry webs, checking the
     * adjacent tiles in a fixed order, so the result is deterministic.</p>
     *
     * @param guardWebs how many guard webs the nest should have
     * @return the number of guard webs placed
     */
    int maintainGuardWebs(int guardWebs);

    /**
     * Extends the lifespan of owned webs near a location, never above
     * the maximum lifespan of eight turns.
     *
     * @param origin the centre of the reinforcement area
     * @param radius how far from the origin webs are reinforced
     * @param turns the turns added to each web
     * @return the number of webs whose lifespan increased
     */
    int reinforceNearby(Location origin, int radius, int turns);

    /**
     * Reveals every owned web until after the Farmer's next turn and
     * extends each web's lifespan, never above eight turns.
     *
     * <p>When the reveal ends, each web returns to its original hidden
     * or visible appearance.</p>
     *
     * @param target the Farmer the webs are revealed to
     * @param turns the turns added to each web
     * @return a description of the reveal
     */
    String revealAndReinforceAll(Actor target, int turns);

    /**
     * Places up to a number of entry webs on tiles next to a location,
     * replacing the shortest-lived web whenever the limit is reached.
     *
     * <p>When a focus location is given, tiles closest to it are tried
     * first (e.g. webs on the way towards an alert); otherwise tiles are
     * tried top row first, then left to right.</p>
     *
     * @param origin the location the webs are placed around
     * @param count the maximum number of entry webs to place
     * @param focus the location to place webs towards, or null
     * @return the number of entry webs placed
     */
    int placeEntryWebs(Location origin, int count, Location focus);

    /**
     * Checks whether a number of ordinary webs can still be placed.
     *
     * @param webs the number of webs needed
     * @return true if there are at least that many free slots
     */
    boolean hasCapacityFor(int webs);

    /**
     * Returns where the latest uncleared alert was raised.
     *
     * @return the alert location, or null if there is no alert
     */
    Location getAlertLocation();

    /**
     * Returns the event id of the latest uncleared alert.
     * A changed value means a newer web was triggered.
     *
     * @return the alert's event id, or 0 if there is no alert
     */
    long getAlertVersion();
}
