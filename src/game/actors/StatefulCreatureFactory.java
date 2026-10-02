package game.actors;

import game.behaviours.DirectionalMoveBehaviour;
import game.navigation.MovementPlanner;

/**
 * Constructs creatures and assembles their collaborating behaviours.
 * Creation details are kept here so that states do not need to construct
 * the behaviours used by newly created creatures.
 */
public class StatefulCreatureFactory {

    private final MovementPlanner movementPlanner;

    /**
     * Create a factory using the supplied movement planner.
     *
     * @param movementPlanner the planner used by creature movement behaviours
     */
    public StatefulCreatureFactory(MovementPlanner movementPlanner) {
        this.movementPlanner = movementPlanner;
    }

    /**
     * Creates an unplaced decoy that resembles the original Mimic.
     * The decoy continues in the supplied direction during its own turns.
     * Its existing turn action manages its limited lifetime.
     *
     * @param original the Mimic whose appearance is copied
     * @param direction the direction obtained from the selected spawn exit
     * @return the newly created decoy
     */
    public DecoyMimic createDecoy(CaveMimic original, String direction) {
        return new DecoyMimic(original, new DirectionalMoveBehaviour(movementPlanner, direction));
    }
}
