package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import game.navigation.MovementPlanner;
import game.perception.EnvironmentScanner;

/**
 * Base class for actor behaviours that inspect the environment
 * and plan movement.
 * Concrete behaviours select an action through the engine's operate
 * method. Scanning and movement planning are delegated to injected
 * collaborators.
 *
 * @author Surya Muni Peragas 35651628
 * @version 1.0.0
 */
public abstract class EnvironmentAwareBehaviour implements Behaviour<Actor, Action> {

    private final EnvironmentScanner scanner;
    private final MovementPlanner movementPlanner;

    /**
     * Creates a behaviour with environmental and navigation support.
     *
     * @param scanner the scanner used to inspect the surroundings
     * @param movementPlanner the planner used to calculate movement steps
     */
    protected EnvironmentAwareBehaviour(EnvironmentScanner scanner, MovementPlanner movementPlanner) {
        this.scanner = scanner;
        this.movementPlanner = movementPlanner;
    }

    /**
     * Returns the scanner available to this behaviour.
     *
     * @return the environmental scanner
     */
    protected final EnvironmentScanner getScanner() {
        return scanner;
    }

    /**
     * Returns the movement planner available to this behaviour.
     *
     * @return the movement planner
     */
    protected final MovementPlanner getMovementPlanner() {
        return movementPlanner;
    }
}
