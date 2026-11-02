package game.utils;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.random.RandomGenerator;

/**
 * Finds adjacent locations that satisfy a supplied condition, and picks a
 * random one of them, so that "a random adjacent tile that ..." is written
 * once instead of in every class that needs it (DRY).
 *
 * @author Surya Muni Peragas 35651628
 * @author Lee Le Si 34599223
 * @version 1.1.0
 */
public final class AdjacentLocations {

    /**
     * Prevents construction of this utility class.
     */
    private AdjacentLocations() {
    }

    /**
     * Finds distinct adjacent locations that satisfy the condition.
     *
     * @param center the location to search around
     * @param eligibility the condition each location must satisfy
     * @return a list of eligible adjacent locations
     */
    public static List<Location> matchingAround(
            Location center, Predicate<Location> eligibility) {

        List<Location> availableLocations = new ArrayList<>();

        for (Exit exit : center.getExits()) {
            Location location = exit.getDestination();

            if (!availableLocations.contains(location)
                    && eligibility.test(location)) {
                availableLocations.add(location);
            }
        }

        return availableLocations;
    }

    /**
     * Picks one random adjacent location that satisfies the condition.
     *
     * @param center the location to search around
     * @param eligibility the condition the chosen location must satisfy
     * @param random the random generator used to choose between eligible locations
     * @return a random eligible adjacent location, or empty if there is none
     */
    public static Optional<Location> randomMatching(
            Location center, Predicate<Location> eligibility, RandomGenerator random) {

        List<Location> candidates = matchingAround(center, eligibility);

        if (candidates.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(candidates.get(random.nextInt(candidates.size())));
    }
}