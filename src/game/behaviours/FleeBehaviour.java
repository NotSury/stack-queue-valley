package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.navigation.MovementPlanner;
import game.perception.EnvironmentScanner;

/**
 * Selects movement away from a nearby Farmer.
 * Uses the environmental scanner to detect a threat and the movement planner
 * to select an adjacent step that increases threat distance.
 */
public class FleeBehaviour extends EnvironmentAwareBehaviour {

    private final int detectionRadius;

    /**
     * Creates a behaviour that detects and flees from nearby Farmers.
     *
     * @param scanner the scanenr used to detect Farmers
     * @param movementPlanner the planner used to select escape steps
     * @param detectionRadius the non-negative maximum threat detection distance
     */
    public FleeBehaviour(EnvironmentScanner scanner, MovementPlanner movementPlanner, int detectionRadius) {
        super(scanner, movementPlanner);
        this.detectionRadius = detectionRadius;
    }

    /**
     * Selects a one-tile movement action away from a detected Farmer.
     * Returns null when no Farmer is detected or no valid step
     * increases the distance from the Farmer.
     *
     * @param actor the actor performing the behaviour
     * @param location the location of the current actor
     * @return an escape movement action, or null if none is available
     */
    @Override
    public Action operate(Actor actor, Location location) {
        Actor farmer = getScanner().findFarmer(location, detectionRadius);

        if (farmer == null) {
            return null;
        }

        Location threatLocation = location.map().locationOf(farmer);

        Exit escapeStep = getMovementPlanner().findStepAway(actor, location, threatLocation);

        if (escapeStep == null) {
            return null;
        }

        return new MoveActorAction(escapeStep.getDestination(), escapeStep.getName());
    }
}
