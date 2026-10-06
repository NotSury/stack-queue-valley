package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Represents something that can be broken by an actor.
 *
 * @author Khoo Lynn 34598901
 * @version 1.0.0
 */
public interface Breakable {

    /**
     * Performs the result of breaking this object.
     *
     * @param actor actor breaking the object.
     * @param location location of the object being broken.
     * @return description of the result.
     */
    String breakBy(Actor actor, Location location);
}
