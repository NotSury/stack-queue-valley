package game.grounds;

import game.behaviours.GrowBehaviour;

import java.util.Random;

/**
 * Represents a Strawberry Seedling that can grow into a Strawberry Sprout.
 *
 * @author Khoo Lynn 34598901
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.1.0
 */
public class StrawberrySeedling extends Plant{
    private static final int GROWTH_INTERVAL = 5;
    private static final double GROWTH_CHANCE = 0.5;

    /**
     * Constructor for StrawberrySeedling.
     */
    public StrawberrySeedling(){
        this(new Random());
    }

    /**
     * Constructor with a random number generator.
     *
     * @param random random number generator.
     */
    public StrawberrySeedling(Random random){
        super('\'', "Strawberry Seedling");

        addGrowBehaviour(new GrowBehaviour(GROWTH_INTERVAL, GROWTH_CHANCE, StrawberrySprout::new, random));
    }
}
