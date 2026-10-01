package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.HarvestAction;
import game.interfaces.Harvestable;

/**
 * Crop is an abstract base class for harvestable crops.
 *
 * <p>This class centralises the common behaviour shared by crops,
 * including providing a HarvestAction to an actor and removing a crop
 * by replacing it with Dirt.</p>
 *
 * <p>Concrete crop classes are responsible for defining their own
 * harvest cost and harvesting effect.</p>
 *
 * @author <Chin Kai Xuan> <35730374>
 * @version 1.0.0
 */
public abstract class Crop extends Ground implements Harvestable {

    /**
     * Constructor for the Crop class.
     *
     * @param displayChar the character used to represent the crop
     * @param name the name of the crop
     */
    protected Crop(char displayChar, String name) {
        super(displayChar, name);
    }

    /**
     * Removes this crop from the game by replacing it with Dirt.
     *
     * @param location the location containing the crop
     */
    protected void removeCrop(Location location) {
        location.setGround(new Dirt());
    }

    /**
     * Returns the actions available for harvesting this crop.
     *
     * <p>A HarvestAction targeting this crop is provided to the actor.</p>
     *
     * @param actor the actor interacting with the crop
     * @param location the location containing the crop
     * @param direction the direction of the crop relative to the actor
     * @return the available actions for this crop
     */
    @Override
    public ActionList allowableActions(
            Actor actor,
            Location location,
            String direction) {

        ActionList actions = new ActionList();
        actions.add(new HarvestAction(this, location));
        return actions;
    }
}