package game.statuses;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.effects.Explosion;

/**
 * Arms an explosion around an actor's location on its next status tick.
 * The centre actor is protected, while adjacent actors receive damage.
 * Actors defeated by the blast are handled immediately.
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.1.0
 */
public final class MelonBurstStatus implements Status {

    private final int damage;
    private boolean active = true;

    /**
     * Creates a one-use blast effect.
     *
     * @param damage explosion damage
     */
    public MelonBurstStatus(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException(
                    "Explosion damage must not be negative"
            );
        }

        this.damage = damage;
    }

    /**
     * Triggers the blast and handles actors defeated by it.
     *
     * @param entity entity carrying this status
     * @param location location of that entity
     */
    @Override
    public void tickStatus(GameEntity entity, Location location) {

        if (active) {

            new Explosion(damage).explodeAround(location);

            for (Exit exit : location.getExits()) {

                Location around = exit.getDestination();

                if (around.containsAnActor()
                        && !around.getActor().isConscious()) {

                    around.getActor().unconscious(location.map());
                }
            }
        }

        active = false;
    }

    /**
     * Returns whether this blast is still waiting to activate.
     *
     * @return true if active
     */
    @Override
    public boolean isStatusActive() {
        return active;
    }
}
