package game.grounds;
import edu.monash.fit2099.engine.positions.Ground;
import game.interfaces.Chargeable;
import game.statuses.ChargedStatus;

/**
 * Puddle represents a wet ground created when Dirt is watered.
 *
 * @author Lee Le Si 34599223
 * @author Khoo Lynn
 * @version 1.1.0
 */
public class Puddle extends Ground implements Chargeable {
    /**
     * Constructor for the Puddle class.
     *
     * <p>Creates a Puddle represented by the '~' display character.</p>
     */
    public Puddle() {
        super('~',"Puddle");
    }

    /**
     * Charges this Puddle.
     */
    @Override
    public void charge(){
        if (!this.hasStatus(ChargedStatus.class)){
            this.addStatus(new ChargedStatus());
        }
    }
}
