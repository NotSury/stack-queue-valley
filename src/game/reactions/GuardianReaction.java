package game.reactions;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.AstralGuardian;
import game.capabilities.GroundAbility;
import game.events.ImpactProfile;
import game.interfaces.CelestialReaction;
import game.navigation.MovementSafety;
import game.support.AreaLocationHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Spawns Astral Guardians on unoccupied impact craters.
 *
 * <p>The profile's guardian count is treated as the total
 * allocation for one celestial event, not a separate
 * allocation for each impact site.</p>
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.1.0
 */
public final class GuardianReaction
        implements CelestialReaction {

    private int spawnedThisEvent;

    /**
     * Resets the guardian allocation for a new event.
     */
    @Override
    public void beginEvent() {
        spawnedThisEvent = 0;
    }

    /**
     * Spawns guardians at safe impact crater locations
     * without exceeding the event's guardian allocation.
     *
     * @param profile calculated celestial impact properties
     * @param location centre of the meteor impact
     */
    @Override
    public void apply(ImpactProfile profile, Location location) {

        Objects.requireNonNull(profile, "profile");
        Objects.requireNonNull(location, "location");

        if (spawnedThisEvent >= profile.getGuardianCount()) {
            return;
        }

        List<Location> eligible = new ArrayList<>();

        for (Location candidate : AreaLocationHelper.circle(
                location, profile.getRadius())) {

            if (candidate.getGround() != null
                    && candidate.getGround().hasAbility(
                    GroundAbility.METEOR_IMPACT_SITE)
                    && !candidate.containsAnActor()
                    && MovementSafety.isSafe(candidate)) {

                eligible.add(candidate);
            }
        }

        eligible.sort(
                Comparator.comparingLong(
                        candidate -> AreaLocationHelper.distanceSquared(
                                candidate, location)
                )
        );

        for (Location candidate : eligible) {

            if (spawnedThisEvent
                    >= profile.getGuardianCount()) {
                break;
            }

            AstralGuardian guardian = new AstralGuardian();

            if (!candidate.canActorEnter(guardian)) {
                continue;
            }

            try {
                candidate.addActor(guardian);
                spawnedThisEvent++;

            } catch (GameEngineException exception) {
                throw new IllegalStateException(
                        "Unable to spawn Astral Guardian at "
                                + candidate,
                        exception
                );
            }
        }
    }
}
