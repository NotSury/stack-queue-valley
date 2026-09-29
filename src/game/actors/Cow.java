package game.actors;

import edu.monash.fit2099.engine.positions.Location;
import game.items.Milk;

/**
 * Cow represents a farm animal that can wander and be fed by the Farmer.
 *
 * <p>When fed successfully, the Cow restores up to 5 health points
 * and produces a Milk item at its current location.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class Cow extends FarmAnimal {

    /**
     * Constructor for the Cow class.
     *
     * <p>Creates a Cow represented by the 'C' display character with
     * 20 health points and a feeding health restoration of 5.</p>
     */
    public Cow() {
        super("Cow", 'C', 20, 5);
    }

    /**
     * Produces the Cow-specific result after feeding.
     *
     * <p>A Milk item is placed at the Cow's current location.</p>
     *
     * @param location the current location of the Cow
     */
    @Override
    protected void produceAfterFeeding(Location location) {
        location.addItem(new Milk());
    }
}