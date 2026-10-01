package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Strawberry;

import java.util.List;

/**
 * Represents a Strawberry crop that can be watered and harvested.
 *
 * <p>Watering costs 1 stamina and causes two Strawberries to appear
 * on randomly selected adjacent tiles. Each Strawberry has an
 * independent 50 percent chance of being rotten.</p>
 *
 * <p>Harvesting costs 1 stamina and always gives the actor two
 * Strawberries. The crop has a chance of being destroyed equal to
 * 50 percent minus the number of times it has been watered.</p>
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public class StrawberryCrop extends WaterableCrop {

    /**
     * Stamina required to harvest this crop.
     */
    private static final int HARVEST_COST = 1;

    /**
     * Stamina required to water this crop.
     */
    private static final int WATERING_COST = 1;

    /**
     * Chance that a produced Strawberry is rotten.
     */
    private static final double ROTTEN_CHANCE = 0.5;

    /**
     * Initial percentage chance that harvesting destroys the crop.
     */
    private static final int BASE_DESTRUCTION_CHANCE = 50;

    /**
     * Constructor for StrawberryCrop.
     */
    public StrawberryCrop() {
        super('S', "Strawberry Crop");
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
     * Waters the Strawberry crop.
     *
     * <p>The watering count is increased and two Strawberries are placed
     * independently on randomly selected adjacent tiles.</p>
     *
     * @param actor the actor watering the crop
     * @param location the location containing the crop
     * @return a description of the watering result
     */
    @Override
    public String water(Actor actor, Location location) {
        incrementWateringCount();

        dropRandomStrawberry(location);
        dropRandomStrawberry(location);

        return actor
                + " waters the Strawberry crop and two Strawberries appear nearby.";
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
     * Harvests the Strawberry crop.
     *
     * <p>Two independently generated Strawberries are added to the actor's
     * inventory. The crop may then be destroyed based on its watering count.</p>
     *
     * @param actor the actor harvesting the crop
     * @param location the location containing the crop
     * @return a description of the harvesting result
     */
    @Override
    public String harvest(Actor actor, Location location) {
        actor.getInventory().add(createRandomStrawberry());
        actor.getInventory().add(createRandomStrawberry());

        int destructionChance =
                Math.max(0, BASE_DESTRUCTION_CHANCE - getWateringCount());

        boolean destroyed =
                Math.random() < destructionChance / 100.0;

        if (destroyed) {
            removeCrop(location);

            return actor
                    + " harvests the Strawberry crop and receives two "
                    + "Strawberries. The crop is destroyed.";
        }

        return actor
                + " harvests the Strawberry crop and receives two "
                + "Strawberries. The crop survives.";
    }

    /**
     * Creates a Strawberry with an independent 50 percent chance
     * of being rotten.
     *
     * @return a newly created Strawberry
     */
    private Strawberry createRandomStrawberry() {
        boolean rotten = Math.random() < ROTTEN_CHANCE;
        return new Strawberry(rotten);
    }

    /**
     * Places one Strawberry on a randomly selected adjacent tile.
     *
     * @param location the location containing the Strawberry crop
     */
    private void dropRandomStrawberry(Location location) {
        List<Exit> exits = location.getExits();

        if (exits.isEmpty()) {
            return;
        }

        int randomIndex =
                (int) (Math.random() * exits.size());

        Location adjacentLocation =
                exits.get(randomIndex).getDestination();

        adjacentLocation.addItem(createRandomStrawberry());
    }
}