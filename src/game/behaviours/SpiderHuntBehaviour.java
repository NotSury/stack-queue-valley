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
 * @author Lee Le Si
 * @version 1.1.0
 */
public class SpiderHuntBehaviour implements Behaviour<Actor, Action> {

    private final SpiderTargetTracker targetTracker;
    private final Behaviour<Actor, Action> pursuitBehaviour;

    /**
     * Creates a hunting behaviour.
     *
     * @param targetTracker the owning Spider's target assessment
     * @param pursuitBehaviour the behaviour that chases and attacks,
     *                         e.g. an AmbushBehaviour with safe walking
     */
    public SpiderHuntBehaviour(SpiderTargetTracker targetTracker, Behaviour<Actor, Action> pursuitBehaviour) {
        this.targetTracker = targetTracker;
        this.pursuitBehaviour = pursuitBehaviour;
    }

    /**
     * Chases the Farmer only while he is a valid target for the Spider.
     *
     * @param actor the Spider operating this behaviour
     * @param location the Spider's current location
     * @return an attack or movement action, or null if there is no target
     */
    @Override
    public Action operate(Actor actor, Location location) {
    if (targetTracker.findActiveTarget(location) == null) {
        return null;
    }
    return pursuitBehaviour.operate(actor, location);
    }
}
