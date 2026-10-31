package game.reactions;

import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.World;

import game.actors.AstralGuardian;
import game.actors.Chicken;
import game.actors.Cow;
import game.actors.Farmer;
import game.events.ImpactProfile;
import game.grounds.CosmicCrop;
import game.grounds.Dirt;
import game.grounds.ImpactCrater;
import game.grounds.Soil;
import game.grounds.StarliteSoil;
import game.items.Fire;
import game.items.MeteoriteOre;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Deterministic tests for immediate celestial impact reactions.
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public class CelestialReactionTest {

    /**
     * Creates an isolated test map.
     *
     * @return test game map
     * @throws Exception if initialization fails
     */
    private GameMap createMap() throws Exception {

        DefaultGroundCreator creator = new DefaultGroundCreator();
        creator.registerGround('.', Dirt::new);

        GameMap map = new GameMap(
                "Reaction Test", creator, '.', 7, 7
        );

        World world = new World(new Display()) {};
        world.addGameMap(map);

        return map;
    }

    /**
     * Creates a fixed impact profile for testing.
     *
     * @param damage impact damage
     * @param oreCount number of meteorite ore pieces
     * @param guardians maximum guardians per event
     * @return test impact profile
     */
    private ImpactProfile profile(
            int damage,
            int oreCount,
            int guardians) {

        return new ImpactProfile(
                "Test Impact",
                3,
                2,
                damage,
                oreCount,
                guardians,
                2
        );
    }

    /**
     * Tests that terrain changes only on impactable ground.
     *
     * @throws Exception if map setup fails
     */
    @Test
    void terrainTransformsOnlyMeteorImpactableGround()
            throws Exception {

        GameMap map = createMap();

        Soil soil = new Soil();
        StarliteSoil protectedSoil = new StarliteSoil();

        CosmicCrop cosmic =
                new CosmicCrop(CosmicCrop.Variety.PARSNIP);

        map.at(4, 3).setGround(soil);
        map.at(3, 4).setGround(protectedSoil);
        map.at(2, 3).setGround(cosmic);

        new TerrainReaction().apply(
                profile(12, 3, 0),
                map.at(3, 3)
        );

        assertInstanceOf(
                ImpactCrater.class,
                map.at(3, 3).getGround()
        );

        assertInstanceOf(
                ImpactCrater.class,
                map.at(4, 3).getGround()
        );

        assertSame(
                protectedSoil,
                map.at(3, 4).getGround()
        );

        assertSame(
                cosmic,
                map.at(2, 3).getGround()
        );
    }

    /**
     * Tests ore generation on eligible and protected terrain.
     *
     * @throws Exception if map setup fails
     */
    @Test
    void resourceReactionProducesOreOnlyOnEligibleTerrain()
            throws Exception {

        GameMap map = createMap();

        ImpactProfile impact = profile(12, 3, 0);

        map.at(3, 3).setGround(new ImpactCrater());
        map.at(4, 3).setGround(new StarliteSoil());

        ResourceReaction reaction = new ResourceReaction();

        reaction.apply(impact, map.at(3, 3));
        reaction.apply(impact, map.at(4, 3));

        assertEquals(
                3L,
                map.at(3, 3).getItems().stream()
                        .filter(MeteoriteOre.class::isInstance)
                        .count()
        );

        assertTrue(map.at(4, 3).getItems().isEmpty());
    }

    /**
     * Tests that overlapping impacts do not repeatedly damage actors.
     *
     * @throws Exception if map setup fails
     */
    @Test
    void overlappingImpactsDamageEachActorOnlyOncePerEvent()
            throws Exception {

        GameMap map = createMap();

        Farmer farmer = new Farmer("Tester");
        Cow cow = new Cow();
        Chicken distant = new Chicken();

        map.addActor(farmer, map.at(3, 3));
        map.addActor(cow, map.at(4, 3));
        map.addActor(distant, map.at(6, 6));

        ImpactDamageReaction reaction =
                new ImpactDamageReaction();

        reaction.beginEvent();

        reaction.apply(
                profile(12, 0, 0),
                map.at(3, 3)
        );

        reaction.apply(
                profile(12, 0, 0),
                map.at(4, 3)
        );

        assertEquals(
                88,
                farmer.getStatistic(ActorStatistics.HEALTH)
        );

        assertEquals(
                8,
                cow.getStatistic(ActorStatistics.HEALTH)
        );

        assertEquals(
                10,
                distant.getStatistic(ActorStatistics.HEALTH)
        );
    }

    /**
     * Verifies damage tracking resets for subsequent events.
     *
     * @throws Exception if map setup fails
     */
    @Test
    void nextEventResetsDirectDamageTracking()
            throws Exception {

        GameMap map = createMap();
        Farmer farmer = new Farmer("Tester");

        map.addActor(farmer, map.at(3, 3));

        ImpactDamageReaction reaction =
                new ImpactDamageReaction();

        reaction.beginEvent();

        reaction.apply(
                profile(5, 0, 0),
                map.at(3, 3)
        );

        reaction.apply(
                profile(5, 0, 0),
                map.at(3, 3)
        );

        assertEquals(
                95,
                farmer.getStatistic(ActorStatistics.HEALTH)
        );

        reaction.beginEvent();

        reaction.apply(
                profile(5, 0, 0),
                map.at(3, 3)
        );

        assertEquals(
                90,
                farmer.getStatistic(ActorStatistics.HEALTH)
        );
    }

    /**
     * Tests guardian allocation and safe spawning locations.
     *
     * @throws Exception if map setup fails
     */
    @Test
    void guardiansRespectEventLimitAndAvoidFireAndOccupiedTiles()
            throws Exception {

        GameMap map = createMap();
        Farmer farmer = new Farmer("Tester");

        map.addActor(farmer, map.at(3, 3));

        map.at(3, 3).setGround(new ImpactCrater());

        // Unsafe crater with an existing fire.
        map.at(2, 3).setGround(new ImpactCrater());
        map.at(2, 3).addItem(new Fire(2, 5, 3));

        // Two safe crater locations.
        map.at(4, 3).setGround(new ImpactCrater());
        map.at(3, 4).setGround(new ImpactCrater());

        // Protected ground must never receive a guardian.
        map.at(4, 4).setGround(new StarliteSoil());

        GuardianReaction reaction = new GuardianReaction();
        reaction.beginEvent();

        ImpactProfile impact = profile(12, 0, 2);

        reaction.apply(impact, map.at(3, 3));
        reaction.apply(impact, map.at(4, 3));

        long guardianCount = 0;

        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {

                if (map.at(x, y).containsAnActor()
                        && map.at(x, y).getActor()
                        instanceof AstralGuardian) {

                    guardianCount++;
                }
            }
        }

        assertEquals(2L, guardianCount);

        assertSame(
                farmer,
                map.at(3, 3).getActor()
        );

        assertFalse(map.at(2, 3).containsAnActor());
        assertFalse(map.at(4, 4).containsAnActor());
    }

    /**
     * Tests impacts at a map boundary.
     *
     * @throws Exception if map setup fails
     */
    @Test
    void impactReactionsAreSafeAtMapCorner()
            throws Exception {

        GameMap map = createMap();

        Farmer farmer = new Farmer("Tester");
        map.addActor(farmer, map.at(0, 0));

        ImpactProfile impact = profile(12, 2, 0);

        new TerrainReaction().apply(
                impact, map.at(0, 0)
        );

        new ResourceReaction().apply(
                impact, map.at(0, 0)
        );

        ImpactDamageReaction damage =
                new ImpactDamageReaction();

        damage.beginEvent();
        damage.apply(impact, map.at(0, 0));

        assertInstanceOf(
                ImpactCrater.class,
                map.at(0, 0).getGround()
        );

        assertEquals(
                2L,
                map.at(0, 0).getItems().stream()
                        .filter(MeteoriteOre.class::isInstance)
                        .count()
        );

        assertEquals(
                88,
                farmer.getStatistic(ActorStatistics.HEALTH)
        );
    }
}
