package game.reactions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.events.ImpactProfile;
import game.interfaces.CelestialReaction;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.Set;

/**
 * Damages conscious actors within celestial impact areas.
 *
 * <p>An actor receives direct impact damage at most once
 * during a single celestial event, even when blast areas
 * overlap.</p>
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.1.0
 */
public final class ImpactDamageReaction
        implements CelestialReaction {

    private final Set<Actor> damagedActors =
            Collections.newSetFromMap(new IdentityHashMap<>());

    /**
     * Resets damage tracking for a new celestial event.
     */
    @Override
    public void beginEvent() {
        damagedActors.clear();
    }

    /**
     * Applies impact damage to actors that have not already
     * been hit during the current event.
     *
     * @param profile calculated celestial impact properties
     * @param location impact centre
     */
    @Override
    public void apply(ImpactProfile profile, Location location) {

        Objects.requireNonNull(profile, "profile");
        Objects.requireNonNull(location, "location");

        int damage = profile.getDamage();

        if (damage <= 0) {
            return;
        }

        GameMap map = location.map();
        long radius = profile.getRadius();

        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {

                long dx = (long) x - location.x();
                long dy = (long) y - location.y();

                if (dx * dx + dy * dy > radius * radius) {
                    continue;
                }

                Location affected = map.at(x, y);

                if (!affected.containsAnActor()) {
                    continue;
                }

                Actor actor = affected.getActor();

                if (actor.isConscious()
                        && damagedActors.add(actor)) {

                    actor.hurt(damage);
                }
            }
        }
    }
}
