package game.utils;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.Hostile;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Finds actors around a location (REQ3). Curses, cursed creatures and undead
 * minions all need "which actors are within N tiles" and "which one is
 * closest", so the search and the distance rule are written once here (DRY).
 *
 * <p>Distance is measured in tiles, counting diagonal steps as one tile, which
 * matches the square area returned by {@link Location#getNearbyLocations}.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public final class NearbyActors {

    /** Matches actors the Farmer is allowed to fight. */
    public static final Predicate<Actor> HOSTILE =
            actor -> actor.asCapability(Hostile.class).isPresent();

    /**
     * Prevents construction of this utility class.
     */
    private NearbyActors() {
    }

    /**
     * Returns the distance between two locations in tiles, counting a diagonal
     * step as one tile.
     *
     * @param from the first location
     * @param to   the second location
     * @return the number of steps between the two locations
     */
    public static int distance(Location from, Location to) {
        return Math.max(Math.abs(from.x() - to.x()), Math.abs(from.y() - to.y()));
    }

    /**
     * Finds every actor within a radius of a centre location (not counting the
     * centre itself) that satisfies a condition.
     *
     * @param centre    the location to search around
     * @param radius    how many tiles away to search
     * @param condition the condition an actor must satisfy
     * @return the matching actors, in the engine's fixed location order
     */
    public static List<Actor> within(Location centre, int radius, Predicate<Actor> condition) {
        List<Actor> found = new ArrayList<>();
        for (Location nearby : centre.getNearbyLocations(radius)) {
            if (nearby.containsAnActor() && condition.test(nearby.getActor())) {
                found.add(nearby.getActor());
            }
        }
        return found;
    }

    /**
     * Finds the location of the closest actor within a radius that satisfies a
     * condition. Ties are broken by the engine's fixed location order, so the
     * result is always predictable.
     *
     * @param centre    the location to search around
     * @param radius    how many tiles away to search
     * @param condition the condition an actor must satisfy
     * @return the closest matching actor's location, or null if there is none
     */
    public static Location nearest(Location centre, int radius, Predicate<Actor> condition) {
        Location closest = null;
        int closestDistance = Integer.MAX_VALUE;
        for (Location nearby : centre.getNearbyLocations(radius)) {
            if (nearby.containsAnActor() && condition.test(nearby.getActor())) {
                int distance = distance(centre, nearby);
                if (distance < closestDistance) {
                    closest = nearby;
                    closestDistance = distance;
                }
            }
        }
        return closest;
    }
}