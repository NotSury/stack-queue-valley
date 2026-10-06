package game.teleportation;

import edu.monash.fit2099.engine.positions.Location;

/**
 * Shows a destination that an actor can teleport to.
 *
 * @author Khoo Lynn 34598901
 * @version 1.0.0
 */
public class TeleportDestination {
    private final String name;
    private final Location location;

    /**
     * Creates a teleport destination.
     *
     * @param name name used to describe the destination.
     * @param location location that the actor will arrive at.
     */
    public TeleportDestination(String name, Location location){
        this.name = name;
        this.location = location;
    }

    /**
     * Returns the destination location.
     *
     * @return destination location.
     */
    public Location getLocation(){
        return location;
    }

    /**
     * Returns the name of the destination.
     *
     * @return destination name.
     */
    public String getName(){
        return name;
    }

    /**
     * Returns the name of the destination.
     *
     * @return destination name.
     */
    @Override
    public String toString(){
        return name;
    }
}
