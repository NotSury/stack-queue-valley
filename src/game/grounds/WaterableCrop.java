package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.WateringAction;
import game.interfaces.Waterable;

/**
 * WaterableCrop is an abstract base class for crops that can be watered.
 *
 * <p>This class keeps track of how many times a crop has been watered
 * and provides a WateringAction to actors interacting with the crop.</p>
 *
 * <p>Concrete subclasses define their own watering cost and watering
 * effects.</p>
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public abstract class WaterableCrop extends Crop implements Waterable {

    /**
     * The number of times this crop has been watered.
     */
    private int wateringCount;

    /**
     * Constructor for WaterableCrop.
     *
     * @param displayChar the character used to represent the crop
     * @param name the name of the crop
     */
    protected WaterableCrop(char displayChar, String name) {
        super(displayChar, name);
        this.wateringCount = 0;
    }

    /**
     * Increases the watering count of this crop by one.
     */
    protected void incrementWateringCount() {
        wateringCount++;
    }

    /**
     * Returns the number of times this crop has been watered.
     *
     * @return the watering count
     */
    protected int getWateringCount() {
        return wateringCount;
    }

    /**
     * Returns the actions available for this crop.
     *
     * <p>The crop keeps the harvesting action provided by Crop and
     * additionally provides a WateringAction.</p>
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

        ActionList actions = super.allowableActions(actor, location, direction);
        actions.add(new WateringAction(this, location));

        return actions;
    }
}