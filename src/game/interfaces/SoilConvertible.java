package game.interfaces;

import game.grounds.Soil;

/**
 * Capability for ground that can be transformed into Soil.
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.0.0
 */
public interface SoilConvertible {

    /**
     * Converts this ground into Soil.
     *
     * @return the resulting Soil ground
     */
    Soil convertToSoil();
}
