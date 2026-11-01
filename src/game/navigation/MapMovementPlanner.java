package game.navigation;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.support.MovementRestrictions;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Plans creature movement using the map's exits.
 * Respects terrain restrictions and occupied tiles. Only adjacent
 * exits on the current map are considered.
 *
 * @author Surya Muni Peragas 35651628
 * @author <Vanessa Liau Jia Yee> <34599398>
 * @version 1.2.0
 */
public class MapMovementPlanner implements MovementPlanner {

    private static final int MAXIMUM_ESCAPE_STEPS = 3;

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
                        || !destination.canActorEnter(creature)
                        || MovementRestrictions.isForbidden(creature, destination)) {
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
     * Finds the first step of a bounded escape route.
     * Prefers routes that increase distance from the threat.
     * When no improving route is found, a non-approaching route
     * that preserves the current grid distance may be used instead.
     * The first movement step never reduces the distance to the threat.
     *
     * @param creature the creature that would perform the movement
     * @param origin the creature's current location
     * @param threat the threatening actor's location
     * @return an escape exit, or null if no valid non-approaching route exists
     */
    @Override
    public Exit findStepAway(Actor creature, Location origin, Location threat) {
        return findStepAway(creature, origin, threat, null);
    }

    /**
     * Searches escape routes up to three movement steps long.
     * Prefers routes that increase grid distance from the threat.
     * If none are available, considers non-approaching routes that
     * preserve the original grid distance.
     *
     * Improving routes prefer an onward exit, greater threat distance
     * and then shorter route length. Neutral routes prefer an onward
     * exit, greater Manhattan separation and then shorter route length.
     * Remaining ties follow the engine's exit ordering.
     *
     * The first step never moves closer to the threat, and the reserved
     * destination is excluded throughout the search.
     *
     * @param creature the creature that would perform the movement
     * @param origin the creature's current location
     * @param threat the threatening actor's location
     * @param excludedExit the exit whose destination must be excluded, or null
     * when no destination is reserved
     * @return the first escape exit, or null if no valid non-approaching route exists
     */
    @Override
    public Exit findStepAway(Actor creature, Location origin, Location threat, Exit excludedExit) {
        if (origin.map() != threat.map()) {
            return null;
        }

        Location excludedDestination = excludedExit == null
                ? null : excludedExit.getDestination();

        Queue<Location> pending = new ArrayDeque<>();
        Map<Location, Exit> firstSteps = new HashMap<>();
        Map<Location, Integer> depths = new HashMap<>();

        pending.add(origin);
        firstSteps.put(origin, null);
        depths.put(origin, 0);

        int originalDistance = GridDistance.between(origin, threat);

        Exit bestExit = null;
        boolean bestHasOnwardExit = false;
        int bestDistance = originalDistance;
        int bestDepth = Integer.MAX_VALUE;
        Exit bestNeutralExit = null;
        boolean bestNeutralHasOnwardExit = false;
        int bestNeutralManhattanDistance = Integer.MIN_VALUE;
        int bestNeutralDepth = Integer.MAX_VALUE;

        while (!pending.isEmpty()) {
            Location current = pending.remove();
            int currentDepth = depths.get(current);

            if (currentDepth >= MAXIMUM_ESCAPE_STEPS) {
                continue;
            }

            for (Exit exit : current.getExits()) {
                Location destination = exit.getDestination();

                if (!isLocalStep(current, destination)
                        || destination == excludedDestination
                        || firstSteps.containsKey(destination)
                        || !destination.canActorEnter(creature)
                        || MovementRestrictions.isForbidden(creature, destination))
                {
                    continue;
                }

                int candidateDistance =
                        GridDistance.between(destination, threat);

                if (current == origin
                        && candidateDistance < originalDistance) {
                    continue;
                }

                Exit firstStep = current == origin
                        ? exit : firstSteps.get(current);

                int candidateDepth = currentDepth + 1;

                firstSteps.put(destination, firstStep);
                depths.put(destination, candidateDepth);
                pending.add(destination);

                if (candidateDistance < originalDistance) {
                    continue;
                }

                boolean candidateHasOnwardExit =
                        hasOnwardExit(creature, destination, current, excludedDestination);

                if (candidateDistance == originalDistance) {
                    int candidateManhattanDistance =
                            Math.abs(destination.x() - threat.x())
                                    + Math.abs(destination.y() - threat.y());

                    boolean betterNeutralCandidate =
                            bestNeutralExit == null
                                    || (candidateHasOnwardExit && !bestNeutralHasOnwardExit)
                                    || (candidateHasOnwardExit == bestNeutralHasOnwardExit
                                    && candidateManhattanDistance > bestNeutralManhattanDistance)
                                    || (candidateHasOnwardExit == bestNeutralHasOnwardExit
                                    && candidateManhattanDistance == bestNeutralManhattanDistance
                                    && candidateDepth < bestNeutralDepth);

                    if (betterNeutralCandidate) {
                        bestNeutralExit = firstStep;
                        bestNeutralHasOnwardExit = candidateHasOnwardExit;
                        bestNeutralManhattanDistance = candidateManhattanDistance;
                        bestNeutralDepth = candidateDepth;
                    }

                    continue;
                }

                boolean betterCandidate =
                        bestExit == null
                                || (candidateHasOnwardExit && !bestHasOnwardExit)
                                || (candidateHasOnwardExit == bestHasOnwardExit
                                && candidateDistance > bestDistance)
                                || (candidateHasOnwardExit == bestHasOnwardExit
                                && candidateDistance == bestDistance
                                && candidateDepth < bestDepth);

                if (betterCandidate) {
                    bestExit = firstStep;
                    bestHasOnwardExit = candidateHasOnwardExit;
                    bestDistance = candidateDistance;
                    bestDepth = candidateDepth;
                }
            }
        }

        return bestExit != null ? bestExit : bestNeutralExit;
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

            if (exit.getName().equals(direction) && isLocalStep(origin, destination)
                    && destination.canActorEnter(creature)
                    && !MovementRestrictions.isForbidden(creature, destination)) {
                return exit;
            }
        }

        return null;
    }

    /**
     * Creates separate escape steps for a creature and its decoy.
     * The creature's selected destination is excluded when choosing
     * the decoy's route so that they begin escaping in different directions.
     *
     * @param creature the creature preparing to escape
     * @param origin the creature's current location
     * @param threat the threatening actor's location
     * @return an escape plan, or null when two distinct escape steps
     * cannot be selected
     */
    @Override
    public EscapePlan planEscape(Actor creature, Location origin, Location threat) {
        Exit mimicStep = findStepAway(creature, origin, threat);

        if (mimicStep == null) {
            return null;
        }

        Exit decoyStep = findStepAway(creature, origin, threat, mimicStep);

        if (decoyStep == null) {
            return null;
        }

        return new EscapePlan(mimicStep, decoyStep);
    }

    private boolean hasOnwardExit(Actor creature, Location location, Location previousLocation, Location excludeDestination) {
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();

            if (destination != previousLocation
                    && destination != excludeDestination
                    && isLocalStep(location, destination)
                    && destination.canActorEnter(creature)
                    && !MovementRestrictions.isForbidden(creature, destination)) {
                return true;
            }
        }

        return false;
    }

    private boolean isLocalStep(
            Location origin,
            Location destination) {

        return origin.map() == destination.map()
                && GridDistance.between(origin, destination) == 1;
    }
}