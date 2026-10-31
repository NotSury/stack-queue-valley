package game.grounds;

import game.behaviours.GrowBehaviour;
import game.behaviours.SpreadBehaviour;

import java.util.Random;

/**
 * Represent a Melon Sprout that can grow into a Melon Crop or spread a new Melon
 * Sprout.
 *
 * @author Khoo Lynn 34598901
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.1.0
 */
public class MelonSprout extends Plant {
    private static final int GROWTH_INTERVAL = 15;
    private static final double GROWTH_CHANCE = 0.25;
    private static final int SPREAD_INTERVAL = 10;

    /**
     * Constructor for MelonSprout.
     */
    public MelonSprout(){
        this(new Random());
    }

    /**
     * Constructor with a random number generator.
     *
     * @param random random number generator.
     */
    public MelonSprout(Random random){
        super('t', "Melon Sprout");

        addGrowBehaviour(new GrowBehaviour(GROWTH_INTERVAL, GROWTH_CHANCE, MelonCrop::new, random));
        addPlantBehaviour(new SpreadBehaviour(SPREAD_INTERVAL, MelonSprout::new, random));
    }
}
