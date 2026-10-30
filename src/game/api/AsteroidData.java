package game.api;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Stores asteroid information retrieved from NASA's NeoWs API.
 *
 * <p>This class contains asteroid measurements only.
 * Celestial events will use these measurements to calculate
 * their effects on the game world.</p>
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public final class AsteroidData {

    private final String id;
    private final String name;
    private final double estimatedDiameterMeters;
    private final double velocityKmPerSecond;
    private final double missDistanceKm;
    private final boolean potentiallyHazardous;
    private final LocalDate approachDate;

    /**
     * Creates an AsteroidData object.
     *
     * @param id the asteroid's NASA identifier
     * @param name the asteroid's name
     * @param estimatedDiameterMeters estimated diameter in metres
     * @param velocityKmPerSecond relative velocity in kilometres per second
     * @param missDistanceKm miss distance in kilometres
     * @param potentiallyHazardous whether NASA classifies it as hazardous
     * @param approachDate the asteroid's close approach date
     */
    public AsteroidData(String id, String name,
                        double estimatedDiameterMeters,
                        double velocityKmPerSecond,
                        double missDistanceKm,
                        boolean potentiallyHazardous,
                        LocalDate approachDate) {

        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");

        if (id.isBlank() || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Asteroid ID and name cannot be blank"
            );
        }

        if (!Double.isFinite(estimatedDiameterMeters)
                || estimatedDiameterMeters <= 0
                || !Double.isFinite(velocityKmPerSecond)
                || velocityKmPerSecond < 0
                || !Double.isFinite(missDistanceKm)
                || missDistanceKm < 0) {
            throw new IllegalArgumentException(
                    "Invalid asteroid measurements"
            );
        }

        this.estimatedDiameterMeters = estimatedDiameterMeters;
        this.velocityKmPerSecond = velocityKmPerSecond;
        this.missDistanceKm = missDistanceKm;
        this.potentiallyHazardous = potentiallyHazardous;
        this.approachDate = Objects.requireNonNull(
                approachDate, "approachDate"
        );
    }

    /**
     * @return the NASA asteroid identifier
     */
    public String getId() {
        return id;
    }

    /**
     * @return the asteroid name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the estimated diameter in metres
     */
    public double getEstimatedDiameterMeters() {
        return estimatedDiameterMeters;
    }

    /**
     * @return the relative velocity in kilometres per second
     */
    public double getVelocityKmPerSecond() {
        return velocityKmPerSecond;
    }

    /**
     * @return the miss distance in kilometres
     */
    public double getMissDistanceKm() {
        return missDistanceKm;
    }

    /**
     * @return true if NASA classifies the asteroid as hazardous
     */
    public boolean isPotentiallyHazardous() {
        return potentiallyHazardous;
    }

    /**
     * @return the asteroid's close approach date
     */
    public LocalDate getApproachDate() {
        return approachDate;
    }
}