package game.navigation;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.MovementHazard;
import game.statuses.ChargedStatus;

/**
 * Checks environmental hazards independently of occupancy
 * and terrain movement restrictions.
 */
public class MovementSafety {

    private MovementSafety() {
    }

    /**
     * Checks whether a location contains a recognised hazard.
     *
     * Rejects charged ground and entities that implement
     * the movement hazard contract.
     *
     * @param location the location to inspect
     * @return true if no recognised hazard is present
     */
    public static boolean isSafe(Location location) {
        if (location.getGround().hasStatus(ChargedStatus.class)
                || location.getGround()
                .asCapability(MovementHazard.class)
                .isPresent()) {
            return false;
        }

        for (Item item : location.getItems()) {
            if (item.asCapability(MovementHazard.class).isPresent()) {
                return false;
            }
        }

        return true;
    }
}
