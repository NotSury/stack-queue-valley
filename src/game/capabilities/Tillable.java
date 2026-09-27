package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Represents an object that can be tilled by an actor.
 */
public interface Tillable {

    /**
     * Performs the effect of tilling this object.
     *
     * @param actor the actor performing the tilling
     * @param location the location of the object being tilled
     * @return a description of the result of tilling
     */
    String tilledBy(Actor actor, Location location);
}
