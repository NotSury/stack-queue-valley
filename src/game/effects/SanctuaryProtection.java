package game.effects;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.Hostile;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * Tracks locations temporarily protected by Nature's Sanctuary.
 *
 * @author <Vanessa Liau Jia Yee> <34599398>
 * @version 1.0.0
 */
public class SanctuaryProtection {

    private final Set<Location> protectedLocations = new HashSet<>();

    /**
     * Marks the given locations as protected.
     *
     * @param locations locations covered by the sanctuary
     */
    public void protect(List<Location> locations) {
        protectedLocations.addAll(locations);
    }

    /**
     * Checks if a hostile actor is blocked from entering a location.
     *
     * @param actor the actor attempting to move
     * @param destination the destination location
     * @return true if sanctuary protection blocks entry
     */
    public boolean blocksEntry(Actor actor, Location destination) {
        return protectedLocations.contains(destination) &&
                actor.asCapability(Hostile.class).isPresent();
    }

    /**
     * Removes the sanctuary protection.
     */
    public void clear() {
        protectedLocations.clear();
    }
}
