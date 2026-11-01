package game.reactions;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.GroundAbility;
import game.events.ImpactProfile;
import game.interfaces.CelestialReaction;
import game.items.MeteoriteOre;
import game.navigation.MovementSafety;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Creates meteorite ore around suitable celestial impact sites.
 *
 * <p>Ore is distributed across neighbouring impact craters
 * so that gravitational fields can attract and absorb it.
 * If no suitable neighbouring craters exist, resources
 * are placed at the impact centre instead.</p>
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public final class ResourceReaction implements CelestialReaction {

    /**
     * Generates meteorite ore at suitable impact locations.
     *
     * @param profile calculated celestial impact properties
     * @param location centre of the meteor impact
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

        List<Location> landingSites = new ArrayList<>();

        for (Exit exit : location.getExits()) {

            Location next = exit.getDestination();

            if (next.getGround().hasAbility(
                    GroundAbility.METEOR_IMPACT_SITE)
                    && MovementSafety.isSafe(next)) {

                landingSites.add(next);
            }
        }

        for (int i = 0; i < profile.getOreCount(); i++) {

            Location target = landingSites.isEmpty()
                    ? location
                    : landingSites.get(i % landingSites.size());

            target.addItem(new MeteoriteOre());
        }
    }
}
