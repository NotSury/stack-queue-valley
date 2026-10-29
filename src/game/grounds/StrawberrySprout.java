package game.grounds;

import game.behaviours.GrowBehaviour;
import game.behaviours.SpreadBehaviour;

import java.util.Random;

public class StrawberrySprout extends Plant{
    private static final int GROWTH_INTERVAL = 10;
    private static final double GROWTH_CHANCE = 0.5;
    private static final int SPREAD_INTERVAL = 5;

    public StrawberrySprout(){
        this(new Random());
    }

    public StrawberrySprout(Random random){
        super('v', "Strawberry Sprout");

        addPlantBehaviour(new GrowBehaviour(GROWTH_INTERVAL, GROWTH_CHANCE, StrawberryCrop::new, random));
        addPlantBehaviour(new SpreadBehaviour(SPREAD_INTERVAL, StrawberrySeedling::new, random));
    }
}
