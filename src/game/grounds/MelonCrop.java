package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.behaviours.SpreadBehaviour;
import game.effects.Explosion;
import game.items.Melon;
import game.interfaces.CosmicMutable;

import java.util.Random;

/**
 * Represents a Melon crop that can be watered and harvested.
 *
 * <p>Watering costs 1 stamina and has a 50 percent chance of increasing
 * the actor's maximum stamina by 1.</p>
 *
 * <p>Harvesting costs 5 stamina and always removes the crop. There is
 * a 50 percent chance that the crop explodes. Otherwise, the actor
 * receives a Melon. Explosion damage is reduced by the number of times
 * the crop has been watered.</p>
 *
 * <p>The crop also spreads a new Melon Sprout onto a random adjacent plantable tile
 * every 20 turns.</p>
 *
 * @author Chin Kai Xuan 35730374
 * @author Khoo Lynn 34598901
 * @version 1.1.0
 */
public class MelonCrop extends WaterableCrop
        implements CosmicMutable {

    /**
     * Stamina required to water this crop.
     */
    private static final int WATERING_COST = 1;

    /**
     * Stamina required to harvest this crop.
     */
    private static final int HARVEST_COST = 5;

    /**
     * Chance of increasing maximum stamina when watered.
     */
    private static final double STAMINA_INCREASE_CHANCE = 0.5;

    /**
     * Chance that the crop explodes when harvested.
     */
    private static final double EXPLOSION_CHANCE = 0.5;

    /**
     * Base damage caused by an explosion before watering reductions.
     */
    private static final int BASE_EXPLOSION_DAMAGE = 20;

    /**
     * Amount by which maximum stamina increases after successful watering.
     */
    private static final int MAX_STAMINA_INCREASE = 1;

    /**
     * Number of turns between spreading attempts.
     */
    private static final int SPREAD_INTERVAL = 20;

    /**
     * Constructor for MelonCrop.
     */
    public MelonCrop(){
        this(new Random());
    }

    /**
     * Constructor with a random number generator.
     *
     * @param random random number generator.
     */
    public MelonCrop(Random random) {
        super('W', "Melon Crop");

        addPlantBehaviour(new SpreadBehaviour(SPREAD_INTERVAL, MelonSprout::new, random));
    }

    /**
     * Returns the stamina cost required to water this crop.
     *
     * @return 1 stamina point
     */
    @Override
    public int getWateringCost() {
        return WATERING_COST;
    }

    /**
     * Waters the Melon crop.
     *
     * <p>The watering count is increased. There is then a 50 percent
     * chance that the actor's maximum stamina increases by 1.</p>
     *
     * @param actor the actor watering the crop
     * @param location the location containing the crop
     * @return a description of the watering result
     */
    @Override
    public String water(Actor actor, Location location) {
        incrementWateringCount();

        if (Math.random() < STAMINA_INCREASE_CHANCE) {
            actor.modifyStatisticMaximum(
                    ActorStatistics.STAMINA,
                    StatisticOperations.INCREASE,
                    MAX_STAMINA_INCREASE
            );

            return actor
                    + " waters the Melon crop and increases maximum stamina by 1.";
        }

        return actor
                + " waters the Melon crop, but maximum stamina is unchanged.";
    }

    /**
     * Returns the stamina cost required to harvest this crop.
     *
     * @return 5 stamina points
     */
    @Override
    public int getHarvestCost() {
        return HARVEST_COST;
    }

    /**
     * Harvests the Melon crop.
     *
     * <p>The crop is always removed. There is a 50 percent chance that
     * it explodes and damages all actors on the crop tile and adjacent
     * tiles. Otherwise, the actor receives a Melon.</p>
     *
     * @param actor the actor harvesting the crop
     * @param location the location containing the crop
     * @return a description of the harvesting result
     */
    @Override
    public String harvest(Actor actor, Location location) {
        int explosionDamage =
                Math.max(0, BASE_EXPLOSION_DAMAGE - getWateringCount());

        if (Math.random() < EXPLOSION_CHANCE) {
            Explosion explosion = new Explosion(explosionDamage);

            /*
             * Trigger the explosion before replacing the crop so that the
             * supplied crop location remains available as its blast centre.
             */
            int affectedActors = explosion.explode(location);

            removeCrop(location);

            return actor
                    + " harvests the Melon crop, but it explodes for "
                    + explosionDamage
                    + " damage and affects "
                    + affectedActors
                    + " actor(s).";
        }

        actor.getInventory().add(new Melon());
        removeCrop(location);

        return actor
                + " harvests the Melon crop and receives a Melon.";
    }

    /**
     * Returns the cosmic mutation variety for Melon.
     *
     * @return Cosmic Melon variety
     */
    @Override
    public CosmicCrop.Variety getCosmicVariety() {
        return CosmicCrop.Variety.MELON;
    }

}