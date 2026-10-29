package game.interfaces;

import game.api.AsteroidData;
import game.events.ImpactProfile;

/**
 * Defines how NASA asteroid data is matched to a celestial event
 * and converted into gameplay impact values.
 *
 * <p>Each implementation represents a different kind
 * of celestial event.</p>
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public interface CelestialEvent {

    /**
     * Determines whether this event can handle the asteroid data.
     *
     * @param asteroidData asteroid measurements retrieved from NASA
     * @return true if this event is suitable for the asteroid
     */
    boolean supports(AsteroidData asteroidData);

    /**
     * Calculates fictional gameplay effects from asteroid measurements.
     *
     * @param asteroidData asteroid measurements retrieved from NASA
     * @return the calculated impact properties for this event
     */
    ImpactProfile buildProfile(AsteroidData asteroidData);
}