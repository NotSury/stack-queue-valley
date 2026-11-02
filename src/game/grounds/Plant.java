package game.grounds;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.PlantBehaviour;
import game.interfaces.Freezable;
import game.interfaces.GrowthAcceleratable;
import game.behaviours.GrowBehaviour;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all plants.
 *
 * <p>Tracks plant age and executes registered behaviours in priority order,
 * stopping after the first successful behaviour.</p>
 *
 * <p>Supports magical growth acceleration and temporary freezing
 * of automatic plant lifecycle behaviours.</p>
 *
 * @author Khoo Lynn 34598901
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.1.0
 */
public abstract class Plant extends Ground implements
        GrowthAcceleratable, Freezable {
    private int age;
    private final List<PlantBehaviour> behaviours;
    private GrowBehaviour growthBehaviour;
    private int frozenTicks = 0;
    private int freezeHolds = 0;

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
     * Registers a growth behaviour that can also be
     * activated through Growth Magic.
     *
     * @param behaviour the growth behaviour to register
     */
    protected void addGrowBehaviour(GrowBehaviour behaviour)
    {
        addPlantBehaviour(behaviour);
        growthBehaviour = behaviour;
    }

    /**
     * Advances the plant by one growth stage when eligible.
     *
     * @param location the Location containing the plant
     * @return true if the plant successfully advances
     */
    @Override
    public boolean advanceGrowth(Location location)
    {
        if (frozenTicks > 0 || freezeHolds > 0 ||growthBehaviour == null)
        {
            return false;
        }
        return  growthBehaviour.advanceGrowth(location);
    }

    /**
     * Temporarily freezes the plant's automatic lifecycle.
     */
    @Override
    public void freeze()
    {
        frozenTicks = 1;
    }

    /**
     * Keeps the plant protected from magical growth while
     * a freeze effect is active.
     */
    @Override
    public void holdFreeze()
    {
        freezeHolds++;
    }

    /**
     * Releases one active freeze hold.
     */
    @Override
    public void releaseFreeze()
    {
        if (freezeHolds > 0)
        {
            freezeHolds--;
        }
    }

    /**
     * Increments the plant's age and attempts behaviours in priority order.
     * Automatic ageing and behaviours are skipped while frozen. (REQ 4)
     *
     * @param location The location of the Ground
     */
    @Override
    public void tick(Location location)
    {
        super.tick(location);

        if (frozenTicks > 0)
        {
            frozenTicks--;
            return;
        }

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
