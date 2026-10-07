package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.JumpAction;
import game.capabilities.ActorRole;
import game.navigation.JumpPlanner;
import game.perception.EnvironmentScanner;

/**
 * Selects useful jump towards a detected Farmer.
 *
 * <p>Delegates target detection and jump planning to injected collaborators.
 * Returns no action when jumping offers no useful approach.</p>
 *
 * @author Khoo Lynn 34598901
 * @version 1.0.0
 */
public class PounceBehaviour implements Behaviour<Actor, Action> {
    private final EnvironmentScanner scanner;
    private final JumpPlanner jumpPlanner;
    private final int detectionRadius;

    /**
     * Creates a behaviour with detection and jump-planning support.
     *
     * @param scanner the scanner used to detect the Farmer.
     * @param jumpPlanner the planner used to select useful landings.
     * @param detectionRadius the maximum Farmer detection distance.
     */
    public PounceBehaviour(EnvironmentScanner scanner, JumpPlanner jumpPlanner, int detectionRadius){
        this.scanner = scanner;
        this.jumpPlanner = jumpPlanner;
        this.detectionRadius = detectionRadius;
    }

    /**
     * Selects a jump towards the nearest detected Farmer.
     *
     * @param actor The entity performing the behaviour
     * @param location The location of the current entity
     * @return a jump action or null if no useful jump exists.
     */
    @Override
    public Action operate(Actor actor, Location location){
        Actor farmer = scanner.findNearestActorWith(location, detectionRadius, ActorRole.FARMER);

        if (farmer == null){
            return null;
        }

        Location farmerLocation = location.map().locationOf(farmer);

        Location landing = jumpPlanner.findBestLanding(actor, location, farmerLocation);

        if (landing == null){
            return null;
        }

        return new JumpAction(landing, jumpPlanner);
    }
}
