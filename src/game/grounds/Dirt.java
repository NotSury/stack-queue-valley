package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
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
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public class Dirt extends Ground implements Tillable, Waterable {

    /**
     * Constructor for the Dirt class.
     */
    public Dirt() {
        super('.', "Dirt");
    }

    /**
     * Applies the tilling effect to this Dirt.
     *
     * @param location the location containing the Dirt being tilled
     */
    @Override
    public void till(Location location) {
        location.setGround(new Soil());
    }

    /**
     * Returns the stamina cost required to water Dirt.
     *
     * @return the watering stamina cost
     */
    @Override
    public int getWateringCost() {
        return 2;
    }

    /**
     * Waters this Dirt and transforms it into a Puddle.
     *
     * @param actor the actor watering the Dirt
     * @param location the location containing the Dirt
     * @return a description of the watering result
     */
    @Override
    public String water(Actor actor, Location location) {
        location.setGround(new Puddle());
        return actor + " waters the Dirt into a Puddle.";
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
            String direction) {

        ActionList actions = new ActionList();

        if (direction.isEmpty()) {
            actions.add(new TillingAction(this, location));
            actions.add(new WateringAction(this, location));
        }

        return actions;
    }
}