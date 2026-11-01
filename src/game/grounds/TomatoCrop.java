package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.behaviours.ProduceBehaviour;
import game.behaviours.SpreadBehaviour;
import game.items.Tomato;
import game.interfaces.CosmicMutable;

import java.util.Random;

/**
 * TomatoCrop represents a mature Tomato crop that can be harvested.
 *
 * <p>Harvesting the crop costs 2 stamina points and produces two
 * Tomatoes. The crop is removed and the actor's maximum stamina
 * is increased by 1.</p>
 *
 * <p>The crop produces a Tomato on a random adjacent tile every 7 turns. It also
 * has a 10 percent chance of spreading a new Tomato Crop onto adjacent plantable
 * ground every 20 turns.</p>
 *
 * @author Lee Le Si 34599223
 * @author Chin Kai Xuan 35730374
 * @author Khoo Lynn 34598901
 * @version 1.1.0
 */
public class TomatoCrop extends Crop
        implements CosmicMutable {

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
     * Number of turns between producing Tomatoes.
     */
    private static final int PRODUCE_INTERVAL = 7;

    /**
     * Number of turns between spreading attempts.
     */
    private static final int SPREAD_INTERVAL = 20;

    /**
     * Chance of spreading a new Tomato Crop.
     */
    private static final double SPREAD_CHANCE = 0.1;

    /**
     * Constructor for TomatoCrop.
     */
    public TomatoCrop(){
        this(new Random());
    }

    /**
     * Constructor with a random number generator.
     *
     * @param random random number generator.
     */
    public TomatoCrop(Random random) {
        super('T', "Tomato Crop");

        addPlantBehaviour(new ProduceBehaviour(PRODUCE_INTERVAL, Tomato::new, random));
        addPlantBehaviour(new SpreadBehaviour(SPREAD_INTERVAL, SPREAD_CHANCE, TomatoCrop::new, random));
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

    /**
     * Returns the cosmic mutation variety for Tomato.
     *
     * @return Cosmic Tomato variety
     */
    @Override
    public CosmicCrop.Variety getCosmicVariety() {
        return CosmicCrop.Variety.TOMATO;
    }

}