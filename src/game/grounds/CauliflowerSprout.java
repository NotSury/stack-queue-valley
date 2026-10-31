package game.grounds;

import game.behaviours.GrowBehaviour;

import java.util.Random;

/**
 * Represents a Cauliflower Sprout that can grow into a Cauliflower Crop.
 *
 * @author Khoo Lynn 34598901
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.1.0
 */
public class CauliflowerSprout extends Plant{
    private static final int GROWTH_INTERVAL = 15;
    private static final double GROWTH_CHANCE = 0.25;

    /**
     * Constructor for CauliflowerSprout.
     */
    public CauliflowerSprout(){
        this(new Random());
    }

    /**
     * Constructor with a random number generator.
     *
     * @param random random number generator.
     */
    public CauliflowerSprout(Random random) {
        super('y', "Cauliflower Sprout");

        addGrowBehaviour(new GrowBehaviour(GROWTH_INTERVAL, GROWTH_CHANCE, CauliflowerCrop::new, random));
    }
}
