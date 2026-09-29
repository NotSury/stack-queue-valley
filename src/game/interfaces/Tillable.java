package game.interfaces;

import edu.monash.fit2099.engine.positions.Location;
/**
 * Tillable represents a ground that can be tilled.
 *
 * <p>Grounds implementing this interface define the transformation
 * that occurs when they are tilled.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public interface Tillable {
    /**
     * Applies the tilling effect to this ground at the given location.
     *
     * @param location the location of the ground being tilled
     */
    void till(Location location);
}
