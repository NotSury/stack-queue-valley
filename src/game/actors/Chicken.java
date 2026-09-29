package game.actors;

import edu.monash.fit2099.engine.positions.Location;
import game.items.Egg;

/**
 * Chicken represents a farm animal that can wander and be fed by the Farmer.
 *
 * <p>When fed successfully, the Chicken restores up to 3 health points
 * and produces one Egg. There is also a 50 percent chance that a second
 * Egg is produced.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class Chicken extends FarmAnimal {

    /**
     * Constructor for the Chicken class.
     *
     * <p>Creates a Chicken represented by the 'c' display character with
     * 10 health points and a feeding health restoration of 3.</p>
     */
    public Chicken() {
        super("Chicken", 'c', 10, 3);
    }

    /**
     * Produces the Chicken-specific result after feeding.
     *
     * <p>One Egg is always produced, with a 50 percent chance of
     * producing one additional Egg.</p>
     *
     * @param location the current location of the Chicken
     */
    @Override
    protected void produceAfterFeeding(Location location) {

        location.addItem(new Egg());

        if (Math.random() < 0.5) {
            location.addItem(new Egg());
        }
    }
}