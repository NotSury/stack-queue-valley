package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.teleportation.TeleportDestination;

import java.util.Random;

/**
 * A ladder that teleports an actor to a fixed destination.
 *
 * <p>When used, there is a 50% chance that the actor falls and takes
 * 25 damage. The actor is teleported regardless of whether the fall occurs.</p>
 */
public class Ladder extends TeleportationGround {
    private static final int FALL_DAMAGE = 25;
    private static final double FALL_CHANCE = 0.5;
    private final Random random = new Random();

    /**
     * Create a Ladder linked to a fixed destination.
     *
     * @param destination the destination reached by using this Ladder.
     */
    public Ladder(TeleportDestination destination){
        super('H', "Ladder", destination);
    }

    /**
     * Teleports the actor to the selected destination.
     *
     * <p>The actor has a 50% chance of taking 25 damage before travelling. A
     * fall does not prevent the teleportation.</p>
     *
     * @param actor actor being teleported.
     * @param map current map of the actor.
     * @param destination selected teleport destination.
     * @return a description of the teleportation result.
     */
    @Override
    public String teleport(Actor actor, GameMap map, TeleportDestination destination){
        boolean fell = random.nextDouble() < FALL_CHANCE;

        if (fell){
            actor.hurt(FALL_DAMAGE);
        }

        destination.getLocation().map().moveActor(actor, destination.getLocation());

        if (fell){
            return actor + " falls from the Ladder, takes " + FALL_DAMAGE + " damage and travels to " +
                    destination + ".";
        }

        return actor + " travels through the Ladder to " + destination + ".";
    }
}
