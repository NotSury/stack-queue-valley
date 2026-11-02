package game.utils;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.random.RandomGenerator;

/**
 * Finds adjacent locations that satisfy a supplied condition and
 * optionally selects one at random.
 *
 * @author Surya Muni Peragas 35651628
 * @author Lee Le Si 34599223
 * @author Khoo Lynn 34598901
 * @version 1.2.0
 */
public final class AdjacentLocations {

    /**
     * Prevents construction of this utility class.
     */
    private AdjacentLocations() {
        throw new UnsupportedOperationException(
                "AdjacentLocations is a utility class and cannot be instantiated."
        );
    }

    /**
     * Finds distinct adjacent locations that satisfy the condition.
     *
     * @param center      the location to search around
     * @param eligibility the condition each location must satisfy
     * @return a list of eligible adjacent locations
     */
    public static List<Location> matchingAround(
            Location center, Predicate<Location> eligibility) {

        Objects.requireNonNull(center);
        Objects.requireNonNull(eligibility);

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
     * @param center      the location to search around
     * @param eligibility the condition the chosen location must satisfy
     * @param random      the random generator used to choose between eligible locations
     * @return a random eligible adjacent location, or empty if there is none
     */
    public static Optional<Location> randomMatching(
            Location center, Predicate<Location> eligibility, RandomGenerator random) {

        Objects.requireNonNull(random);

        List<Location> candidates = matchingAround(center, eligibility);

        if (candidates.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(candidates.get(random.nextInt(candidates.size())));
    }
}