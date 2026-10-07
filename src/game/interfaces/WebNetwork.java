package game.interfaces;

import edu.monash.fit2099.engine.positions.Location;

/**
 * Manages the webs belonging to one creature.
 */
public interface WebNetwork {

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
}
