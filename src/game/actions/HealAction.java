package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * An ordinary action that restores an actor's health.
 * The engine limits healing to the actor's maximum health.
 */
public class HealAction extends Action {

    private final int healingAmount;

    /**
     * Creates an action that restores health.
     *
     * @param healingAmount the positive amount of health to restore
     */
    public HealAction(int healingAmount) {
        this.healingAmount = healingAmount;
    }

    /**
     * Restores health and reports the amount actually recovered.
     *
     * @param actor the actor recovering health
     * @param map the map containing the actor
     * @return a description of the health recovered
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        int previousHealth = actor.getStatistic(ActorStatistics.HEALTH);

        actor.heal(healingAmount);

        int recoveredHealth = actor.getStatistic(ActorStatistics.HEALTH) - previousHealth;

        return actor + " recovers " + recoveredHealth + " HP.";
    }

    /**
     * Describes the recovery action.
     * @param actor the actor recovering health
     * @return the action description
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " resets to recover health";
    }
}
