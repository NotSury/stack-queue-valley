package game.perception;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Provides environmental information and navigation queries for creatures.
 * Implementation inspect the map without moving actors,
 * transferring items or changing creature states.
 */
public interface EnvironmentScanner {

    /**
     * Finds a Farmer within the specified detection radius.
     *
     * @param origin the location from which to scan
     * @param radius the maximum detection distance
     * @return a detected Farmer, or null if none is detected
     */
    Actor findFarmer(Location origin, int radius);

    /**
     * Finds the nearest reachable location containing a portable item
     * within the specified distance range.
     * Includes the origin when minimumDistance is zero.
     * Equally near targets are selected using a fixed ordering.
     *
     * @param creature the creature that must be able to reach the item
     * @param origin the location from which to scan
     * @param minimumDistance the minimum detection distance, inclusive
     * @param maximumDistance the maximum detection distance, inclusive
     * @return the item's location, or null if no eligible item is reachable
     */
    Location findNearestPortableItemLocation(Actor creature, Location origin, int minimumDistance, int maximumDistance);
}
