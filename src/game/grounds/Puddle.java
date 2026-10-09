package game.grounds;
import edu.monash.fit2099.engine.positions.Ground;
import game.coatings.ChargedCoating;
import game.coatings.Coating;
import game.interfaces.Chargeable;
import game.interfaces.CoatingSource;
import game.statuses.ChargedStatus;

/**
 * Puddle represents a wet ground created when Dirt is watered.
 *
 * @author Lee Le Si 34599223
 * @author Khoo Lynn 34598901
 * @version 1.2.0
 */
public class Puddle extends Ground implements Chargeable, CoatingSource {
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

    /**
     * Gives the coating this puddle applies to a weapon.
     *
     * @return a new charged coating
     */
    @Override
    public Coating getCoating(){
        return new ChargedCoating();
    }

    /**
     * A puddle can only coat a weapon while it is charged.
     *
     * <p>An uncharged puddle is just water, so no coat option is offered for it.</p>
     *
     * @return true if this puddle has the ChargedStatus, false otherwise
     */
    @Override
    public boolean canProvideCoating(){
        return this.hasStatus(ChargedStatus.class);
    }
}
