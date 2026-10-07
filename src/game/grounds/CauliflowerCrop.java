package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Cauliflower;
import game.capabilities.GroundAbility;

/**
 * Represents a Cauliflower crop that can be watered and harvested.
 *
 * <p>Watering costs 2 stamina. Each time the crop is watered, there
 * is a 50 percent chance that all adjacent non-crop ground tiles are
 * converted into Puddles.</p>
 *
 * <p>The crop can only be harvested after it has been watered at least
 * five times. Harvesting costs 3 stamina, always removes the crop, and
 * has a 50 percent chance of giving the actor a Cauliflower.</p>
 *
 * @author <Chin Kai Xuan> <35730374>
 * @version 1.0.0
 */
public class CauliflowerCrop extends WaterableCrop {

    /**
     * Stamina required to water this crop.
     */
    private static final int WATERING_COST = 2;

    /**
     * Stamina required to harvest this crop.
     */
    private static final int HARVEST_COST = 3;

    /**
     * Number of times the crop must be watered before harvesting.
     */
    private static final int REQUIRED_WATERINGS = 5;

    /**
     * Chance of converting adjacent ground into Puddles.
     */
    private static final double PUDDLE_CHANCE = 0.5;

    /**
     * Chance of receiving a Cauliflower after harvesting.
     */
    private static final double HARVEST_SUCCESS_CHANCE = 0.5;

    /**
     * Constructor for CauliflowerCrop.
     */
    public CauliflowerCrop() {
        super('F', "Cauliflower Crop");
    }

    /**
     * Returns the stamina cost required to water this crop.
     *
     * @return 2 stamina points
     */
    @Override
    public int getWateringCost() {
        return WATERING_COST;
    }

    /**
     * Waters the Cauliflower crop.
     *
     * <p>The watering count is increased. There is then a 50 percent
     * chance that every adjacent ground tile that is not a Crop is
     * converted into a Puddle.</p>
     *
     * @param actor the actor watering the crop
     * @param location the location containing the crop
     * @return a description of the watering result
     */
    @Override
    public String water(Actor actor, Location location) {
        incrementWateringCount();

        if (Math.random() < PUDDLE_CHANCE) {
            for (Exit exit : location.getExits()) {
                Location adjacentLocation = exit.getDestination();

                if (!adjacentLocation.getGround().hasAbility(GroundAbility.CROP)) {
                    adjacentLocation.setGround(new Puddle());
                }
            }

            return actor
                    + " waters the Cauliflower crop and the surrounding "
                    + "non-crop ground becomes Puddles.";
        }

        return actor
                + " waters the Cauliflower crop, but no Puddles are created.";
    }

    /**
     * Determines whether this crop is ready to be harvested.
     *
     * @param actor the actor attempting to harvest
     * @param location the location containing the crop
     * @return true if the crop has been watered at least five times
     */
    @Override
    public boolean canHarvest(Actor actor, Location location) {
        return getWateringCount() >= REQUIRED_WATERINGS;
    }

    /**
     * Returns the failure message when the crop is harvested too early.
     *
     * @param actor the actor attempting to harvest
     * @return the harvest failure message
     */
    @Override
    public String getHarvestFailureMessage(Actor actor) {
        return actor
                + " cannot harvest the Cauliflower crop until it has been "
                + "watered at least five times.";
    }

    /**
     * Returns the stamina cost required to harvest this crop.
     *
     * @return 3 stamina points
     */
    @Override
    public int getHarvestCost() {
        return HARVEST_COST;
    }

    /**
     * Harvests this Cauliflower crop.
     *
     * <p>The crop is always removed. There is a 50 percent chance that
     * the actor receives a Cauliflower item.</p>
     *
     * @param actor the actor harvesting the crop
     * @param location the location containing the crop
     * @return a description of the harvesting result
     */
    @Override
    public String harvest(Actor actor, Location location) {
        removeCrop(location);

        if (Math.random() < HARVEST_SUCCESS_CHANCE) {
            actor.getInventory().add(new Cauliflower());

            return actor
                    + " harvests the Cauliflower crop and receives a Cauliflower.";
        }

        return actor
                + " harvests the Cauliflower crop, but the harvest fails.";
    }
}