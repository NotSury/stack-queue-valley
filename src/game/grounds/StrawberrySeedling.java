package game.grounds;

import game.behaviours.GrowBehaviour;

import java.util.Random;

public class StrawberrySeedling extends Plant{
    private static final int GROWTH_INTERVAL = 5;
    private static final double GROWTH_CHANCE = 0.5;

    public StrawberrySeedling(){
        this(new Random());
    }

    public StrawberrySeedling(Random random){
        super('\'', "Strawberry Seedling");

        addPlantBehaviour(new GrowBehaviour(GROWTH_INTERVAL, GROWTH_CHANCE, StrawberrySprout::new, random));
    }
}
