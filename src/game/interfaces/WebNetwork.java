package game.interfaces;

import edu.monash.fit2099.engine.positions.Location;

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
     * Keeps two guard webs on the tiles directly next to the nest.
     *
     * <p>Missing guard webs are placed as entry webs, checking the
     * adjacent tiles in a fixed order, so the result is deterministic.</p>
     *
     * @return the number of guard webs placed
     */
    int maintainGuardWebs();

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
     * @param turns the turns added to each web
     * @return the number of webs revealed
     */
    int revealAndReinforceAll(int turns);
}
