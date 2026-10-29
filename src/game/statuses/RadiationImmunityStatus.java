package game.statuses;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Provides temporary immunity to cosmic radiation.
 *
 * <p>The upcoming CosmicRadiation effect must check
 * for this status before applying radiation damage.</p>
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public final class RadiationImmunityStatus implements Status {

    private int remainingTurns;

    /**
     * Creates a timed radiation immunity status.
     *
     * @param turns number of protected status ticks
     */
    public RadiationImmunityStatus(int turns) {
        if (turns <= 0) {
            throw new IllegalArgumentException(
                    "Immunity duration must be positive"
            );
        }

        this.remainingTurns = turns;
    }

    /**
     * Advances the immunity timer.
     *
     * @param entity entity carrying this status
     * @param location entity's current location
     */
    @Override
    public void tickStatus(GameEntity entity, Location location) {
        if (remainingTurns > 0) {
            remainingTurns--;
        }
    }

    /**
     * Checks whether immunity remains active.
     *
     * @return true while immunity remains active
     */
    @Override
    public boolean isStatusActive() {
        return remainingTurns > 0;
    }
}
