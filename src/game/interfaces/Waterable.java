package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Represents an object that can be watered by an actor.
 *
 * <p>Each waterable object determines its own stamina cost and the
 * effect that occurs when it is watered.</p>
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public interface Waterable {

    /**
     * Returns the stamina cost required to water this object.
     *
     * @return the stamina cost of watering
     */
    int getWateringCost();

    /**
     * Applies the watering effect of this object.
     *
     * @param actor the actor performing the watering action
     * @param location the location containing the waterable object
     * @return a description of the watering result
     */
    String water(Actor actor, Location location);
}