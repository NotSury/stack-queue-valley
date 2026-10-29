package game.reactions;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.events.ImpactProfile;
import game.interfaces.CelestialReaction;

import java.util.Objects;

/**
 * Damages actors within a circular celestial impact area.
 *
 * <p>Damage is determined by NASA-derived impact properties.
 * The reaction affects all actors within the radius, including
 * the Farmer, farm animals, and hostile creatures.</p>
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public final class ImpactDamageReaction implements CelestialReaction {

    /**
     * Applies impact damage to actors inside the blast radius.
     *
     * @param profile calculated celestial impact properties
     * @param location centre of the impact
     */
    @Override
    public void apply(ImpactProfile profile, Location location) {

        Objects.requireNonNull(profile, "profile");
        Objects.requireNonNull(location, "location");

        int damage = profile.getDamage();

        if (damage == 0) {
            return;
        }

        GameMap map = location.map();
        long radius = profile.getRadius();

        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {

                long dx = (long) x - location.x();
                long dy = (long) y - location.y();

                if (dx * dx + dy * dy
                        <= radius * radius) {

                    Location affected = map.at(x, y);

                    if (affected.containsAnActor()) {
                        affected.getActor().hurt(damage);
                    }
                }
            }
        }
    }
}
