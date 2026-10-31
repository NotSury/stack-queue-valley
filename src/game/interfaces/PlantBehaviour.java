package game.interfaces;

import edu.monash.fit2099.engine.positions.Location;

/**
 * Represents an automatic behaviour that a plant can perform.
 *
 * <p>Each behaviour has an execution interval and returns whether it
 * successfully performed an action.</p>
 *
 * @author Khoo Lynn 34598901
 * @version 1.0.0
 */
public interface PlantBehaviour {

    /**
     * Returns how frequently the behaviour is attempted.
     * @return the interval in turns.
     */
    int getInterval();

    /**
     * Attempts to perform the behaviour.
     *
     * @param location the Location containing the plant.
     * @return true if the behaviour succeeded, false otherwise.
     */
    boolean perform(Location location);
}
