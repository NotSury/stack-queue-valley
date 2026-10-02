package game.navigation;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Plans creature movement using the map's exits.
 * Respects terrain restrictions and occupied tiles. Only adjacent
 * exits on the current map are considered.
 */
public class MapMovementPlanner implements MovementPlanner {

    /**
     * Finds the first step along a shortest valid movement route.
     * Uses breadth-first search. An occupied target is approached
     * rather than entered. Equal-length routes use the engine's
     * exit ordering.
     *
     * @param creature the creature that would perform the movement
     * @param origin the creature's current location
     * @param target the location to reach or approach
     * @return the first exit, or null if no movement is needed
     *         or no valid route exists
     */
    @Override
    public Exit findStepTowards(
            Actor creature,
            Location origin,
            Location target) {

        if (origin.map() != target.map()) {
            return null;
        }

        Queue<Location> pending = new ArrayDeque<>();
        Map<Location, Exit> firstSteps = new HashMap<>();

        pending.add(origin);
        firstSteps.put(origin, null);

        while (!pending.isEmpty()) {
            Location current = pending.remove();

            if (current == target
                    || (target.containsAnActor()
                    && GridDistance.between(current, target) == 1)) {
                return firstSteps.get(current);
            }

            for (Exit exit : current.getExits()) {
                Location destination = exit.getDestination();

                if (!isLocalStep(current, destination)
                        || firstSteps.containsKey(destination)
                        || !destination.canActorEnter(creature)) {
                    continue;
                }

                Exit firstStep = current == origin
                        ? exit : firstSteps.get(current);

                firstSteps.put(destination, firstStep);
                pending.add(destination);
            }
        }

        return null;
    }

    /**
     * Selects a valid neighboring exit that increases threat distance.
     * @param creature the creature that would perform the movement
     * @param origin the creature's current location
     * @param threat the threatening actor's location
     * @return an escape exit, or null if no improving step exists
     */
    @Override
    public Exit findStepAway(Actor creature, Location origin, Location threat) {
        return findStepAway(creature, origin, threat, null);
    }

    /**
     * Selects an improving escape step while excluding a reserved destination.
     * Chooses the greatest resulting distance. Equal-distance choices
     * use the engine's exit ordering.
     *
     * @param creature the creature that would perform the movement
     * @param origin the creature's current location
     * @param threat the threatening actor's location
     * @param excludedExit the exit whose destination must be excluded, or null
     * when no destination is reserved
     * @return an escape exit, or null if no improving step exists
     */
    @Override
    public Exit findStepAway(Actor creature, Location origin, Location threat, Exit excludedExit) {
        if (origin.map() != threat.map()) {
            return null;
        }

        Exit bestExit = null;
        int bestDistance = GridDistance.between(origin, threat);

        for (Exit exit : origin.getExits()) {
            Location destination = exit.getDestination();

            if (excludedExit != null
                    && destination == excludedExit.getDestination()) {
                continue;
            }

            if (!isLocalStep(origin, destination)
                    || !destination.canActorEnter(creature)) {
                continue;
            }

            int candidateDistance =
                    GridDistance.between(destination, threat);

            if (candidateDistance > bestDistance) {
                bestExit = exit;
                bestDistance = candidateDistance;
            }
        }

        return bestExit;
    }

    /**
     * Finds a valid adjacent exit matching a grid direction.
     * Returns null when the matching direction is blocked or unavailable.
     * Cross-map exits and non-adjacent destinations are excluded.
     *
     * @param creature the creature that would perform the movement
     * @param origin the creature's current location
     * @param direction the directional exit name supplied by the engine
     * @return a matching movement exit, or null if none is available
     */
    @Override
    public Exit findStepInDirection(Actor creature, Location origin, String direction) {
        for (Exit exit : origin.getExits()) {
            Location destination = exit.getDestination();

            if (exit.getName().equals(direction) && isLocalStep(origin, destination) && destination.canActorEnter(creature)) {
                return exit;
            }
        }

        return null;
    }

    private boolean isLocalStep(
            Location origin,
            Location destination) {

        return origin.map() == destination.map()
                && GridDistance.between(origin, destination) == 1;
    }
}