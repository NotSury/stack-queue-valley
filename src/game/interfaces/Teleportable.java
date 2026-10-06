package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.teleportation.TeleportDestination;

/**
 * Shows something capable of teleporting an actor.
 *
 * @author Khoo Lynn 34598901
 * @version 1.0.0
 */
public interface Teleportable {

    /**
     * Teleports an actor to a selected destination.
     *
     * @param actor actor being teleported.
     * @param map current map of the actor.
     * @param destination selected teleport destination.
     * @return description of the teleportation result.
     */
    String teleport(Actor actor, GameMap map, TeleportDestination destination);
}
