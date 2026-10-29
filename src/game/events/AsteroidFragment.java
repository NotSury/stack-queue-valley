package game.events;

import game.api.AsteroidData;
import game.interfaces.CelestialEvent;
import java.util.Objects;

/**
 * Models one concentrated impact from a large or hazardous asteroid,
 * excluding high-speed hazardous asteroids reserved for celestial storms.
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public final class AsteroidFragment implements CelestialEvent {

    private static final double FRAGMENT_DIAMETER_METERS = 80.0;
    private static final double STORM_VELOCITY_KM_PER_SECOND = 20.0;

    /**
     * Determines whether an asteroid qualifies for a concentrated impact.
     *
     * @param asteroidData NASA measurements to classify
     * @return true for large non-hazardous or lower-speed hazardous asteroids
     */
    @Override
    public boolean supports(AsteroidData asteroidData) {
        Objects.requireNonNull(asteroidData, "asteroidData");

        boolean hazardous = asteroidData.isPotentiallyHazardous();

        return (!hazardous
                && asteroidData.getEstimatedDiameterMeters()
                >= FRAGMENT_DIAMETER_METERS)
                || (hazardous
                && asteroidData.getVelocityKmPerSecond()
                < STORM_VELOCITY_KM_PER_SECOND);
    }

    /**
     * Calculates the impact radius, damage, resources and guardian encounters.
     *
     * @param asteroidData NASA asteroid measurements
     * @return the resulting fictional impact profile
     * @throws IllegalArgumentException if the asteroid is not supported
     */
    @Override
    public ImpactProfile buildProfile(AsteroidData asteroidData) {
        if (!supports(asteroidData)) {
            throw new IllegalArgumentException(
                    "Asteroid does not qualify for AsteroidFragment"
            );
        }

        double diameter = asteroidData.getEstimatedDiameterMeters();
        double velocity = asteroidData.getVelocityKmPerSecond();
        boolean hazardous = asteroidData.isPotentiallyHazardous();

        int radius = 1 + Math.min(3, (int) (diameter / 100.0));
        int damage = 10 + Math.min(35, (int) (velocity / 2.0));
        int oreCount = 2 + Math.min(6, (int) (diameter / 70.0));
        int guardianCount = hazardous ? 1 : 0;
        int duration = hazardous ? 3 : 2;

        return new ImpactProfile(
                "Asteroid Fragment",
                1,
                radius,
                damage,
                oreCount,
                guardianCount,
                duration
        );
    }
}