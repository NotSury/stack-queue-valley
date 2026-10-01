package game.statuses;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A status effect that modifies an actor's health once per turn
 * for a limited number of turns.
 *
 * <p>A positive health change heals the actor, while a negative
 * health change damages the actor.</p>
 *
 * @author <Chin Kai Xuan> <35730374>
 * @version 1.0.0
 */
public class HealthOverTimeStatus implements Status {

    /**
     * The amount of health changed each turn.
     * Positive values heal and negative values cause damage.
     */
    private final int healthChange;

    /**
     * The number of turns for which this status remains active.
     */
    private int remainingTurns;

    /**
     * Constructor for HealthOverTimeStatus.
     *
     * @param healthChange the health change applied each turn
     * @param duration the number of turns the effect lasts
     */
    public HealthOverTimeStatus(int healthChange, int duration) {
        this.healthChange = healthChange;
        this.remainingTurns = duration;
    }

    /**
     * Applies the health effect for one turn.
     *
     * @param currentEntity the entity affected by this status
     * @param location the current location of the entity
     */
    @Override
    public void tickStatus(GameEntity currentEntity, Location location) {
        if (currentEntity instanceof Actor actor) {
            if (healthChange > 0) {
                actor.heal(healthChange);
            } else if (healthChange < 0) {
                actor.hurt(Math.abs(healthChange));
            }

            remainingTurns--;
        }
    }

    /**
     * Determines whether this status should remain active.
     *
     * @return true while there are remaining turns
     */
    @Override
    public boolean isStatusActive() {
        return remainingTurns > 0;
    }
}