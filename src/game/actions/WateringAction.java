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
 * WateringAction represents an action that allows an actor to water
 * a ground that supports the Waterable behaviour.
 *
 * <p>The action requires the actor to have the WATERING ability and at
 * least 2 stamina points. When successful, the stamina cost is deducted
 * before the target's watering behaviour is performed.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class WateringAction extends Action {
    /**
     * The waterable target affected by this action.
     */
    private final Waterable target;
    /**
     * The location of the ground being watered.
     */
    private final Location location;
    /**
     * Constructor for the WateringAction class.
     *
     * @param target the waterable target affected by this action
     * @param location the location of the ground being watered
     */
    public WateringAction(Waterable target, Location location) {
        this.target = target;
        this.location = location;
    }
    /**
     * Executes the watering action.
     *
     * <p>The action succeeds only if the actor has the WATERING ability
     * and at least 2 stamina points. When successful, 2 stamina points are
     * deducted and the target's watering behaviour is applied.</p>
     *
     * <p>If either requirement is not satisfied, the target and the actor's
     * stamina remain unchanged.</p>
     *
     * @param actor the actor performing the watering action
     * @param map the game map where the action is performed
     * @return a description of the result of the watering action
     */
    @Override
    public String execute(Actor actor, GameMap map){

        if (!actor.hasAbility(ToolAbility.WATERING) || actor.getStatistic(ActorStatistics.STAMINA) < 2) {
            return "You are either too tired to WATER dirt or don't have a Watering Can to water dirt.";
        }

        actor.modifyStatistic(
                ActorStatistics.STAMINA,
                StatisticOperations.DECREASE,
                2
        );

        target.water(location);
        return actor + " waters the dirt into puddle.";
    }
    /**
     * Returns the description of this action for display in the action menu.
     *
     * @param actor the actor performing the action
     * @return the menu description of the watering action
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " waters Dirt ";
    }
}
