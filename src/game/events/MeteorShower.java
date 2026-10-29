package game.events;

import game.api.AsteroidData;
import game.interfaces.CelestialEvent;
import java.util.Objects;

/**
 * Models multiple small meteor impacts caused by a small, non-hazardous asteroid.
 * All impact values are fictional game balancing values.
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public final class MeteorShower implements CelestialEvent {

    private static final double FRAGMENT_DIAMETER_METERS = 80.0;

    /**
     * Determines whether the asteroid qualifies for a meteor shower.
     *
     * @param asteroidData NASA measurements to classify
     * @return true for non-hazardous asteroids smaller than 80 metres
     */
    @Override
    public boolean supports(AsteroidData asteroidData) {
        Objects.requireNonNull(asteroidData, "asteroidData");

        return !asteroidData.isPotentiallyHazardous()
                && asteroidData.getEstimatedDiameterMeters()
                < FRAGMENT_DIAMETER_METERS;
    }

    /**
     * Calculates several small impact sites, with damage influenced by speed.
     *
     * @param asteroidData NASA asteroid measurements
     * @return the resulting fictional impact profile
     * @throws IllegalArgumentException if the asteroid is not supported
     */
    @Override
    public ImpactProfile buildProfile(AsteroidData asteroidData) {
        if (!supports(asteroidData)) {
            throw new IllegalArgumentException(
                    "Asteroid does not qualify for MeteorShower"
            );
        }

        double diameter = asteroidData.getEstimatedDiameterMeters();
        double velocity = asteroidData.getVelocityKmPerSecond();

        int impactCount = 2 + Math.min(3, (int) (diameter / 20.0));
        int damage = 5 + Math.min(15, (int) (velocity / 4.0));
        int oreCount = 1 + Math.min(3, (int) (diameter / 25.0));

        return new ImpactProfile(
                "Meteor Shower",
                impactCount,
                1,
                damage,
                oreCount,
                0,
                1
        );
    }
}