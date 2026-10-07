package game.perception;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.ActorRole;
import game.interfaces.WebNetwork;
import game.navigation.GridDistance;
import game.navigation.JumpPlanner;
import game.navigation.MovementPlanner;

/**
 * <p>Assesses Farmer targets and reachable web-placement opportunities for a
 * Spider with a fixed nest.</p>
 *
 * Queries do not move actors or change the web network.
 *
 * @author Khoo Lynn 34598901
 * @version 1.0.0
 */
public class SpiderTargetTracker {
    private static final int HUNT_RADIUS = 6;
    private static final int WEB_RADIUS = 3;
    private static final int MAX_WEBS = 6;

    private final Actor creature;
    private final EnvironmentScanner scanner;
    private final MovementPlanner movementPlanner;
    private final JumpPlanner jumpPlanner;
    private final WebNetwork webNetwork;

    public SpiderTargetTracker(Actor creature, EnvironmentScanner scanner, MovementPlanner movementPlanner,
                               JumpPlanner jumpPlanner, WebNetwork webNetwork){
        this.creature = creature;
        this.scanner = scanner;
        this.movementPlanner = movementPlanner;
        this.jumpPlanner = jumpPlanner;
        this.webNetwork = webNetwork;
    }

    public Actor findActiveTarget(Location origin){
        Location nest = webNetwork.getNestLocation();

        if (origin.map() != nest.map()){
            return null;
        }

        int scanRadius = GridDistance.between(origin, nest) + HUNT_RADIUS;

        Actor farmer = scanner.findNearestActorWith(origin, scanRadius, ActorRole.FARMER);

        if (farmer == null || !farmer.isConscious()){
            return null;
        }

        Location target = origin.map().locationOf(farmer);

        if (GridDistance.between(nest, target) > HUNT_RADIUS){
            return null;
        }

        if (canWalkToTarget(origin, target) || findUsefulLanding(origin, target) != null){
            return null;
        }

        return null;
    }

    public boolean canWalkToTarget(Location origin, Location target){
        if (origin.map() != target.map()){
            return false;
        }

        return GridDistance.between(origin, target) == 1 || movementPlanner.findStepTowards(creature, origin, target) != null;
    }

    public Location findUsefulLanding(Location origin, Location target){
        return jumpPlanner.findBestLanding(creature, origin, target);
    }

    public boolean hasWebGap(Location origin){
        Location nest = webNetwork.getNestLocation();

        if (origin.map() != nest.map() || webNetwork.countActiveWebs() >= MAX_WEBS){
            return false;
        }

        GameMap map = nest.map();

        for (int y = nest.y() - WEB_RADIUS; y <= nest.y() + WEB_RADIUS; y++){
            for (int x = nest.x() - WEB_RADIUS; x <= nest.x() + WEB_RADIUS; x++){
                if (!map.getXRange().contains(x) || !map.getYRange().contains(y)){
                    continue;
                }

                Location candidate = map.at(x, y);

                if (candidate == nest || !webNetwork.canPlaceAt(candidate)){
                    continue;
                }

                if (canReachPlacementPosition(origin, candidate)){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canReachPlacementPosition(Location origin, Location webLocation){
        for (Exit exit : webLocation.getExits()){
            Location position = exit.getDestination();

            if (position.map() != origin.map() || GridDistance.between(webLocation, position) != 1){
                continue;
            }

            if (position == origin){
                return true;
            }

            if (position.canActorEnter(creature) && movementPlanner.findStepTowards(creature, origin, position) != null){
                return true;
            }
        }
        return false;
    }
}
