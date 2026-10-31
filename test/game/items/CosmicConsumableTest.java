package game.items;

import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.World;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

import game.actors.Cow;
import game.actors.Farmer;
import game.effects.CosmicRadiation;
import game.grounds.Dirt;
import game.grounds.Puddle;
import game.statuses.MelonBurstStatus;
import game.statuses.RadiationImmunityStatus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the gameplay effects of all five Cosmic Items.
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public class CosmicConsumableTest {

    /**
     * Creates an isolated map for tests requiring locations.
     *
     * @return test map
     * @throws Exception if initialization fails
     */
    private GameMap createMap() throws Exception {
        DefaultGroundCreator creator = new DefaultGroundCreator();
        creator.registerGround('.', Dirt::new);

        GameMap map = new GameMap(
                "Cosmic Item Test", creator, '.', 7, 7
        );

        World world = new World(new Display()) {};
        world.addGameMap(map);

        return map;
    }

    /**
     * Tests Cosmic Parsnip healing, including maximum health.
     *
     * @param healthLost initial health lost
     * @param expectedHealth expected health after consumption
     */
    @ParameterizedTest
    @CsvSource({
            "10, 100",
            "30, 100",
            "40, 90"
    })
    void cosmicParsnipRestoresHealth(
            int healthLost,
            int expectedHealth) {

        Farmer farmer = new Farmer("Tester");
        farmer.hurt(healthLost);

        CosmicParsnip item = new CosmicParsnip();
        farmer.getInventory().add(item);

        item.consume(farmer);

        assertEquals(
                expectedHealth,
                farmer.getStatistic(ActorStatistics.HEALTH)
        );

        assertFalse(
                farmer.getInventory().getItems().contains(item)
        );
    }

    /**
     * Tests Cosmic Tomato stamina recovery and maximum limits.
     *
     * @param staminaLost starting stamina loss
     * @param expectedStamina expected final stamina
     */
    @ParameterizedTest
    @CsvSource({
            "0, 20",
            "3, 20",
            "12, 16"
    })
    void cosmicTomatoRestoresStamina(
            int staminaLost,
            int expectedStamina) {

        Farmer farmer = new Farmer("Tester");

        farmer.modifyStatistic(
                ActorStatistics.STAMINA,
                StatisticOperations.DECREASE,
                staminaLost
        );

        CosmicTomato item = new CosmicTomato();
        farmer.getInventory().add(item);

        item.consume(farmer);

        assertEquals(
                expectedStamina,
                farmer.getStatistic(ActorStatistics.STAMINA)
        );

        assertFalse(
                farmer.getInventory().getItems().contains(item)
        );
    }

    /**
     * Tests Cosmic Cauliflower regeneration over exactly two ticks.
     */
    @Test
    void cosmicCauliflowerRegeneratesForTwoTurns()
            throws Exception {

        GameMap map = createMap();
        Farmer farmer = new Farmer("Tester");

        map.addActor(farmer, map.at(3, 3));
        farmer.hurt(35);

        new CosmicCauliflower().consume(farmer);

        farmer.tickStatuses(map.at(3, 3));

        assertEquals(
                75,
                farmer.getStatistic(ActorStatistics.HEALTH)
        );

        farmer.tickStatuses(map.at(3, 3));

        assertEquals(
                85,
                farmer.getStatistic(ActorStatistics.HEALTH)
        );

        // No additional regeneration after expiration.
        farmer.tickStatuses(map.at(3, 3));

        assertEquals(
                85,
                farmer.getStatistic(ActorStatistics.HEALTH)
        );
    }

    /**
     * Tests Cosmic Melon's delayed explosion.
     * Adjacent actors take damage, but the consumer is protected.
     */
    @Test
    void cosmicMelonDamagesNearbyActor()
            throws Exception {

        GameMap map = createMap();

        Farmer farmer = new Farmer("Tester");
        Cow cow = new Cow();

        map.addActor(farmer, map.at(3, 3));
        map.addActor(cow, map.at(4, 3));

        new CosmicMelon().consume(farmer);

        assertTrue(
                farmer.hasStatus(MelonBurstStatus.class)
        );

        farmer.tickStatuses(map.at(3, 3));

        assertEquals(
                8,
                cow.getStatistic(ActorStatistics.HEALTH)
        );

        assertEquals(
                100,
                farmer.getStatistic(ActorStatistics.HEALTH)
        );

        assertFalse(
                farmer.hasStatus(MelonBurstStatus.class)
        );
    }

    /**
     * Tests that Cosmic Strawberry blocks two radiation field
     * ticks before radiation damage begins.
     */
    @Test
    void cosmicStrawberryBlocksTwoRadiationTicks()
            throws Exception {

        GameMap map = createMap();

        // Make all tiles unsuitable for fire generation so the
        // test measures only radiation and immunity.
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                map.at(x, y).setGround(new Puddle());
            }
        }

        Farmer farmer = new Farmer("Tester");
        map.addActor(farmer, map.at(3, 3));

        new CosmicStrawberry().consume(farmer);

        CosmicRadiation radiation =
                new CosmicRadiation(
                        1, 3, 36, new Random(1)
                );

        radiation.placeAt(map.at(3, 3));

        map.tick();

        assertEquals(
                100,
                farmer.getStatistic(ActorStatistics.HEALTH)
        );

        map.tick();

        assertEquals(
                100,
                farmer.getStatistic(ActorStatistics.HEALTH)
        );

        map.tick();

        assertEquals(
                91,
                farmer.getStatistic(ActorStatistics.HEALTH)
        );

        assertFalse(radiation.isActive());
    }

    /**
     * Tests consuming another Strawberry refreshes the
     * immunity duration rather than stacking statuses.
     */
    @Test
    void strawberryRefreshesExistingImmunity()
            throws Exception {

        GameMap map = createMap();

        Farmer farmer = new Farmer("Tester");
        map.addActor(farmer, map.at(3, 3));

        new CosmicStrawberry().consume(farmer);

        farmer.tickStatuses(map.at(3, 3));
        farmer.tickStatuses(map.at(3, 3));

        // Refresh the existing protection.
        new CosmicStrawberry().consume(farmer);

        assertEquals(
                1,
                farmer.statusesOf(
                        RadiationImmunityStatus.class
                ).size()
        );

        farmer.tickStatuses(map.at(3, 3));
        farmer.tickStatuses(map.at(3, 3));

        assertTrue(
                farmer.hasStatus(RadiationImmunityStatus.class)
        );

        farmer.tickStatuses(map.at(3, 3));

        assertFalse(
                farmer.hasStatus(RadiationImmunityStatus.class)
        );
    }
}
