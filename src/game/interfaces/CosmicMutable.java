package game.interfaces;

import game.grounds.CosmicCrop;

/**
 * Defines a crop that can transform into a Cosmic Crop
 * when affected by a celestial impact.
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public interface CosmicMutable {

    /**
     * Returns the cosmic variety associated with this crop.
     *
     * @return the corresponding Cosmic Crop variety
     */
    CosmicCrop.Variety getCosmicVariety();
}
