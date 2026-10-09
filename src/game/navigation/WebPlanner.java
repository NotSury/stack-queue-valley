package game.navigation;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.WebPlacement;

/**
 * Selects reachable web sites and safe walking steps without changing the map.
 *
 * @author Surya Muni Peragas 35651628
 * @author Lee Le Si 34599223
 * @version 1.1.0
 */
public interface WebPlanner {

    /**
     * Finds the best eligible site near the nest.
     *
     * @param creature the Spider placing webs
     * @param origin the Spider's current location
     * @param network the Spider's web network
     * @return the selected site, or null when no site is available
     */
    Location findPlacementTarget(Actor creature, Location origin, WebPlacement network);

    /**
     * Checks whether an eligible site has a safe reachable adjacent position.
     *
     * @param creature the Spider placing webs
     * @param origin the Spider's current location
     * @param target the proposed site, or null
     * @param network the Spider's web network
     * @return true when the site can still be used
     */
    boolean isPlacementTargetValid(Actor creature, Location origin, Location target, WebPlacement network);

    /**
     * Finds a safe walking step towards a position beside a web site.
     *
     * @param creature the moving Spider
     * @param origin the Spider's current location
     * @param target the web site
     * @return the first step, or null when already adjacent or unreachable
     */
    Exit findStepToPlacement(Actor creature, Location origin, Location target);

    /**
     * Finds a safe walking step towards a location.
     * Approaches an occupied target from an adjacent tile.
     *
     * @param creature the moving Spider
     * @param origin the Spider's current location
     * @param target the destination or occupied target
     * @return the first step, or null when already there or unreachable
     */
    Exit findStepTowards(Actor creature, Location origin, Location target);


    /**
     * Checks whether there is an eligible web site the Spider can safely
     * reach right now.
     *
     * @param creature the Spider placing webs
     * @param origin the Spider's current location
     * @param network the Spider's web network
     * @return true if reachable weaving work exists
     */
    boolean hasReachableWeavingWork(Actor creature, Location origin, WebPlacement network);

    /**
     * Checks whether the network has room for the entry webs placed when
     * Weaving starts plus at least one ordinary web afterwards.
     *
     * @param network the Spider's web network
     * @param entryWebs the number of entry webs Weaving places on entry
     * @return true if there is capacity for entry and ordinary placement
     */
    boolean hasCapacityForWeaving(WebPlacement network, int entryWebs);
}
