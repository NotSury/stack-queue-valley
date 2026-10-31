package game.effects;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.capabilities.GroundAbility;
import game.interfaces.GravitationalFuel;
import game.navigation.MovementSafety;

import java.util.ArrayList;
import java.util.List;

/**
 * A temporary gravity field that drains stamina and attracts
 * loose celestial resources toward its impact centre.
 *
 * <p>Each absorbed fuel item extends the field's duration,
 * subject to the maximum bonus defined by EnvironmentalField.</p>
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public final class GravitationalDistortion
        extends EnvironmentalField {

    private static final int STAMINA_DRAIN = 3;

    /**
     * Records an item's position at the beginning of a field tick.
     *
     * @param location original item location
     * @param ore item to move
     */
    private record OrePosition(Location location, Item ore) {
    }

    /**
     * Creates a gravitational field.
     *
     * @param radius radius calculated from the impact profile
     * @param durationTurns duration calculated from the impact profile
     */
    public GravitationalDistortion(
            int radius,
            int durationTurns) {

        super(
                "Gravitational Distortion",
                'G',
                radius,
                durationTurns
        );
    }

    /**
     * Drains stamina from conscious actors and attracts eligible
     * resources by at most one orthogonal tile per field tick.
     *
     * @param centre centre of the gravitational field
     */
    @Override
    protected void applyFieldEffect(Location centre) {

        List<Location> affected = affectedLocations(centre);
        List<OrePosition> looseOre = new ArrayList<>();

        // Identify actors and celestial fuel within the area.
        for (Location location : affected) {

            if (location.containsAnActor()) {

                Actor actor = location.getActor();

                if (actor.isConscious()
                        && actor.hasStatistic(
                        ActorStatistics.STAMINA)) {

                    actor.modifyStatistic(
                            ActorStatistics.STAMINA,
                            StatisticOperations.DECREASE,
                            STAMINA_DRAIN
                    );
                }
            }

            // Only resources travelling toward the centre count.
            if (location != centre) {

                for (Item item : location.getItems()) {

                    if (item.asCapability(
                            GravitationalFuel.class).isPresent()) {

                        looseOre.add(
                                new OrePosition(location, item)
                        );
                    }
                }
            }
        }

        // Process a snapshot so an item cannot move
        // multiple tiles during the same field tick.
        for (OrePosition entry : looseOre) {

            Location source = entry.location();
            Item ore = entry.ore();

            if (!source.getItems().contains(ore)) {
                continue;
            }

            Location destination =
                    chooseCloserTile(source, centre);

            if (destination == null) {
                continue;
            }

            source.removeItem(ore);

            if (destination == centre) {

                int charge = ore.asCapability(
                                GravitationalFuel.class)
                        .orElseThrow()
                        .getGravityCharge();

                // Absorb the item if its energy can be used.
                if (extendDuration(charge) == 0) {

                    // At the extension cap, keep the
                    // item available for collection.
                    centre.addItem(ore);
                }

            } else {

                // The item travels one tile closer.
                destination.addItem(ore);
            }
        }
    }

    /**
     * Finds a safe orthogonally adjacent destination that brings
     * an item closer to the gravitational centre.
     *
     * @param source current item location
     * @param centre field centre
     * @return a suitable destination, or null if blocked
     */
    private Location chooseCloserTile(
            Location source,
            Location centre) {

        long originalDistance =
                distanceSquared(source, centre);

        Location best = null;
        long bestDistance = originalDistance;

        for (Exit exit : source.getExits()) {

            Location next = exit.getDestination();

            int horizontalStep =
                    Math.abs(next.x() - source.x());

            int verticalStep =
                    Math.abs(next.y() - source.y());

            // Move only one tile horizontally or vertically.
            if (horizontalStep + verticalStep != 1) {
                continue;
            }

            long distance =
                    distanceSquared(next, centre);

            if (distance >= bestDistance) {
                continue;
            }

            boolean traversable =
                    next.getGround().hasAbility(
                            GroundAbility.OPEN_GROUND)
                            || next.getGround().hasAbility(
                            GroundAbility.METEOR_IMPACT_SITE);

            // Reject protected or hazardous destinations.
            if (traversable && MovementSafety.isSafe(next)) {

                best = next;
                bestDistance = distance;
            }
        }

        return best;
    }

    /**
     * Calculates the squared distance between two locations.
     *
     * @param first first location
     * @param second second location
     * @return squared Euclidean distance
     */
    private long distanceSquared(
            Location first,
            Location second) {

        long dx = (long) first.x() - second.x();
        long dy = (long) first.y() - second.y();

        return dx * dx + dy * dy;
    }
}
