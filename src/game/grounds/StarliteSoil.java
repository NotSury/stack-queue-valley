package game.grounds;

import edu.monash.fit2099.engine.positions.Ground;
import game.capabilities.GroundAbility;
import game.interfaces.PlantableGround;

/**
 * Plantable ground restored from a meteor impact crater.
 * Unlike ordinary impactable terrain, it is resistant
 * to later meteor impacts.
 *
 * @author Chin Kai Xuan 35730374
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.1.0
 */
public final class StarliteSoil extends Ground
        implements PlantableGround {

    /**
     * Creates a restored, meteor-resistant soil tile.
     */
    public StarliteSoil() {
        super('_', "Starlite Soil");

        enableAbility(GroundAbility.OPEN_GROUND);
    }

    /**
     * Checks if a crop can be placed on Starlite Soil.
     *
     * @return true becasue Starlite Soil supports planting
     */
    @Override
    public boolean canPlant() {
        return true;
    }
}
