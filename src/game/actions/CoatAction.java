package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.coatings.Coating;
import game.interfaces.Coatable;
import game.interfaces.CoatingSource;
/**
 * Coats something (e.g. the sword) using a coating source. Replaces any existing coating.
 *
 * @author <Lee Le Si> <34599223>
 * @version 1.0.0
 */
public class CoatAction extends Action {
    /**
     * The thing being coated.
     */
    private final Coatable coatable;
    /**
     * Where the coating comes from.
     */
    private final CoatingSource source;
    /**
     * Where the source lies, or null if the actor is carrying it.
     */
    private final Location sourceLocation;


    /**
     * Constructor for CoatAction.
     *
     * @param coatable       the thing being coated
     * @param source         where the coating comes from
     * @param sourceLocation where the source lies, or null if the actor carries it
     */
    public CoatAction(Coatable coatable, CoatingSource source, Location sourceLocation){
        this.coatable = coatable;
        this.source = source;
        this.sourceLocation = sourceLocation;
    }


    /**
     * Applies the source's coating, replacing any coating already applied, then
     * lets the source react (e.g. a melon removes itself because it is used up).
     *
     * @param actor the actor applying the coating
     * @param map   the map the actor is on
     * @return a description of the coating
     */
    @Override
    public String execute(Actor actor, GameMap map){
        Coating coating = source.getCoating();
        coatable.setCoating(coating); // replaces the previous coating, if any
        source.onCoatingApplied(actor, sourceLocation);  // e.g. melon removes itself
        return actor + " coats " + coatable + " with " + coating + ".";
    }


    /**
     * Text shown for this action in the menu.
     *
     * @param actor the actor who would apply the coating
     * @return e.g. "Farmer Bob coats Sword with Melon Coating"
     */
    @Override
    public String menuDescription(Actor actor){
        return actor + " coats " + coatable + " with " + source.getCoating();
    }

}
