package game.behaviours;

import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Plant;
import game.interfaces.PlantBehaviour;
import game.interfaces.PlantFactory;
import game.utils.PlantingLocations;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Behaviour that allows a plant to spread to an adjacent plantable tile.
 *
 * @author Khoo Lynn 34598901
 * @version 1.0.0
 */
public class SpreadBehaviour implements PlantBehaviour {
    private final int interval;
    private final PlantFactory newPlantFactory;
    private final Random random;

    /**
     * Constructor for SpreadBehaviour.
     *
     * @param interval number of turns between spreading attempts.
     * @param newPlantFactory factory for creating the new plant.
     * @param random random number generator.
     */
    public SpreadBehaviour(int interval, PlantFactory newPlantFactory, Random random){
        if (interval <= 0){
            throw new IllegalArgumentException(
                    "Spreading interval must be positive."
            );
        }

        this.interval = interval;
        this.newPlantFactory = Objects.requireNonNull(newPlantFactory);
        this.random = Objects.requireNonNull(random);
    }

    /**
     * Returns the interval between spreading attempts.
     *
     * @return spreading interval.
     */
    @Override
    public int getInterval(){
        return interval;
    }

    /**
     * Attempts to spread onto a random adjacent plantable tile.
     *
     * @param location the Location containing the plant.
     * @return true if spreading succeeds, false otherwise.
     */
    @Override
    public boolean perform(Location location){
        List<Location> availableLocations = PlantingLocations.plantableTilesAround(location, false);

        if (availableLocations.isEmpty()){
            return false;
        }

        Location destination = availableLocations.get(random.nextInt(availableLocations.size()));
        Plant newPlant = Objects.requireNonNull(newPlantFactory.create());
        destination.setGround(newPlant);

        System.out.println(location.getGround() + " at (" + location.x() + ", " + location.y() +
                ") on " + location.map() + " spreads, and " + newPlant + " takes root at (" +
                destination.x() + ", " + destination.y() + ") on " + destination.map() + ".");

        return true;
    }
}
