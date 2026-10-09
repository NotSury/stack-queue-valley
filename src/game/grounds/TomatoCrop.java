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
 * Tomatoes. The crop is removed and the actor's maximum stamina
 * is increased by 1.</p>
 *
 * @author Lee Le Si 34599223
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public class TomatoCrop extends Crop {

    /**
     * Stamina required to harvest this crop.
     */
    private static final int HARVEST_COST = 2;

    /**
     * Number of Tomatoes received when harvested.
     */
    private static final int TOMATO_AMOUNT = 2;

    /**
     * Amount by which maximum stamina increases after harvesting.
     */
    private static final int MAX_STAMINA_INCREASE = 1;

    /**
     * Constructor for TomatoCrop.
     */
    public TomatoCrop() {
        super('T', "Tomato Crop");
    }

    /**
     * Returns the stamina required to harvest this crop.
     *
     * @return 2 stamina points
     */
    @Override
    public int getHarvestCost() {
        return HARVEST_COST;
    }

    /**
     * Harvests this Tomato crop.
     *
     * <p>Two Tomatoes are added to the actor's inventory, the crop is
     * removed, and the actor's maximum stamina increases by 1.</p>
     *
     * @param actor the actor harvesting the crop
     * @param location the location containing the crop
     * @return a description of the harvesting result
     */
    @Override
    public String harvest(Actor actor, Location location) {

        for (int i = 0; i < TOMATO_AMOUNT; i++) {
            actor.getInventory().add(new Tomato());
        }

        removeCrop(location);

        actor.modifyStatisticMaximum(
                ActorStatistics.STAMINA,
                StatisticOperations.INCREASE,
                MAX_STAMINA_INCREASE
        );

        return actor + " harvests the Tomato crop";
    }
}