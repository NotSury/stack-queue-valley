package game.reactions;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.GroundAbility;
import game.events.ImpactProfile;
import game.interfaces.CelestialReaction;
import game.items.MeteoriteOre;

import java.util.Objects;

/**
 * Creates meteorite ore at suitable celestial impact locations.
 *
 * <p>Resources may be generated on impactable terrain or an
 * existing impact crater. Protected locations cannot generate ore.</p>
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public final class ResourceReaction implements CelestialReaction {

    /**
     * Generates meteorite ore at the given impact location.
     *
     * @param profile the calculated celestial impact properties
     * @param location the centre of the meteor impact
     */
    @Override
    public void apply(ImpactProfile profile, Location location) {
        Objects.requireNonNull(profile, "profile");
        Objects.requireNonNull(location, "location");

        Ground ground = location.getGround();

        if (ground == null
                || !(ground.hasAbility(GroundAbility.METEOR_IMPACTABLE)
                || ground.hasAbility(GroundAbility.METEOR_IMPACT_SITE))) {
            return;
        }

        for (int i = 0; i < profile.getOreCount(); i++) {
            location.addItem(new MeteoriteOre());
        }
    }
}
