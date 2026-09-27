package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Represents an actor that can be fed by another actor
 */
public interface Feedable {

    /**
     * Applies the specific effects of feeding this actor.
     *
     * @param actor the actor performing the feeding
     * @param actorLocation the location of the actor performing the feeding
     */
    void fedBy(Actor actor, Location actorLocation);
}
