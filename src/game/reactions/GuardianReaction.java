package game.reactions;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.AstralGuardian;
import game.capabilities.GroundAbility;
import game.events.ImpactProfile;
import game.interfaces.CelestialReaction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Spawns Astral Guardians at available crater tiles inside
 * a celestial impact radius.
 *
 * <p>Existing actors and protected structures are preserved.</p>
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public final class GuardianReaction implements CelestialReaction {

    /**
     * Spawns up to the number of guardians specified by the profile.
     *
     * <p>If insufficient free crater tiles exist, fewer guardians
     * are spawned.</p>
     *
     * @param profile calculated celestial impact properties
     * @param location centre of the celestial impact
     */
    @Override
    public void apply(ImpactProfile profile, Location location) {
        Objects.requireNonNull(profile, "profile");
        Objects.requireNonNull(location, "location");

        if (profile.getGuardianCount() == 0) {
            return;
        }

        GameMap map = location.map();
        long radius = profile.getRadius();

        List<Location> eligible = new ArrayList<>();

        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {

                Location candidate = map.at(x, y);

                if (distanceSquared(candidate, location)
                        <= radius * radius
                        && candidate.getGround() != null
                        && candidate.getGround().hasAbility(
                        GroundAbility.METEOR_IMPACT_SITE)
                        && !candidate.containsAnActor()) {

                    eligible.add(candidate);
                }
            }
        }

        // Prioritise locations closest to the impact centre.
        eligible.sort(
                Comparator.comparingLong(
                        candidate -> distanceSquared(
                                candidate, location)
                )
        );

        int spawned = 0;

        for (Location candidate : eligible) {

            if (spawned >= profile.getGuardianCount()) {
                break;
            }

            AstralGuardian guardian = new AstralGuardian();

            if (!candidate.canActorEnter(guardian)) {
                continue;
            }

            try {
                candidate.addActor(guardian);
                spawned++;

            } catch (GameEngineException exception) {
                throw new IllegalStateException(
                        "Unable to spawn Astral Guardian at "
                                + candidate,
                        exception
                );
            }
        }
    }

    /**
     * Calculates the squared distance between two locations.
     *
     * @param first first location
     * @param second second location
     * @return squared distance between locations
     */
    private long distanceSquared(Location first, Location second) {

        long dx = (long) first.x() - second.x();
        long dy = (long) first.y() - second.y();

        return dx * dx + dy * dy;
    }
}
