package game.actors;

import edu.monash.fit2099.engine.positions.Location;
import game.items.Milk;
import edu.monash.fit2099.engine.positions.Exit;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Cow represents a farm animal that can wander and be fed by the Farmer.
 *
 * <p>When fed successfully, the Cow restores up to 5 health points
 * and produces a Milk item at a random adjacent location.</p>
 *
 * @author Lee Le Si 34599223
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.1.0
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
     * <p>A Milk item is placed at a random location adjacent to the Cow.</p>
     *
     * @param location the current location of the Cow
     */
    @Override
    protected void produceAfterFeeding(Location location)
    {
        List<Location> availableLocations = new ArrayList<>();

        for (Exit exit : location.getExits())
        {
            availableLocations.add(exit.getDestination());
        }

        if (!availableLocations.isEmpty())
        {
            Random random = new Random();
            int randomIndex = random.nextInt(availableLocations.size());

            Location milkLocation = availableLocations.get(randomIndex);
            milkLocation.addItem(new Milk());
        }
    }
}