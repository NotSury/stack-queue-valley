package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.AttackAction;
import game.navigation.GridDistance;
import game.navigation.WebPlanner;
import game.perception.SpiderTargetTracker;

/**
 * Walks towards and attacks the Spider's validated engaged Farmer.
 * Reuses the existing attack action and safe walking planner.
 *
 * @author Surya Muni Peragas
 * @author Lee Le Si
 * @version 1.2.0
 */
public class SpiderHuntBehaviour implements Behaviour<Actor, Action> {

    private final SpiderTargetTracker targetTracker;
    private final WebPlanner walkingPlanner;

    /**
     * Creates a behaviour that follows an existing engagement.
     *
     * @param targetTracker the shared Hunting target tracker
     * @param walkingPlanner the planner used for safe walking
     */
    public SpiderHuntBehaviour(
            SpiderTargetTracker targetTracker,
            WebPlanner walkingPlanner
    ) {
        this.targetTracker = targetTracker;
        this.walkingPlanner = walkingPlanner;
    }

    /**
     * Attacks the engaged Farmer when adjacent or walks towards them.
     * A temporarily blocked route produces no action and is retried later.
     *
     * @param actor the Spider taking its turn
     * @param location the Spider's current location
     * @return an attack or movement action, or null when none is available
     */
    @Override
    public Action operate(Actor actor, Location location) {
        Actor target = targetTracker.getEngagedTarget();

        if (target == null || !location.map().contains(target)) {
            return null;
        }

        Location targetLocation = location.map().locationOf(target);

        if (GridDistance.between(location, targetLocation) == 1) {
            for (Exit exit : location.getExits()) {
                if (exit.getDestination() == targetLocation) {
                    return new AttackAction(
                            target,
                            exit.getName(),
                            actor.getIntrinsicWeapon()
                    );
                }
            }
        }

        Exit step = walkingPlanner.findStepTowards(
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
