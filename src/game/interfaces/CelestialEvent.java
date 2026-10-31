package game.interfaces;

import game.api.AsteroidData;
import game.effects.EnvironmentalField;
import game.events.ImpactProfile;

/**
 * Defines an event selected from NASA asteroid data.
 *
 * <p>Each implementation identifies suitable asteroids,
 * calculates their gameplay impact, and creates the
 * corresponding environmental field.</p>
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.1.0
 */
public interface CelestialEvent {

    /**
     * Determines whether this event supports the asteroid.
     *
     * @param asteroidData parsed NASA asteroid data
     * @return true if this event handles the asteroid
     */
    boolean supports(AsteroidData asteroidData);

    /**
     * Calculates the impact profile for this event.
     *
     * @param asteroidData parsed NASA asteroid data
     * @return calculated impact profile
     */
    ImpactProfile buildProfile(AsteroidData asteroidData);

    /**
     * Creates a fresh environmental field using the impact profile.
     *
     * <p>The returned field is not placed on the map.
     * The caller is responsible for choosing the impact
     * location and placing the field.</p>
     *
     * @param impactProfile calculated impact properties
     * @return a new environmental field
     */
    EnvironmentalField createField(ImpactProfile impactProfile);
}
