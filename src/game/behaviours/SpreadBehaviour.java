package game.behaviours;

import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Plant;
import game.interfaces.PlantBehaviour;
import game.interfaces.PlantFactory;
import game.utils.PlantMessages;
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
    private static final double GUARANTEED_CHANCE = 1.0;
    private final int interval;
    private final double probability;
    private final PlantFactory newPlantFactory;
    private final Random random;

    /**
     * Constructor for spreading without a probability limit.
     *
     * @param interval turns between spreading attempts.
     * @param newPlantFactory factory for creating a new plant.
     * @param random random number generator.
     */
    public SpreadBehaviour(int interval, PlantFactory newPlantFactory, Random random){
        this(interval, GUARANTEED_CHANCE, newPlantFactory, random);
    }

    /**
     * Constructor with a configurable spreading probability.
     *
     * @param interval turns between spreading attempts.
     * @param probability chance of successful spreading.
     * @param newPlantFactory factory for creating a new plant.
     * @param random random number generator.
     */
    public SpreadBehaviour(int interval, double probability, PlantFactory newPlantFactory, Random random){
        if (interval <= 0 || Double.isNaN(probability) || probability < 0 || probability > GUARANTEED_CHANCE){
            throw new IllegalArgumentException(
                    "Invalid spreading interval or probability."
            );
        }

        this.interval = interval;
        this.probability = probability;
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

        if (probability < GUARANTEED_CHANCE && random.nextDouble() >= probability){
            return false;
        }

        Location destination = availableLocations.get(random.nextInt(availableLocations.size()));
        Plant newPlant = Objects.requireNonNull(newPlantFactory.create());
        destination.setGround(newPlant);

        System.out.println(location.getGround() + " at " + PlantMessages.formatLocation(location)
                + " spreads, and " + newPlant + " takes root at " +
                PlantMessages.formatLocation(destination) + ".");

        return true;
    }
}
