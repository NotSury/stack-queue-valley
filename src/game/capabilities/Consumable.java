package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;

/**
 * Represents an object that can be consumed by an actor.
 */
public interface Consumable {

    /**
     * Applies the effects of consuming this object.
     *
     * @param actor the actor consuming the object
     * @return a description of the consumption result
     */
    String consumedBy(Actor actor);
}
