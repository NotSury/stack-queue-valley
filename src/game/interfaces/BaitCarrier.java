package game.interfaces;

import edu.monash.fit2099.engine.positions.Location;

/**
 * Provides operations for scattering and reclaiming portable bait.
 */
public interface BaitCarrier {

    /**
     * Scatters portable inventory items onto eligible adjacent tiles.
     * Items without an eligible destination remain in the inventory.
     *
     * @param origin the creature's current location
     * @param maximumItems the maximum number of items to scatter
     * @return the number of items transferred to the ground
     */
    int scatterBait(Location origin, int maximumItems);

    /**
     * Transfers adjacent portable ground items into the inventory.
     * Excludes the creature's current tile.
     *
     * @param origin the creature's current location
     * @return the number of items transferred into the inventory
     */
    int reclaimAdjacentItems(Location origin);
}
