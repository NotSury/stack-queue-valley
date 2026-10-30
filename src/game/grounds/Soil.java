package game.grounds;
import edu.monash.fit2099.engine.positions.Ground;
import game.capabilities.GroundAbility;
import game.interfaces.PlantableGround;
import game.capabilities.GroundAbility;

/**
 * Soil represents cultivated ground created when Dirt is tilled.
 *
 * @author <Lee Le Si> <34599223>
 * @author <Vanessa Liau Jia Yee> <34599398>
 * @version 1.1.0
 */
public class Soil extends Ground implements PlantableGround{
    /**
     * Constructor for the Soil class.
     *
     * <p>Creates Soil represented by the ',' display character.</p>
     */
    public Soil() {
        super(',',"Soil");
        this.enableAbility(GroundAbility.OPEN_GROUND);
        this.enableAbility(GroundAbility.METEOR_IMPACTABLE);
    }
}
