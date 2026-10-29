package game.events;

import game.api.AsteroidData;
import game.interfaces.CelestialEvent;
import java.util.Objects;

/**
 * Models severe celestial disturbances caused by a fast,
 * potentially hazardous asteroid.
 *
 * <p>This is a fictional gameplay event, not a NASA impact forecast.</p>
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public final class CelestialStorm implements CelestialEvent {

    private static final double STORM_VELOCITY_KM_PER_SECOND = 20.0;

    /**
     * Determines whether an asteroid qualifies for a celestial storm.
     *
     * @param asteroidData NASA measurements to classify
     * @return true for hazardous asteroids travelling at least 20 km/s
     */
    @Override
    public boolean supports(AsteroidData asteroidData) {
        Objects.requireNonNull(asteroidData, "asteroidData");

        return asteroidData.isPotentiallyHazardous()
                && asteroidData.getVelocityKmPerSecond()
                >= STORM_VELOCITY_KM_PER_SECOND;
    }

    /**
     * Calculates multiple stronger impacts, guardian encounters
     * and longer-lasting environmental effects.
     *
     * @param asteroidData NASA asteroid measurements
     * @return the resulting fictional impact profile
     * @throws IllegalArgumentException if the asteroid is not supported
     */
    @Override
    public ImpactProfile buildProfile(AsteroidData asteroidData) {
        if (!supports(asteroidData)) {
            throw new IllegalArgumentException(
                    "Asteroid does not qualify for CelestialStorm"
            );
        }

        double diameter = asteroidData.getEstimatedDiameterMeters();
        double velocity = asteroidData.getVelocityKmPerSecond();

        int impactCount = 2 + Math.min(2, (int) (velocity / 25.0));
        int radius = 2 + Math.min(2, (int) (diameter / 150.0));
        int damage = 15 + Math.min(40, (int) (velocity / 1.5));
        int oreCount = 2 + Math.min(5, (int) (diameter / 100.0));
        int guardianCount = diameter >= 150.0 ? 2 : 1;
        int duration = 3 + Math.min(3, (int) (velocity / 30.0));

        return new ImpactProfile(
                "Celestial Storm",
                impactCount,
                radius,
                damage,
                oreCount,
                guardianCount,
                duration
        );
    }
}