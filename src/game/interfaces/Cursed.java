package game.interfaces;

import edu.monash.fit2099.engine.positions.Location;
import game.afflictions.Affliction;

/**
 * A creature that carries a curse (REQ3). When it dies, its killer inherits a
 * fresh copy of its curse, and its remains change the land around it.
 *
 * <p>{@code HostileCreature} looks for this capability in its death hook, so
 * only cursed creatures release a curse and no type checks are needed.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public interface Cursed {

    /**
     * Creates a fresh copy of this creature's curse, to be passed to its killer.
     *
     * @return a new curse object
     */
    Affliction curse();

    /**
     * Changes the land around this creature's remains when it dies.
     *
     * @param remains the location where the creature died
     * @return a description of what happened
     */
    String onDeath(Location remains);
}