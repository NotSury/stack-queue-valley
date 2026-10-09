package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.items.ItemAbility;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.navigation.MovementPlanner;
import game.perception.EnvironmentScanner;

/**
 * Selects actions for seeking and collecting nearby portable items.
 * Delegates item detection to an environmental scanner and navigation
 * to a movement planner. Uses engine actions to perform movement
 * and pickup.
 *
 * @author Surya Muni Peragas 35651628
 * @version 1.0.0
 */
public class SeekItemBehaviour extends EnvironmentAwareBehaviour {

    private final int detectionRadius;

    /**
     * Creates a behaviour that seeks nearby portable items.
     *
     * @param scanner the scanner used to find reachable portable items
     * @param movementPlanner the planner used to approach item locations
     * @param detectionRadius the non-negative maximum item detection distance
     */
    public SeekItemBehaviour(EnvironmentScanner scanner, MovementPlanner movementPlanner, int detectionRadius) {
        super(scanner, movementPlanner);
        this.detectionRadius = detectionRadius;
    }

    /**
     * Selects an action for approaching or collecting a portable item.
     * Includes the actor's current location in the search. Collects at most one
     * item per turn and delegates pickup action creation to the item.
     *
     * @param actor the actor performing the behaviour
     * @param location the actor's current location
     * @return a pickup or movement action, or null if none is available
     */
    @Override
    public Action operate(Actor actor, Location location) {
        Location target = getScanner().findNearestPortableItemLocation(actor, location, 0, detectionRadius);

        if (target == null) {
            return null;
        }

        if (target == location) {
            for (Item item : location.getItems()) {
                if (item.hasAbility(ItemAbility.PORTABLE)) {
                    Action pickUpAction = item.getPickUpAction(actor);

                    if (pickUpAction != null) {
                        return pickUpAction;
                    }
                }
            }

            return null;
        }

        Exit nextStep = getMovementPlanner().findStepTowards(actor, location, target);

        if (nextStep == null) {
            return null;
        }

        return new MoveActorAction(nextStep.getDestination(), nextStep.getName());
    }
}
