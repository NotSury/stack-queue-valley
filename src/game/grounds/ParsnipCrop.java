package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.ProduceBehaviour;
import game.items.Parsnip;

import java.util.List;
import java.util.Random;

/**
 * Represents a mature Parsnip crop that can be watered and harvested, and
 * automatically produce Parsnips.
 *
 * <p>Harvesting costs 1 stamina and gives the actor one Parsnip.
 * The crop is then removed, with a 50 percent chance that an additional
 * Parsnip appears on a randomly selected adjacent tile.</p>
 *
 * <p>Watering costs 1 stamina and restores 20 health to the actor.
 * The crop then has a destruction chance equal to 10 percent plus
 * the number of times it has been watered.</p>
 *
 * <p>The crop also produces a Parsnip on a random adjacent tile every 15 turns.</p>
 *
 * @author Lee Le Si 34599223
 * @author Chin Kai Xuan 35730374
 * @author Khoo Lynn 34598901
 * @version 1.1.0
 */
public class ParsnipCrop extends WaterableCrop {

    /**
     * Stamina required to harvest this crop.
     */
    private static final int HARVEST_COST = 1;

    /**
     * Stamina required to water this crop.
     */
    private static final int WATERING_COST = 1;

    /**
     * Health restored when this crop is watered.
     */
    private static final int HEAL_AMOUNT = 20;

    /**
     * Base percentage chance that watering destroys the crop.
     */
    private static final int BASE_DESTRUCTION_CHANCE = 10;

    /**
     * Chance of producing an additional Parsnip when harvested.
     */
    private static final double EXTRA_PARSNIP_CHANCE = 0.5;

    /**
     * Number of turns between producing Parsnips.
     */
    private static final int PRODUCE_INTERVAL = 15;

    /**
     * Constructor for ParsnipCrop.
     */
    public ParsnipCrop(){
        this(new Random());
    }

    /**
     * Constructor with a random number generator.
     *
     * @param random random number generator.
     */
    public ParsnipCrop(Random random) {
        super('P', "Parsnip Crop");
        addPlantBehaviour(new ProduceBehaviour(PRODUCE_INTERVAL, Parsnip::new, random));
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
     * Waters the Parsnip crop.
     *
     * <p>The watering count is increased and the actor is healed by
     * 20 health points. The crop then has a destruction chance equal
     * to 10 plus its watering count.</p>
     *
     * @param actor the actor watering the crop
     * @param location the location containing the crop
     * @return a description of the watering result
     */
    @Override
    public String water(Actor actor, Location location) {
        incrementWateringCount();

        actor.heal(HEAL_AMOUNT);

        int destructionChance = Math.min(
                100,
                BASE_DESTRUCTION_CHANCE + getWateringCount()
        );

        boolean destroyed =
                Math.random() < destructionChance / 100.0;

        if (destroyed) {
            removeCrop(location);

            return actor
                    + " waters the Parsnip crop and restores 20 health. "
                    + "The crop is destroyed.";
        }

        return actor
                + " waters the Parsnip crop and restores 20 health. "
                + "The crop survives.";
    }

    /**
     * Returns the stamina cost required to harvest this crop.
     *
     * @return 1 stamina point
     */
    @Override
    public int getHarvestCost() {
        return HARVEST_COST;
    }

    /**
     * Harvests this Parsnip crop.
     *
     * <p>One Parsnip is added to the actor's inventory and the crop is
     * removed. There is also a 50 percent chance that an additional
     * Parsnip is placed on a randomly selected adjacent tile.</p>
     *
     * @param actor the actor harvesting the crop
     * @param location the location containing the crop
     * @return a description of the harvesting result
     */
    @Override
    public String harvest(Actor actor, Location location) {

        actor.getInventory().add(new Parsnip());

        if (Math.random() < EXTRA_PARSNIP_CHANCE) {
            List<Exit> exits = location.getExits();

            if (!exits.isEmpty()) {
                int randomIndex = (int) (Math.random() * exits.size());

                Location adjacentLocation =
                        exits.get(randomIndex).getDestination();

                adjacentLocation.addItem(new Parsnip());
            }
        }

        removeCrop(location);

        return actor + " harvests the Parsnip crop";
    }
}