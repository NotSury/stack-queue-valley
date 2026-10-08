package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.navigation.GridDistance;
import game.navigation.WebPlanner;
import game.perception.SpiderTargetTracker;

/**
 * Pursues an active Farmer through safe walking routes.
 * Delegates adjacent attacks to an injected combat behaviour.
 *
 * @author Surya Muni Peragas
 * @version 1.0.0
 */
public class SpiderHuntBehaviour implements Behaviour<Actor, Action> {

    private final SpiderTargetTracker targetTracker;
    private final WebPlanner planner;
    private final Behaviour<Actor, Action> attackBehaviour;

    /**
     * Creates a hunting behaviour using shared target assessment.
     *
     * @param targetTracker the owning Spider's target assessment
     * @param planner the planner used for safe walking routes
     * @param attackBehaviour the behaviour used for adjacent attacks
     */
    public SpiderHuntBehaviour(SpiderTargetTracker targetTracker, WebPlanner planner, Behaviour<Actor, Action> attackBehaviour) {
        this.targetTracker = targetTracker;
        this.planner = planner;
        this.attackBehaviour = attackBehaviour;
    }

    /**
     * Selects an adjacent attack or one safe step towards the active Farmer.
     * Returns no action when no target or useful walking step is available.
     *
     * @param actor the Spider operating this behaviour
     * @param location the Spider's current location
     * @return an attack or movement action, or null
     */
    @Override
    public Action operate(Actor actor, Location location) {
        Actor target = targetTracker.findActiveTarget(location);

        if (target == null) {
            return null;
        }
        Location targetLocation = location.map().locationOf(target);

        if (GridDistance.between(location, targetLocation) == 1) {
            return attackBehaviour.operate(actor, location);
        }

        Exit step = planner.findStepTowards(
                actor,
                location,
                targetLocation
        );

        if (step == null) {
            return null;
        }

        return new MoveActorAction(
                step.getDestination(),
                step.getName()
        );
    }
}
