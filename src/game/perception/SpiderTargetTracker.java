package game.perception;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.ActorRole;
import game.interfaces.WebNetwork;
import game.navigation.GridDistance;
import game.navigation.JumpPlanner;
import game.navigation.MovementPlanner;

/**
 * Provides the target and navigation assessment used by Spider states.
 * Extends the shared nest-threat assessment so all four states can use
 * the same environmental information.
 *
 * Queries do not move actors or change the environment.
 *
 * @author Khoo Lynn 34598901
 * @author Surya Muni Peragas 35651628
 * @version 1.1.0
 */
public interface SpiderTargetTracker extends SpiderAwareness {

    /**
     * Finds an active Farmer using the shared Farmer assessment.
     * Preserves the method name used by Hunting and Pouncing.
     *
     * @param origin the owning Spider's current location
     * @return the detected approachable Farmer, or null when none qualifies
     */
    Actor findActiveTarget(Location origin);

    /**
     * Checks whether the Spider can approach a target through safe walking.
     * An already adjacent target requires no further movement.
     *
     * @param origin the Spider's current location
     * @param target the target's location
     * @return true when the target is adjacent or has a safe walking approach
     */
    boolean canWalkToTarget(Location origin, Location target);

    /**
     * Finds a legal jump landing that improves the approach to a target.
     *
     * @param origin the Spider's current location
     * @param target the target's location
     * @return a useful landing, or null when no useful jump exists
     */
    Location findUsefulLanding(Location origin, Location target);

    /**
     * Checks whether an eligible web site has a safely reachable
     * adjacent placement position within the nest's web area.
     *
     * @param origin the Spider's current location
     * @return true when a reachable web-placement opportunity exists
     */
    boolean hasWebGap(Location origin);
}
