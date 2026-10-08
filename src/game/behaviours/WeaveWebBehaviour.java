package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.PlaceWebAction;
import game.interfaces.WebNetwork;
import game.navigation.GridDistance;
import game.navigation.WebPlanner;
/**
* Replans when a site becomes occupied, unsafe or otherwise unavailable.
* Uses only ordinary placement, so it stops when the six-web limit is
* reached instead of replacing webs.
*
* @author Surya Muni Peragas 35651628
* @author Lee Le Si 34599223
* @version 1.1.0
*/

public class WeaveWebBehaviour implements Behaviour<Actor, Action> {
    /**
     * Selects web sites and safe walking steps.
     */
    private final WebPlanner planner;
    /**
     * The owning Spider's web network.
     */
    private final WebNetwork network;
    /**
     * The web site currently being approached, or null.
     * Kept between turns so the Spider finishes one site before choosing another.
     */
    private Location placementTarget;

    /**
     * Creates a weaving behaviour for one Spider.
     *
     * @param planner the planner used to select sites and safe walking steps
     * @param network the owning Spider's web network
     */
    public WeaveWebBehaviour(WebPlanner planner, WebNetwork network) {
        this.planner = planner;
        this.network = network;
    }

    /**
     * Selects one walking action or one web placement action.
     *
     * @param actor the Spider operating this behaviour
     * @param location the Spider's current location
     * @return the selected action, or null when no useful action exists
     */
    @Override
    public Action operate(Actor actor, Location location) {

        // ordinary placement never replaces webs: stop once the network is full
        if (network.getRemainingCapacity() == 0) {
            placementTarget = null;
            return null;
        }

        if (!planner.isPlacementTargetValid(
                actor, location, placementTarget, network)) {
            placementTarget = planner.findPlacementTarget(
                    actor, location, network
            );
        }

        if (placementTarget == null) {
            return null;
        }

        if (GridDistance.between(location, placementTarget) == 1) {
            Location destination = placementTarget;
            placementTarget = null;

            return new PlaceWebAction(destination, network);
        }

        Exit step = planner.findStepToPlacement(
                actor, location, placementTarget
        );

        if (step == null) {
            // the route closed this turn: replan once straight away
            placementTarget = planner.findPlacementTarget(actor, location, network);

            if (placementTarget == null) {
                return null;
            }

            if (GridDistance.between(location, placementTarget) == 1) {
                Location destination = placementTarget;
                placementTarget = null;

                return new PlaceWebAction(destination, network);
            }

            step = planner.findStepToPlacement(actor, location, placementTarget);

            if (step == null) {
                placementTarget = null;
                return null;
            }
        }

        return new MoveActorAction(
                step.getDestination(),
                step.getName()
        );
    }

}
