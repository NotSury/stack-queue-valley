package game.statuses;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

/**
 * A status effect that modifies an entity's health once per turn
 * for a limited number of turns.
 *
 * <p>A positive health change heals the entity, while a negative
 * health change damages the entity.</p>
 *
 * @author Chin Kai Xuan 35730374
 * @author Khoo Lynn 34598901
 * @version 1.1.0
 */
public class HealthOverTimeStatus implements Status {

    /**
     * The amount of health changed each turn.
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

        if (currentEntity.hasStatistic(ActorStatistics.HEALTH)) {

            if (healthChange > 0) {
                currentEntity.modifyStatistic(
                        ActorStatistics.HEALTH,
                        StatisticOperations.INCREASE,
                        healthChange
                );
            } else if (healthChange < 0) {
                currentEntity.modifyStatistic(
                        ActorStatistics.HEALTH,
                        StatisticOperations.DECREASE,
                        Math.abs(healthChange)
                );
            }

            remainingTurns--;
        }
    }

    /**
     * Check whether this status applies the given health change.
     *
     * @param healthChange the health change to compare.
     * @return true if the health change matches.
     */
    public boolean hasHealthChange(int healthChange){
        return this.healthChange == healthChange;
    }

    /**
     * Resets the remaining duration of this status.
     *
     * @param duration the new number of remaining turns.
     */
    public void refresh(int duration){
        this.remainingTurns = duration;
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