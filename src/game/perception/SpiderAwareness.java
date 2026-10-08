package game.perception;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.traps.WebAlert;

/**
 * Shares Farmer targeting and nest-threat assessment between Spider states.
 * Queries do not move actors, place webs or change statuses.
 *
 * @author Surya Muni Peragas 35651628
 * @author Lee Le Si 34599223
 * @version 1.1.0
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
     * Checks whether a Farmer threatens the nest.     *
     * @param farmer the Farmer being assessed, or null
     * @return true when the Farmer should trigger Hunting
     */
    boolean isIntruder(Actor farmer);

    /**
     * Checks whether any conscious Farmer is close to the nest.
     *
     * <p>Works wherever the Spider is, so a Spider away from its nest
     * still notices an intrusion.</p>
     *
     * @return true if a Farmer is within the nest-threat radius of the nest
     */
    boolean isNestIntruded();

    /**
     * Finds a conscious Farmer around the location of a web alert.
     *
     * @param alert the alert being investigated, or null
     * @return the nearest Farmer around the alert, or null if none
     */
    Actor findFarmerNearAlert(WebAlert alert);
}
