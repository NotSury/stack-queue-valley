package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
/**
 * Harvestable represents a ground or crop that can be harvested by an actor.
 *
 * <p>Each harvestable object defines the stamina cost required for
 * harvesting and the specific result produced when harvested.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public interface Harvestable {

    /**
     * Returns the stamina cost required to harvest this object.
     *
     * @return the stamina cost of harvesting
     */
    int getHarvestCost();
    /**
     * Harvests this object and applies its specific harvesting effect.
     *
     * @param actor the actor performing the harvest
     * @param location the location of the harvestable object
     * @return a description of the harvesting result
     */
    String harvest(Actor actor, Location location);
}
