package game.interfaces;

import edu.monash.fit2099.engine.positions.Location;
/**
 * Waterable represents a ground that can be watered.
 *
 * <p>Grounds implementing this interface define the transformation
 * that occurs when they are watered.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public interface Waterable {
    /**
     * Applies the watering effect to this ground at the given location.
     *
     * @param location the location of the ground being watered
     */
    void water(Location location);
}
