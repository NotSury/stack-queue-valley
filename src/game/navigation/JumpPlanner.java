package game.navigation;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Evaluates legal two-tile jumps and selects useful approaches to a target.
 *
 * Queries inspect the environment without moving actors or changing the map.
 *
 * @author Khoo Lynn 34598901
 * @version 1.0.0
 */
public interface JumpPlanner {

    /**
     * Finds a legal landing that improves the approach to a target.
     *
     * <p>A useful jump either opens an otherwise unreachable walking approach or
     * reduces the total actions needed to reach a tile adjacent to the target.</p>
     *
     * @param creature the creature performing the jump.
     * @param origin the creature's current location.
     * @param target the location to approach.
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
     * @param origin the jump's starting location.
     * @param landing the proposed destination.
     * @return true if the jump is legal
     */
    boolean isLegalJump(Actor creature, Location origin, Location landing);
}
