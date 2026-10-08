package game.navigation;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.MovementHazard;
import game.statuses.ChargedStatus;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Plans legal two-tile jumps and safe approaches on a single map.
 *
 * <p>A jump moves exactly two tiles in a straight or diagonal line and
 * must land on an enterable, hazard-free tile. A landing is useful only
 * if it reaches the target in fewer actions than walking. Arrival and
 * approach checks let a creature tell when it has reached a location,
 * or that the location cannot be reached for now.</p>
 *
 * @author Khoo Lynn 34598901
 * @author Lee Le Si 34599223
 * @version 1.1.0
 */

public class MapJumpPlanner implements JumpPlanner{
    private static final int JUMP_DISTANCE = 2;
    private static final int UNREACHABLE = Integer.MAX_VALUE;

    @Override
    public Location findBestLanding(Actor creature, Location origin, Location target){
        if (origin.map() != target.map() || origin == target){
            return null;
        }

        int walkingActions = walkingDistanceToTarget(creature, origin, target, origin);
        Location bestLanding = null;
        int bestActionCount = UNREACHABLE;
        int bestOnwardExits = -1;

        GameMap map = origin.map();

        for (int vertical = -1; vertical <= 1; vertical++){
            for (int horizontal = -1; horizontal <= 1; horizontal++){
                if (horizontal == 0 && vertical == 0){
                    continue;
                }

                int landingX = origin.x() + horizontal * JUMP_DISTANCE;
                int landingY = origin.y() + vertical * JUMP_DISTANCE;

                if (!map.getXRange().contains(landingX) || !map.getYRange().contains(landingY)){
                    continue;
                }

                Location landing = map.at(landingX, landingY);

                if (landing == target || !isLegalJump(creature, origin, landing)){
                    continue;
                }

                int remainWalkingActions = walkingDistanceToTarget(creature, landing, target, origin);

                if (remainWalkingActions == UNREACHABLE){
                    continue;
                }

                int totalActions = 1 + remainWalkingActions;

                if (totalActions >= walkingActions){
                    continue;
                }

                int onwardExits = countOnwardsExits(creature, landing, origin);

                if (isBetterLanding(landing, totalActions, onwardExits, bestLanding, bestActionCount, bestOnwardExits)){
                    bestLanding = landing;
                    bestActionCount = totalActions;
                    bestOnwardExits = onwardExits;
                }
            }
        }
        return bestLanding;
    }

    @Override
    public boolean isLegalJump(Actor creature, Location origin, Location landing){
        if (origin.map() != landing.map()){
            return false;
        }

        int horizontalDistance = Math.abs(landing.x() - origin.x());
        int verticalDistance = Math.abs(landing.y() - origin.y());

        boolean validHorizontalDistance = horizontalDistance == 0 || horizontalDistance == JUMP_DISTANCE;
        boolean validVerticalDistance = verticalDistance == 0 || verticalDistance == JUMP_DISTANCE;

        if (!validHorizontalDistance || !validVerticalDistance || Math.max(horizontalDistance, verticalDistance) != JUMP_DISTANCE){
            return false;
        }

        GameMap map = origin.map();

        if (!map.getXRange().contains(landing.x()) || !map.getYRange().contains(landing.y())){
            return false;
        }

        return landing.canActorEnter(creature) && isSafe(landing);
    }

    @Override
    public boolean hasArrived(Location origin, Location target){
        return origin.map() == target.map() && GridDistance.between(origin, target) <=1;
    }

    @Override
    public boolean canApproach(Actor creature, Location origin, Location target){
        if (origin.map() != target.map()) {
            return false;
        }
        if (hasArrived(origin, target)){
            return true;
        }
        return findBestLanding(creature, origin, target) != null
                || walkingDistanceToTarget(creature,origin, target, origin) != UNREACHABLE;
    }

    private int walkingDistanceToTarget(Actor creature, Location start, Location target, Location vacatedOrigin){
        Queue<Location> pending = new ArrayDeque<>();
        Map<Location, Integer> distances = new HashMap<>();

        pending.add(start);
        distances.put(start, 0);

        while (!pending.isEmpty()){
            Location current = pending.remove();
            int currentDistance = distances.get(current);

            if (GridDistance.between(current, target) == 1){
                return currentDistance;
            }

            for (Exit exit : current.getExits()){
                Location destination = exit.getDestination();

                if (!isLocalStep(current, destination) || destination == target
                        || distances.containsKey(destination) || !canWalkOn(
                                creature, destination, vacatedOrigin)){
                    continue;
                }

                distances.put(destination, currentDistance + 1);
                pending.add(destination);
            }
        }
        return UNREACHABLE;
    }

    private int countOnwardsExits(Actor creature, Location landing, Location vacatedOrigin){
        int count = 0;

        for (Exit exit : landing.getExits()){
            Location destination = exit.getDestination();

            if (isLocalStep(landing, destination) && canWalkOn(creature, destination, vacatedOrigin)){
                count++;
            }
        }

        return count;
    }

    private boolean canWalkOn(Actor creature, Location location, Location vacatedOrigin){
        boolean enterable;

        if (location == vacatedOrigin){
            enterable = location.getGround().canActorEnter(creature);
        } else{
            enterable = location.canActorEnter(creature);
        }

        return enterable && isSafe(location);
    }

    private boolean isSafe(Location location){
        if (location.getGround().hasStatus(ChargedStatus.class) || location.getGround().asCapability(MovementHazard.class).isPresent()){
            return false;
        }

        for (Item item: location.getItems()){
            if (item.asCapability(MovementHazard.class).isPresent()){
                return false;
            }
        }

        return true;
    }

    private boolean isLocalStep(Location origin, Location destination){
        return origin.map() == destination.map() && GridDistance.between(origin, destination) == 1;
    }

    private boolean isBetterLanding(Location candidate, int candidateActions, int candidateExits, Location bestLanding, int bestActions, int bestExits){
        if (bestLanding == null){
            return true;
        }

        if (candidateActions != bestActions){
            return candidateActions < bestActions;
        }

        if (candidateExits != bestExits){
            return candidateExits > bestExits;
        }

        if (candidate.y() != bestLanding.y()){
            return candidate.y() < bestLanding.y();
        }

        return candidate.x() < bestLanding.x();
    }
}
