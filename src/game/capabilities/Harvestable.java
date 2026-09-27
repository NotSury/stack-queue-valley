package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Represents a crop or ground type that can be harvested by an actor.
 */
public interface Harvestable {
    /**
     * Harvests this object at the given location.
     *
     * @param actor the actor performing the harvest
     * @param location the location of the object being harvested
     * @return a description of the harvesting result
     */
    String harvestedBy(Actor actor, Location location);
}
