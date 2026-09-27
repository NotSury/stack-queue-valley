package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TillAction;
import game.actions.WaterAction;
import game.capabilities.Tillable;
import game.capabilities.Waterable;

/**
 * Represents dirt ground that can be both tilled and watered.
 * Tilling dirt transforms it into soil, while watering dirt transforms
 * it into a puddle.
 */
public class Dirt extends Ground implements Tillable, Waterable {

    /**
     * Constructs a new dirt ground.
     */
    public Dirt() {
        super('.', "Dirt");
    }

    /**
     * Tills this dirt and replaces it with soil.
     *
     * @param actor the actor performing the tilling
     * @param location the location of this dirt
     * @return a description of the tilling result
     */
    @Override
    public String tilledBy(Actor actor, Location location) {
        location.setGround(new Soil());
        return actor + " tills the dirt into soil.";
    }

    /**
     * Waters this dirt and replaces it with a puddle.
     *
     * @param actor the actor performing the watering
     * @param location the location of this dirt
     * @return a description of the watering result
     */
    @Override
    public String wateredBy(Actor actor, Location location) {
        location.setGround(new Puddle());
        return actor + " waters the dirt into a puddle.";
    }

    /**
     * Returns the actions that an actor can perform on this dirt.
     * Tilling and watering are only offered when the actor is standing
     * directly on this ground.
     *
     * @param actor the Actor interacting with the ground
     * @param location the location of this dirt
     * @param direction the direction of this ground relative to the actor
     * @return the actions available for this dirt
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();

        if (direction.isEmpty()) {
            actions.add(new TillAction(this, location));
            actions.add(new WaterAction(this, location));
        }

        return actions;
    }
}
