package game.grounds;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.PlantBehaviour;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all plants.
 *
 * <p>Tracks plant age and executes registered behaviours in priority order,
 * stopping after the first successful behaviour.</p>
 *
 * @author Khoo Lynn 34598901
 * @version 1.0.0
 */
public abstract class Plant extends Ground {
    private int age;
    private final List<PlantBehaviour> behaviours;

    /**
     * Constructor for Plant.
     *
     * @param displayChar character representing the plant.
     * @param name name of the plant.
     */
    protected Plant(char displayChar, String name){
        super(displayChar, name);
        this.age = 0;
        this.behaviours = new ArrayList<>();
    }

    /**
     * Registers a plant behaviour.
     *
     * <p>Behaviours are registered in priority order, with the highest priority
     * behaviour added first.</p>
     *
     * @param behaviour the behaviour to register.
     */
    protected void addPlantBehaviour(PlantBehaviour behaviour){
        if (behaviour == null || behaviour.getInterval() <= 0){
            throw new IllegalArgumentException(
                    "Plant behaviour must have a positive interval."
            );
        }

        behaviours.add(behaviour);
    }

    /**
     * Increments the plant's age and attempts behaviours in priority order.
     *
     * @param location The location of the Ground
     */
    @Override
    public void tick(Location location){
        super.tick(location);
        age++;

        for (PlantBehaviour behaviour : behaviours){
            if (isDueEvery(behaviour.getInterval()) && behaviour.perform(location)){
                return;
            }
        }
    }

    /**
     * Checks whether the plant has reached a scheduled turn.
     *
     * @param interval number of turns between attempts.
     * @return true if an attempt is due.
     */
    protected boolean isDueEvery(int interval){
        return interval > 0 && age % interval == 0;
    }

    /**
     * Returns the current age of the plant.
     *
     * @return the plant's age.
     */
    protected int getAge(){
        return age;
    }
}
