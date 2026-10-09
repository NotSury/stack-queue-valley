package game.interfaces;

import edu.monash.fit2099.engine.positions.Location;

/**
 * Provides operations for scattering and reclaiming portable bait.
 *
 * @author Surya Muni Peragas 35651628
 * @version 1.1.1
 */
public interface BaitCarrier {

    /**
     * Checks for a portable inventory item and an eligible adjacent tile.
     * Does not transfer items or change environment.
     * @param origin the creature's current location
     * @return true when at least one bait item can be scattered
     */
    boolean canScatterBait(Location origin);

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
     * Transfers portable ground items from the creature's current
     * tile and adjacent tiles into its inventory.
     *
     * @param origin the creature's current location
     * @return the number of items transferred into the inventory
     */
    int reclaimNearbyItems(Location origin);
}
