package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actions.HarvestAction;
import game.capabilities.Harvestable;
import game.items.Tomato;

/**
 * Represents a mature Tomato crop that can be harvested.
 */
public class TomatoCrop extends Ground implements Harvestable {

    private static final int HARVEST_STAMINA_COST = 2;
    private static final int TOMATOES_PER_HARVEST = 2;
    private static final int MAX_STAMINA_INCREASE = 1;

    /**
     * Constructs a mature Tomato crop.
     */
    public TomatoCrop(){
        super('T', "Tomato Crop");
    }

    /**
     * Applies the specific effects of harvesting this Tomato crop.
     * Two portable Tomatoes are added to the actor's inventory, the harvested
     * crop is replaced with Dirt, and the actor's maximum stamina is increased
     * by one point.
     *
     * @param actor the actor performing the harvest
     * @param location the location of this Tomato crop
     * @return a description of the harvesting result
     */
    @Override
    public String harvestedBy(Actor actor, Location location) {
        for (int i = 0; i < TOMATOES_PER_HARVEST; i++) {
            actor.getInventory().add(new Tomato());
        }

        location.setGround(new Dirt());

        actor.modifyStatisticMaximum(ActorStatistics.STAMINA, StatisticOperations.INCREASE, MAX_STAMINA_INCREASE);

        return actor + " harvests the Tomato crop.";
    }

    /**
     * Returns the actions that can be performed on this Tomato crop.
     *
     * @param actor the actor interacting with the crop
     * @param location the location of this crop
     * @param direction the direction of this crop relative to the actor
     * @return the actions available for this crop
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        actions.add(new HarvestAction(this, location, HARVEST_STAMINA_COST));
        return actions;
    }
}
