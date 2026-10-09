package game.actors;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Egg;
import game.items.Milk;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Chicken represents a farm animal that can wander and be fed by the Farmer.
 *
 * <p>When fed successfully, the Chicken restores up to 3 health points
 * and produces one Egg. There is also a 50 percent chance that a second
 * Egg is produced.</p>
 *
 * @author Lee Le Si 34599223
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.1.0
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
     * <p>One Egg is always produced at a random adjacent Location,
     * with a 50 percent chance of producing one additional Egg
     * at a randomly selected adjacent location.</p>
     *
     * @param location the current location of the Chicken
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

            Location eggLocation = availableLocations.get(randomIndex);
            eggLocation.addItem(new Egg());

            if (Math.random() < 0.5)
            {
                randomIndex = random.nextInt(availableLocations.size());
                eggLocation = availableLocations.get(randomIndex);
                eggLocation.addItem(new Egg());
            }
        }
    }
}