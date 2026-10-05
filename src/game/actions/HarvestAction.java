package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.interfaces.Harvestable;

/**
 * An action that allows an actor to harvest a Harvestable target.
 *
 * <p>The target determines whether it is currently ready to be harvested,
 * the stamina cost of harvesting, and the effect of the harvest.</p>
 *
 * @author <Chin Kai Xuan> <35730374>
 * @version 1.0.0
 */
public class HarvestAction extends Action {

    /**
     * The target being harvested.
     */
    private final Harvestable target;

    /**
     * The location containing the target.
     */
    private final Location location;

    /**
     * Constructor for HarvestAction.
     *
     * @param target the target to be harvested
     * @param location the location containing the target
     */
    public HarvestAction(Harvestable target, Location location) {
        this.target = target;
        this.location = location;
    }

    /**
     * Executes the harvesting action.
     *
     * @param actor the actor performing the action
     * @param map the current game map
     * @return the result of the harvesting action
     */
    @Override
    public String execute(Actor actor, GameMap map) {

        if (!target.canHarvest(actor, location)) {
            return target.getHarvestFailureMessage(actor);
        }

        int cost = target.getHarvestCost();

        if (actor.getStatistic(ActorStatistics.STAMINA) < cost) {
            return actor + " is too tired to harvest " + target + ".";
        }

        actor.modifyStatistic(
                ActorStatistics.STAMINA,
                StatisticOperations.DECREASE,
                cost
        );

        return target.harvest(actor, location);
    }

    /**
     * Returns the description of this action for the menu.
     *
     * @param actor the actor performing the action
     * @return the menu description
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " harvests " + target;
    }
}