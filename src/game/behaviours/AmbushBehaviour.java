package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.ActorRole;
import game.navigation.MovementPlanner;
import game.perception.EnvironmentScanner;

/**
 * Selects an attack or movement action when a Farmer is detected nearby.
 * Gives the injected attack behaviour priority, then uses the movement
 * planner to approach the detected Farmer.
 *
 * @author Surya Muni Peragas 35651628
 * @version 1.0.0
 */
public class AmbushBehaviour extends EnvironmentAwareBehaviour{

    private final Behaviour<Actor, Action> attackBehaviour;
    private final int detectionRadius;

    /**
     * Creates an ambush behaviour with Farmer detection, movement planning
     * and an attack-selection collaborator.
     *
     * @param scanner the environmental scanner used to detect Farmers
     * @param movementPlanner the planner used to approach the Farmer
     * @param attackBehaviour the behaviour used to select an attack
     * @param detectionRadius the maximum Farmer detection distance
     */
    public AmbushBehaviour(EnvironmentScanner scanner, MovementPlanner movementPlanner, Behaviour<Actor, Action> attackBehaviour, int detectionRadius) {
        super(scanner, movementPlanner);
        this.attackBehaviour = attackBehaviour;
        this.detectionRadius = detectionRadius;
    }

    /**
     * Detects the nearest conscious Farmer and requests an attack first.
     * If no attack is selected, requests a movement step towards that Farmer.
     *
     * @param actor the actor performing the ambush
     * @param location the actor's current location
     * @return the selected attack or movement action, or null when no Farmer
     * is detected or neither action is available
     */
    @Override
    public Action operate(Actor actor, Location location) {
        Actor farmer = getScanner().findNearestActorWith(location, detectionRadius, ActorRole.FARMER);

        if (farmer == null) {
            return null;
        }

        Action attack = attackBehaviour.operate(actor, location);

        if (attack != null) {
            return attack;
        }

        Location farmerLocation = location.map().locationOf(farmer);

        Exit step = getMovementPlanner().findStepTowards(actor, location, farmerLocation);

        if (step == null) {
            return null;
        }

        return new MoveActorAction(step.getDestination(), step.getName());
    }
}
