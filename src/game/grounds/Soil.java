package game.grounds;
import edu.monash.fit2099.engine.positions.Ground;
/**
 * Soil represents cultivated ground created when Dirt is tilled.
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class Soil extends Ground {
    /**
     * Constructor for the Soil class.
     *
     * <p>Creates Soil represented by the ',' display character.</p>
     */
    public Soil() {
        super(',',"Soil");
    }
}
