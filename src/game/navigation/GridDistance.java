package game.navigation;

import edu.monash.fit2099.engine.positions.Location;

/**
 * Calculates distance between locations on the same map.
 * Uses Chebyshev distance, where horizontal, vertical and diagonal movement
 * to an adjacent tile each count as one step.
 *
 * @author Surya Muni Peragas 35651628
 * @version 1.0.0
 */
public final class GridDistance {

    private GridDistance() {}

    /**
     * Calculates the distance between two locations.
     * Both locations must belong to the same map.
     *
     * @param first the first location
     * @param second the second location
     * @return the greater of the horizontal and vertical distances
     */
    public static int between(Location first, Location second) {
        int horizontalDistance = Math.abs(first.x() - second.x());
        int verticalDistance = Math.abs(first.y() - second.y());

        return Math.max(horizontalDistance, verticalDistance);
    }
}
