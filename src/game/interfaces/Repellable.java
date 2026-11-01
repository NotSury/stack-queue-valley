package game.interfaces;

import edu.monash.fit2099.engine.positions.Location;

import java.util.Collection;

/**
 * Represents an actor that can be repelled by magical effects
 * and temporarily prevented from entering certain locations.
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.0
 */
public interface Repellable {

    /**
     * Records locations that this actor cannot enter temporarily.
     *
     * @param area locations forbidden to this actor
     */
    void forbid(Collection<Location> area);

    /**
     * Checks if a destination is forbidden to this actor.
     *
     * @param location the destination being checked
     * @return true if entry is forbidden
     */
    boolean isForbidden(Location location);
}
