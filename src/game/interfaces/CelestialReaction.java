package game.interfaces;

import edu.monash.fit2099.engine.positions.Location;
import game.events.ImpactProfile;

/**
 * Defines a reaction to a celestial impact.
 *
 * <p>Reactions can reset event-specific state before a new
 * celestial event begins. Stateless reactions do not need
 * to override this lifecycle method.</p>
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.1.0
 */
public interface CelestialReaction {

    /**
     * Prepares the reaction for a new celestial event.
     *
     * <p>The default implementation does nothing.</p>
     */
    default void beginEvent() {
        // Stateless reactions require no reset.
    }

    /**
     * Applies this reaction at an impact location.
     *
     * @param impactProfile calculated impact properties
     * @param location impact centre
     */
    void apply(ImpactProfile impactProfile, Location location);
}
