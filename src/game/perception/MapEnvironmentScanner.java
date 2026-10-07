package game.perception;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.items.ItemAbility;
import edu.monash.fit2099.engine.positions.Location;
import game.navigation.GridDistance;
import game.navigation.MovementPlanner;

/**
 * Inspects map locations for actors with requested abilities and portable items.
 * Uses an injected movement planner to check item reachability.
 * Detection distance includes diagonal neighbors.
 */
public class MapEnvironmentScanner implements EnvironmentScanner {

    private final MovementPlanner movementPlanner;

    /**
     * Creates a scanner with access to movement planning.
     *
     * @param movementPlanner the planner used to check item reachability
     */
    public MapEnvironmentScanner(MovementPlanner movementPlanner) {
        this.movementPlanner = movementPlanner;
    }

    /**
     * Finds the nearest conscious Farmer within the detection radius.
     *
     * Excludes the origin. Equal-distance matches use the ordering
     * supplied by the engine's nearby-location list.
     *
     * @param origin the location from which to scan
     * @param radius the non-negative maximum detection distance
     * @return a detected Farmer, or null if none is detected
     */
    @Override
    public Actor findNearestActorWith(Location origin, int radius, Enum<?> ability) {
        Actor nearestActor = null;
        int nearestDistance = Integer.MAX_VALUE;

        for (Location location : origin.getNearbyLocations(radius)) {
            if (!location.containsAnActor()) {
                continue;
            }

            Actor actor = location.getActor();
            int candidateDistance = GridDistance.between(origin, location);

            if (actor.hasAbility(ability)
                    && actor.isConscious()
                    && candidateDistance < nearestDistance) {
                nearestActor = actor;
                nearestDistance = candidateDistance;
            }
        }

        return nearestActor;
    }

    /**
     * Finds the nearest reachable location containing a portable item.
     *
     * Distance limits are inclusive and measure detection distance,
     * rather than route length. A route may detour outside the
     * detection area. Equal-distance targets use the engine's
     * nearby-location ordering.
     *
     * @param creature the creature that would collect the item
     * @param origin the location from which to scan
     * @param minimumDistance the non-negative minimum detection distance
     * @param maximumDistance the maximum detection distance,
     *                        at least the minimum distance
     * @return an eligible location, or null if none is reachable
     */
    @Override
    public Location findNearestPortableItemLocation(
            Actor creature,
            Location origin,
            int minimumDistance,
            int maximumDistance) {

        if (minimumDistance == 0 && hasPortableItem(origin)) {
            return origin;
        }

        Location nearestLocation = null;
        int nearestDistance = Integer.MAX_VALUE;

        for (Location location
                : origin.getNearbyLocations(maximumDistance)) {

            int candidateDistance =
                    GridDistance.between(origin, location);

            if (candidateDistance < minimumDistance
                    || candidateDistance >= nearestDistance
                    || !hasPortableItem(location)
                    || !location.canActorEnter(creature)) {
                continue;
            }

            if (movementPlanner.findStepTowards(
                    creature, origin, location) != null) {

                nearestLocation = location;
                nearestDistance = candidateDistance;
            }
        }

        return nearestLocation;
    }

    private boolean hasPortableItem(Location location) {
        for (Item item : location.getItems()) {
            if (item.hasAbility(ItemAbility.PORTABLE)) {
                return true;
            }
        }

        return false;
    }
}