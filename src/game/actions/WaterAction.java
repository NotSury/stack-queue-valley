package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.capabilities.Ability;
import game.capabilities.Waterable;

/**
 * An action that allows an actor to water a waterable object.
 * The action succeeds only when the actor has a Watering Tool
 * and sufficient stamina.
 */
public class WaterAction extends Action {

    private static final int STAMINA_COST = 2;

    private final Waterable waterable;
    private final Location location;

    /**
     * Constructs a new water action.
     *
     * @param waterable the object to be watered
     * @param location the location of the object to be watered
     */
    public WaterAction(Waterable waterable, Location location) {
        this.waterable = waterable;
        this.location = location;
    }

    /**
     * Executes the watering action.
     * If the actor has a watering tool and at least two stamina points,
     * two stamina points are consumed and the target is watered. Otherwise,
     * the action fails without consuming stamina or modifying the target.
     *
     * @param actor the actor performing the action
     * @param map the map containing the actor
     * @return a description of the result of the action
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (!actor.hasAbility(Ability.WATER)) {
            return "You don't have a Watering Can to water dirt.";
        }

        if (actor.getStatistic(ActorStatistics.STAMINA) < STAMINA_COST) {
            return "You are too tired to water dirt";
        }

        actor.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.DECREASE, STAMINA_COST);

        return waterable.wateredBy(actor, location);
    }

    /**
     * Returns the description of this action for the action menu.
     *
     * @param actor the actor performing the action
     * @return the menu description
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " waters " + waterable;
    }
}
