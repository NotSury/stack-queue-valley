package game.effects;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Represents an explosion that damages actors within its affected area.
 *
 * <p>The explosion affects any actor standing at the centre location
 * as well as actors on all adjacent tiles. The amount of damage is
 * supplied when the Explosion is created, allowing the class to be
 * reused by other explosive features in the future.</p>
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public class Explosion {

    /**
     * Damage dealt to each actor caught in the explosion.
     */
    private final int damage;

    /**
     * Constructor for Explosion.
     *
     * @param damage the amount of damage dealt to each affected actor
     */
    public Explosion(int damage) {
        this.damage = Math.max(0, damage);
    }

    /**
     * Triggers the explosion at the given location.
     *
     * <p>The actor standing on the centre tile, if present, is damaged.
     * Every actor standing on an adjacent tile is also damaged.</p>
     *
     * @param centre the centre location of the explosion
     * @return the number of actors affected by the explosion
     */
    public int explode(Location centre) {
        int affectedActors = damageActorAt(centre);

        for (Exit exit : centre.getExits()) {
            affectedActors += damageActorAt(exit.getDestination());
        }

        return affectedActors;
    }

    /**
     * Damages the actor at a location if one is present.
     *
     * @param location the location to check
     * @return 1 if an actor was affected, otherwise 0
     */
    private int damageActorAt(Location location) {
        if (!location.containsAnActor()) {
            return 0;
        }

        Actor actor = location.getActor();
        actor.hurt(damage);
        return 1;
    }
}