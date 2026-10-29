package game.reactions;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.GroundAbility;
import game.events.ImpactProfile;
import game.grounds.*;
import game.interfaces.CelestialReaction;

import java.util.Map;
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

    private static final Map<Class<?>, CosmicCrop.Variety> VARIETIES =
            Map.of(
                    ParsnipCrop.class, CosmicCrop.Variety.PARSNIP,
                    TomatoCrop.class, CosmicCrop.Variety.TOMATO,
                    CauliflowerCrop.class, CosmicCrop.Variety.CAULIFLOWER,
                    MelonCrop.class, CosmicCrop.Variety.MELON,
                    StrawberryCrop.class, CosmicCrop.Variety.STRAWBERRY
            );

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
     * Mutates or destroys crops within the impact radius.
     *
     * @param profile calculated celestial impact properties
     * @param location centre of the impact
     */
    @Override
    public void apply(ImpactProfile profile, Location location) {

        Objects.requireNonNull(profile, "profile");
        Objects.requireNonNull(location, "location");

        GameMap map = location.map();
        long radius = profile.getRadius();

        double mutationChance = Math.max(
                0.20,
                Math.min(0.65, 0.65 - profile.getDamage() / 100.0)
        );

        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {

                long dx = (long) x - location.x();
                long dy = (long) y - location.y();

                if (dx * dx + dy * dy > radius * radius) {
                    continue;
                }

                Location affected = map.at(x, y);
                Ground ground = affected.getGround();

                if (ground == null
                        || !ground.hasAbility(GroundAbility.CROP)
                        || !ground.hasAbility(
                        GroundAbility.METEOR_IMPACTABLE)) {
                    continue;
                }

                CosmicCrop.Variety variety =
                        VARIETIES.get(ground.getClass());

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
}
