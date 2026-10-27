package game.cooking;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.random.RandomGenerator;

/**
 * Provides the actor, station location and shared random operations
 * for one cooking attempt.
 *
 * <p>All adjacent-location selection is centred on the cooking station.
 *  The random source is supplied by the caller to support deterministic tests.
 */
public final class CookingContext {

    private final Actor cook;
    private final Location stationLocation;
    private final RandomGenerator random;

    /**
     * Creates a context for one cooking attempt.
     *
     * @param cook the actor performing the cooking action
     * @param stationLocation the location containing the cooking station
     * @param random the random source used by cooking effects
     */
    public CookingContext(
            Actor cook,
            Location stationLocation,
            RandomGenerator random
    ) {
        this.cook = cook;
        this.stationLocation = stationLocation;
        this.random = random;
    }

    /**
     * Returns the actor performing the cooking action.
     *
     * @return the cook
     */
    public Actor getCook() {
        return cook;
    }

    /**
     * Returns the location used as the center of cooking effects.
     *
     * @return the station location
     */
    public Location getStationLocation() {
        return stationLocation;
    }

    /**
     * Performs a probability check using the injected random source.
     *
     * @param probability the probability of success, between zero and one inclusive
     * @return whether the check succeeds
     */
    public boolean chance(double probability) {
        return random.nextDouble() < probability;
    }

    /**
     * Selects a random eligible location adjacent to the station.
     *
     * <p>The supplied condition checks whether a destination is suitable
     * without modifying it. Candidates are rebuilt on each call so earlier
     * cooking effects can change which locations remain eligible.
     *
     * @param eligibility the condition that a destination must satisfy
     * @return a selected location, or empty if no destination qualifies
     */
    public Optional<Location> randomAdjacent(
            Predicate<Location> eligibility
    ) {
        List<Location> candidates = new ArrayList<>();

        for (Exit exit : stationLocation.getExits()) {
            Location destination = exit.getDestination();

            if (!candidates.contains(destination) && eligibility.test(destination)) {
                candidates.add(destination);
            }
        }

        if (candidates.isEmpty()) {
            return Optional.empty();
        }

        int selectedIndex = random.nextInt(candidates.size());
        return Optional.of(candidates.get(selectedIndex));
    }

}
