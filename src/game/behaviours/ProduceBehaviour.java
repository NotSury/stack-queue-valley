package game.behaviours;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.PlantBehaviour;
import game.interfaces.ProduceFactory;
import game.utils.AdjacentLocations;
import game.utils.PlantMessages;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;

/**
 * Behaviour that allows plants to drop produce on a random adjacent tile.
 *
 * @author Khoo Lynn 34598901
 * @version 1.0.0
 */
public class ProduceBehaviour implements PlantBehaviour {
    private final int interval;
    private final ProduceFactory produceFactory;
    private final Random random;

    /**
     * Constructor for ProduceBehaviour.
     *
     * @param interval number of turns between producing.
     * @param produceFactory factory for creating produce.
     * @param random random number generator.
     */
    public ProduceBehaviour(int interval, ProduceFactory produceFactory, Random random){
        if (interval <= 0){
            throw new IllegalArgumentException(
                    "Produce interval must be positive"
            );
        }

        this.interval = interval;
        this.produceFactory = Objects.requireNonNull(produceFactory);
        this.random = Objects.requireNonNull(random);
    }

    /**
     * Returns the interval between producing attempts.
     *
     * @return producing interval.
     */
    @Override
    public int getInterval(){
        return interval;
    }

    /**
     * Attempts to drop produce on a random adjacent tile.
     *
     * @param location the Location containing the plant.
     * @return true if produce was dropped, false otherwise.
     */
    @Override
    public boolean perform(Location location){
        Optional<Location> destination = AdjacentLocations.randomMatching(
                location, adjacentLocation -> true, random
        );

        if (destination.isEmpty()){
            return false;
        }

        Location dropLocation = destination.get();
        Item produce = Objects.requireNonNull(produceFactory.create());
        dropLocation.addItem(produce);

        System.out.println(location.getGround() + " at " + PlantMessages.formatLocation(location)
                + " drops a " + produce + " at " + PlantMessages.formatLocation(dropLocation) + ".");

        return true;
    }
}
