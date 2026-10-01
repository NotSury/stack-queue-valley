package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.capabilities.ToolAbility;
import game.interfaces.Waterable;

/**
 * An action that allows an actor to water a Waterable target.
 *
 * <p>The target determines the stamina cost required for watering
 * and the effect that occurs when it is watered.</p>
 *
 * @author <Chin Kai Xuan> <35730374>
 * @version 1.0.0
 */
public class WateringAction extends Action {

    /**
     * The target being watered.
     */
    private final Waterable target;

    /**
     * The location containing the target.
     */
    private final Location location;

    /**
     * Constructor for WateringAction.
     *
     * @param target the target to be watered
     * @param location the location containing the target
     */
    public WateringAction(Waterable target, Location location) {
        this.target = target;
        this.location = location;
    }

    /**
     * Executes the watering action.
     *
     * @param actor the actor performing the action
     * @param map the current game map
     * @return the result of watering the target
     */
    @Override
    public String execute(Actor actor, GameMap map) {

        if (!actor.hasAbility(ToolAbility.WATERING)) {
            return actor + " does not have a watering tool.";
        }

        int cost = target.getWateringCost();

        if (actor.getStatistic(ActorStatistics.STAMINA) < cost) {
            return actor + " is too tired to water " + target + ".";
        }

        actor.modifyStatistic(
                ActorStatistics.STAMINA,
                StatisticOperations.DECREASE,
                cost
        );

        return target.water(actor, location);
    }

    /**
     * Returns the description of this action for the menu.
     *
     * @param actor the actor performing the action
     * @return the menu description
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " waters " + target;
    }
}