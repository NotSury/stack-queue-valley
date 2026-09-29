package game.grounds;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import game.actions.TillingAction;

import game.actions.WateringAction;
import game.interfaces.Tillable;
import game.interfaces.Waterable;
/**
 * Dirt represents a ground that can be tilled or watered by an actor.
 *
 * <p>When tilled, Dirt is transformed into Soil. When watered, it is
 * transformed into a Puddle. Tilling and watering actions are only made
 * available when the actor is standing directly on the Dirt.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class Dirt extends Ground implements Tillable, Waterable {

    /**
     * Constructor for the Dirt class.
     *
     * <p>Creates Dirt represented by the '.' display character.</p>
     */
    public Dirt() {
        super('.', "Dirt");
    }
    /**
     * Applies the tilling effect to this Dirt.
     *
     * <p>The Dirt at the given location is replaced with Soil.</p>
     *
     * @param location the location containing the Dirt being tilled
     */
    @Override
    public void till(Location location){
        location.setGround(new Soil());
    }
    /**
     * Applies the watering effect to this Dirt.
     *
     * <p>The Dirt at the given location is replaced with a Puddle.</p>
     *
     * @param location the location containing the Dirt being watered
     */
    @Override
    public void water(Location location){
        location.setGround(new Puddle());

    }
    /**
     * Returns the actions available to an actor interacting with this Dirt.
     *
     * <p>TillingAction and WateringAction are provided only when the actor
     * is standing directly on this ground.</p>
     *
     * @param actor the actor interacting with the Dirt
     * @param location the location containing this Dirt
     * @param direction the direction of this ground relative to the actor
     * @return the available actions for this Dirt
     */
    @Override
    public ActionList allowableActions(
            Actor actor,
            Location location,
            String direction
    ){
        ActionList actions = new ActionList();

        if (direction.isEmpty()){
            actions.add(new TillingAction(this,location));
            actions.add(new WateringAction(this,location));
        }
        return actions;
    }


}
