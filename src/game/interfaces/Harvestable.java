package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Represents an object that can be harvested by an actor.
 *
 * <p>Each harvestable object determines its own stamina cost and
 * harvesting behaviour. A harvestable object may also define whether
 * it is currently ready to be harvested.</p>
 *
 * @author <Chin Kai Xuan> <35730374>
 * @version 1.0.0
 */
public interface Harvestable {

    /**
     * Returns whether this object can currently be harvested.
     *
     * <p>Most crops can always be harvested, so the default behaviour
     * returns true.</p>
     *
     * @param actor the actor attempting to harvest
     * @param location the location containing the harvestable object
     * @return true if harvesting is currently allowed
     */
    default boolean canHarvest(Actor actor, Location location) {
        return true;
    }

    /**
     * Returns the message shown when harvesting is not currently allowed.
     *
     * @param actor the actor attempting to harvest
     * @return the harvest failure message
     */
    default String getHarvestFailureMessage(Actor actor) {
        return actor + " cannot harvest " + this + " yet.";
    }

    /**
     * Returns the stamina cost required to harvest this object.
     *
     * @return the stamina cost of harvesting
     */
    int getHarvestCost();

    /**
     * Performs the harvesting behaviour of this object.
     *
     * @param actor the actor performing the harvest
     * @param location the location containing the harvestable object
     * @return a description of the harvesting result
     */
    String harvest(Actor actor, Location location);
}