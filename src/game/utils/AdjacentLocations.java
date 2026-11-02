package game.utils;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Finds adjacent locations that satisfy a supplied condition.
 *
 * @author Surya Muni Peragas 35651628
 * @version 1.0.0
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
}