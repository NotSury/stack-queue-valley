package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.capabilities.Harvestable;

/**
 * Represents an action that allows an actor to harvest a harvestable object.
 */
public class HarvestAction extends Action {

    private final Harvestable harvestable;
    private final Location location;
    private final int staminaCost;

    /**
     * Constructs a new harvest action.
     *
     * @param harvestable the object to be harvested
     * @param location the location of the object to be harvested
     * @param staminaCost the stamina required to perform the harvest
     */
    public HarvestAction(
            Harvestable harvestable,
            Location location,
            int staminaCost
    ) {
        this.harvestable = harvestable;
        this.location = location;
        this.staminaCost = staminaCost;
    }

    /**
     * Executes the harvesting action.
     *
     * The harvest fails if the actor does not have enough stamina. On a
     * successful attempt, the required stamina is consumed before delegating
     * the crop-specific harvesting behavior to the harvestable object.
     *
     * @param actor the actor performing the harvest
     * @param map the game map containing the actor
     * @return a description of the harvesting result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (actor.getStatistic(ActorStatistics.STAMINA) < staminaCost) {
            return "You are too tired to harvest this crop.";
        }

        actor.modifyStatistic(
                ActorStatistics.STAMINA,
                StatisticOperations.DECREASE,
                staminaCost
        );

        return harvestable.harvestedBy(actor, location);
    }

    /**
     * Returns the description of this action for the action menu.
     *
     * @param actor the actor performing the action
     * @return the menu description
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " harvests " + harvestable;
    }
}