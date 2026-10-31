package game.interfaces;

import edu.monash.fit2099.engine.positions.Location;

/**
 * Represents a plant whose growth can be accelerated
 * independently of its normal growth interval and probability.
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.0.0
 */
public interface GrowthAcceleratable
{
    /**
     * Advances the plant by exactly one growth stage.
     *
     * @param location the Location containing the plant
     * @return true if the growth succeeds, or else false
     */
    boolean advanceGrowth(Location location);
}
