package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.items.Tomato;

/**
 * TomatoCrop represents a mature Tomato crop that can be harvested.
 *
 * <p>Harvesting the crop costs 2 stamina points and produces two
 * Tomatoes. The crop is replaced with Dirt and the actor's maximum
 * stamina is increased by 1.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class TomatoCrop extends Crop {

    /**
     * Constructor for the TomatoCrop class.
     *
     * <p>Creates a mature Tomato crop represented by the 'T'
     * display character.</p>
     */
    public TomatoCrop() {
        super('T', "Tomato Crop");
    }

    /**
     * Returns the stamina required to harvest this crop.
     *
     * @return the harvest cost of 2 stamina points
     */
    @Override
    public int getHarvestCost() {
        return 2;
    }

    /**
     * Harvests this Tomato crop.
     *
     * <p>Two Tomatoes are added to the actor's inventory, the crop is
     * replaced with Dirt, and the actor's maximum stamina is increased
     * by 1.</p>
     *
     * @param actor the actor harvesting the crop
     * @param location the location containing the crop
     * @return a description of the harvesting result
     */
    @Override
    public String harvest(Actor actor, Location location) {

        actor.getInventory().add(new Tomato());
        actor.getInventory().add(new Tomato());

        location.setGround(new Dirt());

        actor.modifyStatisticMaximum(
                ActorStatistics.STAMINA,
                StatisticOperations.INCREASE,
                1
        );

        return actor + " harvest the Tomato crop";
    }
}