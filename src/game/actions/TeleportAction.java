package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.interfaces.Teleportable;
import game.teleportation.TeleportDestination;

/**
 * An action that teleports an actor using a Teleportable object.
 *
 * @author Khoo Lynn 34598901
 * @version 1.0.0
 */
public class TeleportAction extends Action {
    private final Teleportable teleporter;
    private final TeleportDestination destination;

    /**
     * Creates a teleport action.
     *
     * @param teleporter teleport mechanism used by this action.
     * @param destination destination selected for teleportation.
     */
    public TeleportAction(Teleportable teleporter, TeleportDestination destination){
        this.teleporter = teleporter;
        this.destination = destination;
    }

    /**
     * Performs teleportation using the selected teleport mechanism.
     *
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return description of the teleportation result.
     */
    @Override
    public String execute(Actor actor, GameMap map){
        return teleporter.teleport(actor, map, destination);
    }

    /**
     * Returns the description shown in the action menu.
     *
     * @param actor The actor performing the action.
     * @return menu description
     */
    @Override
    public String menuDescription(Actor actor){
        return actor + " travels to " + destination;
    }
}
