package game.reactions;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.GroundAbility;
import game.events.ImpactProfile;
import game.grounds.CosmicCrop;
import game.grounds.ImpactCrater;
import game.interfaces.CelestialReaction;
import game.support.AreaLocationHelper;
import game.interfaces.CosmicMutable;

import java.util.Objects;
import java.util.Random;

/**
 * Mutates eligible crops within a celestial impact radius
 * or destroys them based on impact severity.
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public final class CropMutationReaction implements CelestialReaction {

    private final Random random;

    /**
     * Creates a reaction with a random source.
     */
    public CropMutationReaction() {
        this(new Random());
    }

    /**
     * Creates a reaction with a supplied random source.
     *
     * @param random source of mutation probability draws
     */
    public CropMutationReaction(Random random) {
        this.random = Objects.requireNonNull(random, "random");
    }

    /**
     * Mutates or destroys eligible crops within the impact radius.
     *
     * @param profile calculated celestial impact properties
     * @param location centre of the meteor impact
     */
    @Override
    public void apply(ImpactProfile profile, Location location) {

        Objects.requireNonNull(profile, "profile");
        Objects.requireNonNull(location, "location");

        double mutationChance = Math.max(
                0.20,
                Math.min(0.65, 0.65 - profile.getDamage() / 100.0)
        );

        for (Location affected : AreaLocationHelper.circle(
                location, profile.getRadius())) {

            Ground ground = affected.getGround();

            if (ground == null
                    || !ground.hasAbility(GroundAbility.CROP)
                    || !ground.hasAbility(
                    GroundAbility.METEOR_IMPACTABLE)) {
                continue;
            }

            CosmicCrop.Variety variety = ground
                    .asCapability(CosmicMutable.class)
                    .map(CosmicMutable::getCosmicVariety)
                    .orElse(null);

            if (variety == null) {
                continue;
            }

            if (random.nextDouble() < mutationChance) {
                affected.setGround(new CosmicCrop(variety));
            } else {
                affected.setGround(new ImpactCrater());
            }
        }
    }
}
