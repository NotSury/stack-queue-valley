package game.reactions;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.GroundAbility;
import game.events.ImpactProfile;
import game.grounds.ImpactCrater;
import game.interfaces.CelestialReaction;

import java.util.Objects;

/**
 * Applies celestial impact damage to terrain.
 *
 * <p>Ground within the impact radius is transformed into
 * ImpactCrater if it has the METEOR_IMPACTABLE ability.
 * Protected terrain and structures remain unchanged.</p>
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public final class TerrainReaction implements CelestialReaction {

    /**
     * Transforms impactable ground inside the impact radius.
     *
     * @param profile the calculated celestial impact properties
     * @param location the centre of the meteor impact
     */
    @Override
    public void apply(ImpactProfile profile, Location location) {
        Objects.requireNonNull(profile, "profile");
        Objects.requireNonNull(location, "location");

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
                Ground ground = affected.getGround();

                if (ground != null
                        && ground.hasAbility(
                        GroundAbility.METEOR_IMPACTABLE)) {

                    affected.setGround(new ImpactCrater());
                }
            }
        }
    }
}
