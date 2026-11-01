package game.support;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.Repellable;

import java.util.Optional;

/**
 * Checks temporary movement restrictions affecting actors.
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.0.0
 */
public final class MovementRestrictions {

    /**
     * Prevents instantiation of this utility class
     */
    private MovementRestrictions(){
    }

    /**
     * Checks if an actor is forbidden from entering a location.
     *
     * @param actor the actor attempting to move
     * @param destination the destination location
     * @return true if entry is forbidden
     */
    public static boolean isForbidden(Actor actor, Location destination) {
        Optional<Repellable> repellable =
                actor.asCapability(Repellable.class);

        if (repellable.isPresent()) {
            return repellable.get().isForbidden(destination);
        }
        return false;
    }
}
