package game.interfaces;

import edu.monash.fit2099.engine.positions.Location;

/**
 * Provides ordinary web placement and capacity queries.
 * Clients do not need access to alerts or state-entry effects.
 *
 * @author Surya Muni Peragas 35651628
 * @version 1.0.0
 */
public interface WebPlacement {

    /**
     * Returns the fixed nest location.
     *
     * @return the nest location
     */
    Location getNestLocation();

    /**
     * Checks ordinary placement eligibility, including capacity.
     *
     * @param location the proposed placement location
     * @return true when ordinary placement is allowed
     */
    boolean canPlaceAt(Location location);

    /**
     * Places one ordinary web without replacing an existing web.
     *
     * @param location the proposed placement location
     * @return true when a web was placed
     */
    boolean placeAt(Location location);

    /**
     * Returns the number of available ordinary web slots.
     *
     * @return the remaining capacity, never below zero
     */
    int getRemainingCapacity();

    /**
     * Checks whether enough ordinary web slots remain.
     *
     * @param webs the number of required slots
     * @return true when sufficient capacity remains
     */
    boolean hasCapacityFor(int webs);
}
