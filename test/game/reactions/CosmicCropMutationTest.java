package game.reactions;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.World;

import game.actors.Farmer;
import game.capabilities.GroundAbility;
import game.events.ImpactProfile;
import game.grounds.*;
import game.items.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests celestial crop mutations, destruction and harvesting.
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public class CosmicCropMutationTest {

    /**
     * Creates an isolated game map.
     *
     * @return test map
     * @throws Exception if initialization fails
     */
    private GameMap createMap() throws Exception {
        DefaultGroundCreator creator = new DefaultGroundCreator();
        creator.registerGround('.', Dirt::new);

        GameMap map = new GameMap(
                "Cosmic Crop Test", creator, '.', 7, 7
        );

        World world = new World(new Display()) {};
        world.addGameMap(map);

        return map;
    }

    /**
     * Creates an impact profile for a specified damage value.
     *
     * @param damage meteor damage
     * @return test impact profile
     */
    private ImpactProfile profile(int damage) {
        return new ImpactProfile(
                "Test Impact", 1, 1, damage, 2, 0, 2
        );
    }

    /**
     * Ensures all five crop types mutate successfully
     * when the random roll is zero.
     */
    @Test
    void allFiveCropsCanMutate() throws Exception {
        GameMap map = createMap();

        map.at(3, 3).setGround(new ParsnipCrop());
        map.at(3, 2).setGround(new TomatoCrop());
        map.at(3, 4).setGround(new CauliflowerCrop());
        map.at(2, 3).setGround(new MelonCrop());
        map.at(4, 3).setGround(new StrawberryCrop());

        Random alwaysMutate = new Random() {
            @Override
            public double nextDouble() {
                return 0.0;
            }
        };

        new CropMutationReaction(alwaysMutate)
                .apply(profile(20), map.at(3, 3));

        CosmicCrop.Variety[] expected = {
                CosmicCrop.Variety.PARSNIP,
                CosmicCrop.Variety.TOMATO,
                CosmicCrop.Variety.CAULIFLOWER,
                CosmicCrop.Variety.MELON,
                CosmicCrop.Variety.STRAWBERRY
        };

        int[][] positions = {
                {3, 3}, {3, 2}, {3, 4}, {2, 3}, {4, 3}
        };

        for (int i = 0; i < positions.length; i++) {
            Ground ground = map.at(
                    positions[i][0],
                    positions[i][1]
            ).getGround();

            CosmicCrop crop = assertInstanceOf(
                    CosmicCrop.class, ground
            );

            assertEquals(expected[i], crop.getVariety());
            assertTrue(ground.hasAbility(GroundAbility.COSMIC_CROP));
        }

        // Mutated crops must survive crater creation.
        new TerrainReaction().apply(
                profile(20), map.at(3, 3)
        );

        for (int[] position : positions) {
            assertInstanceOf(
                    CosmicCrop.class,
                    map.at(position[0], position[1]).getGround()
            );
        }
    }

    /**
     * Checks mutation outcomes under different meteor damage values.
     * A fixed roll of 0.5 gives reproducible results.
     *
     * @param damage meteor damage
     * @param shouldMutate expected mutation outcome
     */
    @ParameterizedTest
    @CsvSource({
            "10, true",
            "20, false",
            "100, false"
    })
    void mutationProbabilityDependsOnDamage(
            int damage,
            boolean shouldMutate) throws Exception {

        GameMap map = createMap();
        map.at(3, 3).setGround(new ParsnipCrop());

        Random fixedRandom = new Random() {
            @Override
            public double nextDouble() {
                return 0.5;
            }
        };

        new CropMutationReaction(fixedRandom)
                .apply(profile(damage), map.at(3, 3));

        if (shouldMutate) {
            assertInstanceOf(
                    CosmicCrop.class,
                    map.at(3, 3).getGround()
            );
        } else {
            assertInstanceOf(
                    ImpactCrater.class,
                    map.at(3, 3).getGround()
            );
        }
    }

    /**
     * Ensures the mutation reaction does not change existing
     * cosmic crops or crops outside the impact radius.
     */
    @Test
    void protectedAndDistantCropsRemainUnchanged()
            throws Exception {

        GameMap map = createMap();

        CosmicCrop protectedCrop =
                new CosmicCrop(CosmicCrop.Variety.PARSNIP);

        StrawberryCrop distantCrop = new StrawberryCrop();

        map.at(3, 3).setGround(protectedCrop);
        map.at(6, 6).setGround(distantCrop);

        new CropMutationReaction(new Random(1))
                .apply(profile(20), map.at(3, 3));

        assertSame(
                protectedCrop,
                map.at(3, 3).getGround()
        );

        assertSame(
                distantCrop,
                map.at(6, 6).getGround()
        );
    }

    /**
     * Verifies that every Cosmic Crop variety produces
     * its matching consumable when harvested.
     *
     * @param variety crop variety to harvest
     */
    @ParameterizedTest
    @EnumSource(CosmicCrop.Variety.class)
    void harvestingProducesMatchingCosmicItem(
            CosmicCrop.Variety variety) throws Exception {

        GameMap map = createMap();
        Farmer farmer = new Farmer("Tester");

        map.addActor(farmer, map.at(2, 1));

        CosmicCrop crop = new CosmicCrop(variety);
        map.at(2, 2).setGround(crop);

        int inventoryBefore =
                farmer.getInventory().getItems().size();

        crop.harvest(farmer, map.at(2, 2));

        Class<?> expectedItem = switch (variety) {
            case PARSNIP -> CosmicParsnip.class;
            case TOMATO -> CosmicTomato.class;
            case CAULIFLOWER -> CosmicCauliflower.class;
            case MELON -> CosmicMelon.class;
            case STRAWBERRY -> CosmicStrawberry.class;
        };

        assertTrue(
                farmer.getInventory().getItems().stream()
                        .anyMatch(expectedItem::isInstance)
        );

        assertEquals(
                inventoryBefore + 1,
                farmer.getInventory().getItems().size()
        );

        assertInstanceOf(
                Dirt.class,
                map.at(2, 2).getGround()
        );
    }

    /**
     * Verifies that subclasses of existing mutable crops can
     * mutate without being registered in a concrete-class map.
     *
     * @throws Exception if map creation fails
     */
    @Test
    void subclassOfMutableCropCanMutate() throws Exception {

        GameMap map = createMap();

        // This anonymous subclass would not match the old
        // VARIETIES map, which used exact concrete classes.
        ParsnipCrop subclass = new ParsnipCrop() {
        };

        map.at(3, 3).setGround(subclass);

        Random alwaysMutate = new Random() {
            @Override
            public double nextDouble() {
                return 0.0;
            }
        };

        CropMutationReaction reaction =
                new CropMutationReaction(alwaysMutate);

        reaction.apply(profile(20), map.at(3, 3));

        CosmicCrop result = assertInstanceOf(
                CosmicCrop.class,
                map.at(3, 3).getGround()
        );

        assertEquals(
                CosmicCrop.Variety.PARSNIP,
                result.getVariety()
        );
    }
}
