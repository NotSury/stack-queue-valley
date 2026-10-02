package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.navigation.MovementPlanner;

/**
 * Selects one-tile movement in a fixed grid direction.
 * Delegates movement validation to a planner and remains idle
 * when the selected direction is blocked.
 */
public class DirectionalMoveBehaviour implements Behaviour<Actor, Action> {

    private final MovementPlanner movementPlanner;
    private final String direction;

    /**
     * Creates a behaviour that follows a fixed grid direction.
     *
     * @param movementPlanner the planner used to find valid movement steps
     * @param direction the directional exit name supplied by the engine
     */
    public DirectionalMoveBehaviour(MovementPlanner movementPlanner, String direction) {
        this.movementPlanner = movementPlanner;
        this.direction = direction;
    }

    /**
     * Selects movement in the configured direction.
     *
     * @param actor the actor performing the behaviour
     * @param location the actor's current location
     * @return a movement action, or null if the direction is unavailable
     */
    @Override
    public Action operate(Actor actor, Location location) {
        Exit nextStep = movementPlanner.findStepInDirection(actor, location, direction);

        if (nextStep == null) {
            return null;
        }

        return new MoveActorAction(nextStep.getDestination(), nextStep.getName());
    }
}
