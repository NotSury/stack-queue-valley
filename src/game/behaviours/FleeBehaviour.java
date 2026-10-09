package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.ActorRole;
import game.navigation.MovementPlanner;
import game.perception.EnvironmentScanner;

/**
 * Selects movement away from a nearby Farmer.
 * Uses an environmental scanner to detect the threat and an injected
 * movement planner to select the first step of an escape route.
 * Supports distance-preserving movement when no improving route exists.
 *
 * @author Surya Muni Peragas 35651628
 * @version 1.0.0
 */
public class FleeBehaviour extends EnvironmentAwareBehaviour {

    private final int detectionRadius;

    /**
     * Creates a behaviour that detects and flees from nearby Farmers.
     *
     * @param scanner the scanner used to detect Farmers
     * @param movementPlanner the planner used to select escape steps
     * @param detectionRadius the non-negative maximum threat detection distance
     */
    public FleeBehaviour(EnvironmentScanner scanner, MovementPlanner movementPlanner, int detectionRadius) {
        super(scanner, movementPlanner);
        this.detectionRadius = detectionRadius;
    }

    /**
     * Selects the first movement step of a bounded escape route.
     * Replans each turn using the current terrain and actor positions.
     * Prefers routes ending farther from the Farmer, falling back to
     * distance-preserving routes when no improving route exists.
     * The selected first step never moves closer to the Farmer.
     *
     * @param actor the actor performing the behaviour
     * @param location the actor's current location
     * @return an escape movement action, or null when no valid
     * non-approaching movement is available
     */
    @Override
    public Action operate(Actor actor, Location location) {
        Actor farmer = getScanner().findNearestActorWith(location, detectionRadius, ActorRole.FARMER);

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
