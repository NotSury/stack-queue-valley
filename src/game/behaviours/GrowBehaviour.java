package game.behaviours;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Plant;
import game.interfaces.PlantBehaviour;
import game.interfaces.PlantFactory;
import game.interfaces.GrowthAcceleratable;

import java.util.Objects;
import java.util.Random;

/**
 * Behaviour that allows a plant to grow into its next stage.
 *
 * <p>Growth is attempted at a fixed interval with a configurable probability.</p>
 *
 * @author Khoo Lynn 34598901
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.1.0
 */
public class GrowBehaviour implements PlantBehaviour, GrowthAcceleratable {
    private final int interval;
    private final double probability;
    private final PlantFactory nextStageFactory;
    private final Random random;

    /**
     * Constructor for GrowBehaviour.
     *
     * @param interval number of turns between growth attempts.
     * @param probability probability of successful growth.
     * @param nextStageFactory factory for creating the next stage.
     * @param random random number generator.
     */
    public GrowBehaviour(int interval, double probability, PlantFactory
            nextStageFactory, Random random){
        if (interval <= 0 || Double.isNaN(probability) || probability < 0 || probability > 1){
            throw new IllegalArgumentException(
                    "Invalid growth interval or probability."
            );
        }

        this.interval = interval;
        this.probability = probability;
        this.nextStageFactory = Objects.requireNonNull(nextStageFactory);
        this.random = Objects.requireNonNull(random);
    }

    /**
     * Returns the interval between growth attempts.
     *
     * @return growth interval.
     */
    @Override
    public int getInterval(){
        return interval;
    }

    /**
     * Attempts to grow the plant according to its growth probability.
     *
     * @param location the Location containing the plant.
     * @return true if growth succeeds, false otherwise.
     */
    @Override
    public boolean perform(Location location) {
        if (random.nextDouble() >= probability) {
            return false;
        }

        return advanceGrowth(location);
    }

    /**
     * Immediately advances the plant into its next growth stage
     * without applying the normal growth probability.
     *
     * @param location the Location containing the plant
     * @return true if the plant successfully advances
     */
    public boolean advanceGrowth(Location location)
    {
        Ground currentPlant = location.getGround();
        Plant nextStage = Objects.requireNonNull(nextStageFactory.create());
        location.setGround(nextStage);

        System.out.println(currentPlant + " at (" + location.x() + ", " +
                location.y() + ") grows into " + nextStage + ".");

        return true;
    }
}
