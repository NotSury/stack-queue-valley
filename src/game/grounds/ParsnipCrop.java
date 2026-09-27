package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.HarvestAction;
import game.capabilities.Harvestable;
import game.items.Parsnip;

import java.util.List;
import java.util.Random;

/**
 * Represents a mature Parsnip crop that can be harvested.
 */
public class ParsnipCrop extends Ground implements Harvestable {

    private static final int HARVEST_STAMINA_COST = 1;
    private static final double BONUS_PARSNIP_CHANCE = 0.5;

    private final Random random = new Random();

    /**
     * Constructs a mature Parsnip crop.
     */
    public ParsnipCrop(){
        super('P', "Parsnip Crop");
    }

    /**
     * Applies the specific effects of harvesting this Parsnip crop.
     * One portable Parsnip is added to the actor's inventory and the harvested
     * crop is replaced with Dirt. There is also a 50% chance that another
     * Parsnip is placed on a randomly selected adjacent location.
     *
     * @param actor the actor performing the harvest
     * @param location the location of this Parsnip crop
     * @return a description of the harvesting result
     */
    @Override
    public String harvestedBy(Actor actor, Location location) {
        actor.getInventory().add(new Parsnip());
        location.setGround(new Dirt());

        if (random.nextDouble() < BONUS_PARSNIP_CHANCE) {
            placeBonusParsnip(location);
        }

        return actor + " harvests the Parsnip crop.";
    }

    /**
     * Places a bonus Parsnip on a randomly selected adjacent location.
     *
     * @param location the location of the harvested Parsnip crop
     */
    private void placeBonusParsnip(Location location) {
        List<Location> adjacentLocations = location.getNearbyLocations(1);

        if (!adjacentLocations.isEmpty()) {
            int randomIndex = random.nextInt(adjacentLocations.size());
            Location selectedLocation = adjacentLocations.get(randomIndex);
            selectedLocation.addItem(new Parsnip());
        }
    }

    /**
     * Returns the actions that can be performed on this Parsnip crop.
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
