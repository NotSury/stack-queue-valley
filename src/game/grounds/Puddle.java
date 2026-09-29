package game.grounds;
import edu.monash.fit2099.engine.positions.Ground;
/**
 * Puddle represents a wet ground created when Dirt is watered.
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class Puddle extends Ground {
    /**
     * Constructor for the Puddle class.
     *
     * <p>Creates a Puddle represented by the '~' display character.</p>
     */
    public Puddle() {
        super('~',"Puddle");
    }
}
