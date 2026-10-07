package game.perception;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Shares Farmer targeting and nest-threat assessment between Spider states.
 * Queries do not move actors, place webs or change statuses.
 */
public interface SpiderAwareness {

    /**
     * Finds a conscious Farmer within the Spider's permitted pursuit area.
     * Requires an adjacent target, a safe walking route or a useful jump.
     *
     * @param origin the owning Spider's current location
     * @return the active Farmer, or null when none can be pursued
     */
    Actor findActiveFarmer(Location origin);

    /**
     * Checks whether a Farmer threatens the nest or is currently web-bound.
     *
     * @param farmer the Farmer being assessed, or null
     * @return true when the Farmer should trigger Hunting
     */
    boolean isIntruder(Actor farmer);
}
