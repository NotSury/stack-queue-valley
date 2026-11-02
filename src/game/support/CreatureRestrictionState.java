package game.support;

import edu.monash.fit2099.engine.positions.Location;

import java.util.Set;
import java.util.HashSet;
import java.util.Collection;

/**
 * Manages temporary stun and movement restrictions for creatures.
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.0.0
 */
public class CreatureRestrictionState {

    private int stunTurns = 0;
    private final Set<Location> forbiddenLocations = new HashSet<>();
    private boolean restrictionTurnUsed = false;

    /**
     * Applies stun without shortening the existing duration.
     *
     * @param turns number of turns to stun
     */
    public void stun(int turns) {
        stunTurns = Math.max(stunTurns, turns);
    }

    /**
     * Checks if the creature loses this turn due to stun.
     *
     * @return true if stunned during this turn
     */
    public boolean consumeStunnedTurn() {
        if (stunTurns > 0) {
            stunTurns--;
            return true;
        }
        return false;
    }

    /**
     * Forbids entry into the given locations temporarily.
     *
     * @param area locations to restrict
     */
    public void forbid(Collection<Location> area) {
        forbiddenLocations.addAll(area);
        restrictionTurnUsed = false;
    }

    /**
     * Checks if a location is forbidden.
     *
     * @param location destination to check
     * @return true if the location is forbidden
     */
    public boolean isForbidden(Location location) {
        return forbiddenLocations.contains(location);
    }

    /**
     * Removes all movement restrictions.
     */
    public void clearForbidden() {
        forbiddenLocations.clear();
        restrictionTurnUsed = false;
    }

    /**
     * Keeps restrictions for one creature turn, then clears them.
     */
    public void updateRestrictionTurn() {
        if (forbiddenLocations.isEmpty()) {
            return;
        }
        if (restrictionTurnUsed) {
            clearForbidden();
        }
        else {
            restrictionTurnUsed = true;
        }
    }


}
