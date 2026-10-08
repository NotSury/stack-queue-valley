package game.interfaces;

import edu.monash.fit2099.engine.positions.Location;

/**
 * Receives a report each time an owned web successfully catches an actor.
 *
 * <p>A web only needs this one method, so it does not depend on the rest
 * of the web network (Interface Segregation Principle).</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public interface WebTriggerRecorder {

    /**
     * Records that the web at a location has just caught an actor.
     * Called before the web removes itself, so the location is never lost.
     *
     * @param location the location of the triggered web
     */
    void recordTrigger(Location location);
}
