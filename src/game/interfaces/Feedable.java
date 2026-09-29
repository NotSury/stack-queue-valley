package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
/**
 * Feedable represents an actor that can be fed by the Farmer.
 *
 * <p>Classes implementing this interface define their own effects that
 * occur when they are successfully fed.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public interface Feedable {
    /**
     * Applies the feeding effect to this feedable actor.
     *
     * @param farmer the actor feeding this feedable actor
     * @param location the location of the feedable actor
     * @return a description of the feeding result
     */
    String feed(Actor farmer, Location location);
}
