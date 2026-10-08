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
 * @author Lee Le Si 34599223
 * @version 1.1.0
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
     * <p>Checks the destination first: if another actor is standing there, the
     * actor stays put and nothing else happens (no fall damage, no fire), so the
     * game does not crash trying to put two actors on one tile.</p>
     *
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return description of the teleportation result.
     */
    @Override
    public String execute(Actor actor, GameMap map){
        // two actors cannot share a tile, so stay put instead of crashing
        if (destination.getLocation().containsAnActor()){
            return actor + " cannot travel to " + destination + " because someone is standing there.";
        }
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
