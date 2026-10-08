package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.JumpAction;
import game.interfaces.WebAlertSource;
import game.navigation.JumpPlanner;
import game.navigation.WebPlanner;
import game.traps.WebAlert;

/**
 * Moves towards the location of the most recently triggered web.
 *
 * <p>Each turn the latest alert is read again, so the creature
 * redirects at once when a newer web is triggered. A useful two-tile
 * jump is preferred; otherwise the creature takes one safe walking
 * step. No action is returned once the creature has arrived, when
 * there is no alert, or when the alert cannot be approached.</p>
 *
 * @author Khoo Lynn 34598901
 * @author Lee Le Si 34599223
 * @version 1.1.0
 */

public class PounceBehaviour implements Behaviour<Actor, Action> {
    /**
     * Source of the latest web-trigger alert.
     */
    private final WebAlertSource alerts;
    /**
     * Plans legal and useful two-tile jumps.
     */
    private final JumpPlanner jumpPlanner;
    /**
     * Plans safe walking steps that avoid hazards.
     */
    private final WebPlanner walkingPlanner;

    /**
     * Creates a pounce behaviour that follows web alerts.
     *
     * @param alerts the source of the latest web alert
     * @param jumpPlanner the planner used to select useful landings
     * @param walkingPlanner the planner used for safe walking steps
     */
    public PounceBehaviour(WebAlertSource alerts, JumpPlanner jumpPlanner, WebPlanner walkingPlanner){
        this.alerts = alerts;
        this.jumpPlanner = jumpPlanner;
        this.walkingPlanner = walkingPlanner;
    }

    /**
     * Selects a jump or a safe step towards the latest alert location.
     *
     * <p>Order: (1) no alert or alert on another map: nothing;
     * (2) already on or next to the alert: nothing, the state decides
     * what happens next; (3) a useful jump exists: jump;
     * (4) otherwise one safe walking step towards a tile next to the
     * alert, if any.</p>
     *
     * @param actor the creature operating this behaviour
     * @param location the creature's current location
     * @return a jump or movement action, or null
     */
    @Override
    public Action operate(Actor actor, Location location){
        WebAlert alert = alerts.getLatestAlert();
        if (alert == null){
            return null;
        }

        Location target = alert.getLocation();
        if (target.map() != location.map() || jumpPlanner.hasArrived(location, target)){
            return null;
        }

        Location landing = jumpPlanner.findBestLanding(actor,location,target);
        if (landing != null){
            return new JumpAction(landing, jumpPlanner);
        }

        Exit step = walkingPlanner.findStepToPlacement(actor,location,target);
        if  (step == null){
            return null;
        }
        return new MoveActorAction(step.getDestination(),step.getName());
    }
}
