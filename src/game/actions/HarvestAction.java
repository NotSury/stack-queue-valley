package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.interfaces.Harvestable;
/**
 * HarvestAction represents an action that allows an actor to harvest
 * a Harvestable target.
 *
 * <p>The stamina cost is determined by the Harvestable target. If the
 * actor has enough stamina, the required amount is deducted before the
 * target's specific harvesting behaviour is performed.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class HarvestAction extends Action {
    /**
     * The harvestable target of this action.
     */
    private final Harvestable target;
    /**
     * The location of the harvestable target.
     */
    private final Location location;
    /**
     * Constructor for the HarvestAction class.
     *
     * @param target the harvestable target
     * @param location the location of the harvestable target
     */
    public HarvestAction(Harvestable target, Location location) {
        this.target = target;
        this.location = location;
    }

    /**
     * Executes the harvesting action.
     *
     * <p>The required stamina cost is obtained from the Harvestable target.
     * If the actor has sufficient stamina, the cost is deducted and the
     * target's harvesting behaviour is performed.</p>
     *
     * <p>If the actor does not have enough stamina, the target is not
     * harvested and no stamina is deducted.</p>
     *
     * @param actor the actor performing the harvest
     * @param map the game map where the action is performed
     * @return a description of the harvesting result
     */
    @Override
    public String execute(Actor actor, GameMap map){
        int cost = target.getHarvestCost();

        if (actor.getStatistic(ActorStatistics.STAMINA) < cost) {
            return actor + " is too tired to harvest";
        }

        actor.modifyStatistic(
                ActorStatistics.STAMINA,
                StatisticOperations.DECREASE,
                cost
        );

        return target.harvest(actor, location);
    }

    /**
     * Returns the description of this action for display in the action menu.
     *
     * @param actor the actor performing the action
     * @return the menu description of the harvesting action
     */
    @Override
    public String menuDescription(Actor actor){
        return actor + " harvest " + target;
    }
}
