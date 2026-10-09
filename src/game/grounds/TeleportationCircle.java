package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.effects.FireSpread;
import game.teleportation.TeleportDestination;

/**
 * A teleportation circle that transports an actor to a fixed destination and
 * creates Fire on the tiles surrounding the arrival location.
 *
 * @author Khoo Lynn 34598901
 * @version 1.0.0
 */
public class TeleportationCircle extends TeleportationGround {
    private static final int FIRE_DURATION = 2;
    private static final int BURN_DAMAGE = 5;
    private static final int BURN_DURATION = 3;
    private final FireSpread fireSpread;

    /**
     * Creates a TeleportationCircle linked to a fixed destination.
     *
     * @param destination the destination reached by using this circle.
     */
    public TeleportationCircle(TeleportDestination destination){
        super('*', "Teleportation Circle", destination);
        this.fireSpread = new FireSpread(FIRE_DURATION, BURN_DAMAGE, BURN_DURATION);
    }

    /**
     * Teleports the actor and creates Fire on every adjacent location around the
     * destination.
     *
     * @param actor actor being teleported.
     * @param map current map of the actor.
     * @param destination selected teleport destination.
     * @return a description of the teleportation result.
     */
    @Override
    public String teleport(Actor actor, GameMap map, TeleportDestination destination){
        Location destinationLocation = destination.getLocation();
        destinationLocation.map().moveActor(actor, destinationLocation);
        fireSpread.applyAround(destinationLocation);
        return actor + " travels through the Teleportation Circle to " + destination + ".";
    }
}
