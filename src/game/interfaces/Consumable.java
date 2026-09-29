package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
/**
 * Consumable represents an object that can be consumed by an actor.
 *
 * <p>Classes implementing this interface define the effect that occurs
 * when the object is consumed.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public interface Consumable {

    /**
     * Consumes this object and applies its consumption effect to the actor.
     *
     * @param actor the actor consuming the object
     * @return a description of the result of consumption
     */
    String consume(Actor actor);
}
