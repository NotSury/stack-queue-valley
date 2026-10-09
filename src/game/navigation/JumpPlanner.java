package game.navigation;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Evaluates legal two-tile jumps and selects useful approaches to a target.
 *
 * Queries inspect the environment without moving actors or changing the map.
 *
 * @author Khoo Lynn 34598901
 * @author Lee Le Si 34599223
 * @version 1.1.0
 */
public interface JumpPlanner {

    /**
     * Finds a legal landing that improves the approach to a target.
     *
     * <p>A useful jump either opens an otherwise unreachable walking approach or
     * reduces the total actions needed to reach a tile adjacent to the target.</p>
     *
     * @param creature the creature performing the jump.
     * @param origin   the creature's current location.
     * @param target   the location to approach.
     * @return the best landing or null if no useful jump exists.
     */
    Location findBestLanding(Actor creature, Location origin, Location target);

    /**
     * Checks whether a two-tile jump is currently legal.
     *
     * <p>The jump must follow a horizontal, vertical or diagonal direction on the
     * same map. The landing must be passable, unoccupied and safe. The middle tile
     * is skipped.</p>
     *
     * @param creature the creature performing the jump.
     * @param origin   the jump's starting location.
     * @param landing  the proposed destination.
     * @return true if the jump is legal
     */
    boolean isLegalJump(Actor creature, Location origin, Location landing);

    /**
     * Checks whether the creature has reached a location.
     *
     * <p>Arrival means standing on the location or on a tile next to it,
     * on the same map.</p>
     *
     * @param origin the creature's current location
     * @param target the location being approached
     * @return true if the creature is on or next to the target
     */
    boolean hasArrived(Location origin, Location target);

    /**
     * Checks whether a location can still be approached.
     *
     * <p>True if the creature has already arrived, if a useful jump
     * exists, or if a safe walking route reaches a tile next to it.
     * False means the location is unreachable for now.</p>
     *
     * @param creature the creature approaching
     * @param origin the creature's current location
     * @param target the location to approach
     * @return true if the target can be approached
     */
    boolean canApproach(Actor creature, Location origin, Location target);

}