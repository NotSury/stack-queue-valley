package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Represents something that can be watered.
 */
public interface Waterable {

    /**
     * Performs the effect of being watered.
     *
     * @param actor the actor performing the watering
     * @param location the location of the waterable object
     * @return description of the watering result
     */
    String wateredBy(Actor actor, Location location);
}
