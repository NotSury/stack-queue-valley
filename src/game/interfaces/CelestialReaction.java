package game.interfaces;

import edu.monash.fit2099.engine.positions.Location;
import game.events.ImpactProfile;

/**
 * Defines a reaction that modifies the game world
 * after a celestial event.
 *
 * <p>Implementations can change terrain, create items,
 * spawn actors, cause immediate damage, or apply
 * lasting status effects.</p>
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public interface CelestialReaction {

    /**
     * Applies this reaction at a selected impact location.
     *
     * @param impactProfile gameplay properties calculated for the event
     * @param location location where this reaction is applied
     */
    void apply(ImpactProfile impactProfile, Location location);
}