package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TeleportAction;
import game.interfaces.Teleportable;
import game.teleportation.TeleportDestination;

/**
 * An abstract ground that allows an actor standing on it to teleport to a
 * fixed destination.
 *
 * Concrete teleportation grounds define their own teleportation effects.
 *
 * @author Khoo Lynn 34598901
 * @version 1.0.0
 */
public abstract class TeleportationGround extends Ground implements Teleportable {
    private final TeleportDestination destination;

    /**
     * Creates a teleportation ground with a fixed destination.
     *
     * @param displayChar the character used to represent the ground.
     * @param name the name of the ground.
     * @param destination the destination linked to this ground.
     */
    protected TeleportationGround(char displayChar, String name, TeleportDestination destination){
        super(displayChar, name);
        this.destination = destination;
    }

    /**
     * Returns the teleportation action when the actor is standing directly on this ground.
     *
     * @param actor the Actor acting
     * @param location the current Location
     * @param direction the direction of the Ground from the Actor
     * @return the available actions for this ground.
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction){
        ActionList actions = new ActionList();

        if (direction.isEmpty()){
            actions.add(new TeleportAction(this, destination));
        }
        return  actions;
    }
}
