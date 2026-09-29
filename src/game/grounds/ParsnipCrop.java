package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Parsnip;

import java.util.List;

/**
 * ParsnipCrop represents a mature Parsnip crop that can be harvested.
 *
 * <p>Harvesting the crop costs 1 stamina point and adds one Parsnip
 * to the actor's inventory. The crop is then replaced with Dirt.
 * There is also a 50 percent chance that an additional Parsnip is
 * placed on a randomly selected adjacent location.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class ParsnipCrop extends Crop {

    /**
     * Constructor for the ParsnipCrop class.
     *
     * <p>Creates a mature Parsnip crop represented by the 'P'
     * display character.</p>
     */
    public ParsnipCrop() {
        super('P', "Parsnip Crop");
    }

    /**
     * Returns the stamina required to harvest this crop.
     *
     * @return the harvest cost of 1 stamina point
     */
    @Override
    public int getHarvestCost() {
        return 1;
    }

    /**
     * Harvests this Parsnip crop.
     *
     * <p>One Parsnip is added to the actor's inventory and the crop is
     * replaced with Dirt. There is a 50 percent chance that another
     * Parsnip is placed on a randomly selected adjacent location.</p>
     *
     * @param actor the actor harvesting the crop
     * @param location the location containing the crop
     * @return a description of the harvesting result
     */
    @Override
    public String harvest(Actor actor, Location location) {

        actor.getInventory().add(new Parsnip());

        location.setGround(new Dirt());

        if (Math.random() < 0.5) {
            List<Exit> exits = location.getExits();

            if (!exits.isEmpty()) {
                int randomIndex = (int) (Math.random() * exits.size());

                Location adjacentLocation =
                        exits.get(randomIndex).getDestination();

                adjacentLocation.addItem(new Parsnip());
            }
        }

        return actor + " harvest the Parsnip crop";
    }
}