package game.interfaces;

/**
 * This represents a ground on which the Merchant can plant a purchased crop.
 *
 * @author Vanessa Liau Jia Yee <34599398>
 * @version 1.0.0
 */
public interface PlantableGround
{
    /**
     * Checks if a crop can be planted on this ground.
     *
     * @return true if planting is allowed, false otherwise
     */
    boolean canPlant();
}
