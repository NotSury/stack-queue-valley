package game.grounds;
import edu.monash.fit2099.engine.positions.Ground;
import game.capabilities.GroundAbility;
import game.interfaces.PlantableGround;

/**
 * Soil represents cultivated ground created when Dirt is tilled.
 *
 * @author <Lee Le Si> <34599223>
 * @author <Vanessa Liau Jia Yee> <34599398>
 * @version 1.1.0
 */
public class Soil extends Ground implements PlantableGround {
    /**
     * Constructor for the Soil class.
     *
     * <p>Creates Soil represented by the ',' display character.</p>
     */
    public Soil() {
        super(',', "Soil");
        this.enableAbility(GroundAbility.OPEN_GROUND);
        this.enableAbility(GroundAbility.METEOR_IMPACTABLE);
    }

    /**
     * Checks if a crop can be placed on this soil.
     *
     * @return true if planting is allowed
     */
    @Override
    public boolean canPlant() {
        return true;
    }
}
