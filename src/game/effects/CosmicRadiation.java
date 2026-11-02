package game.effects;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.GroundAbility;
import game.items.Fire;
import game.navigation.MovementSafety;
import game.statuses.RadiationImmunityStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * A temporary radiation field created by a celestial storm.
 *
 * <p>Each field tick damages unprotected actors within its area
 * and creates up to one temporary fire on safe ground.</p>
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public final class CosmicRadiation extends EnvironmentalField {

    private static final int FIRE_DURATION = 2;
    private static final int FIRE_DAMAGE_PER_TURN = 5;
    private static final int FIRE_BURN_DURATION = 3;
    private static final int MAX_RADIATION_DAMAGE = 10;

    private final int radiationDamage;
    private final Random random;

    /**
     * Creates radiation using a NASA-derived impact profile.
     *
     * @param radius impact radius
     * @param durationTurns impact effect duration
     * @param impactDamage calculated impact damage
     */
    public CosmicRadiation(
            int radius,
            int durationTurns,
            int impactDamage) {

        this(radius, durationTurns, impactDamage, new Random());
    }

    /**
     * Creates radiation with a supplied random source for testing.
     *
     * @param radius impact radius
     * @param durationTurns impact effect duration
     * @param impactDamage calculated impact damage
     * @param random random source for fire placement
     */
    public CosmicRadiation(
            int radius,
            int durationTurns,
            int impactDamage,
            Random random) {

        super(
                "Cosmic Radiation",
                'i',
                radius,
                durationTurns
        );

        if (impactDamage <= 0) {
            throw new IllegalArgumentException(
                    "Impact damage must be positive"
            );
        }

        this.radiationDamage = Math.max(
                1,
                Math.min(
                        MAX_RADIATION_DAMAGE,
                        impactDamage / 4
                )
        );

        this.random = Objects.requireNonNull(
                random, "random"
        );
    }

    /**
     * Applies radiation damage and generates a fire.
     *
     * @param centre centre of the environmental field
     */
    @Override
    protected void applyFieldEffect(Location centre) {

        List<Location> affected = affectedLocations(centre);

        // Apply radiation to every actor currently inside the field.
        for (Location location : affected) {

            if (!location.containsAnActor()) {
                continue;
            }

            Actor actor = location.getActor();

            if (!actor.isConscious()) {
                continue;
            }

            // Cosmic Strawberry prevents radiation damage.
            if (!actor.hasStatus(RadiationImmunityStatus.class)) {

                actor.hurt(radiationDamage);

                // Use the actor's own death handling.
                if (!actor.isConscious()) {
                    actor.unconscious(location.map());
                }
            }
        }

        // Find suitable locations for a new fire.
        List<Location> available = new ArrayList<>();

        for (Location location : affected) {

            boolean suitableGround =
                    location.getGround().hasAbility(
                            GroundAbility.OPEN_GROUND)
                            || location.getGround().hasAbility(
                            GroundAbility.METEOR_IMPACT_SITE);

            if (!location.containsAnActor()
                    && suitableGround
                    && MovementSafety.isSafe(location)) {

                available.add(location);
            }
        }

        // Create at most one fire per field tick.
        if (!available.isEmpty()) {

            Location fireSite = available.get(
                    random.nextInt(available.size())
            );

            fireSite.addItem(
                    new Fire(
                            FIRE_DURATION,
                            FIRE_DAMAGE_PER_TURN,
                            FIRE_BURN_DURATION
                    )
            );
        }
    }

    /**
     * Returns damage applied to an unprotected actor each tick.
     *
     * @return radiation damage per field tick
     */
    public int getRadiationDamage() {
        return radiationDamage;
    }
}

