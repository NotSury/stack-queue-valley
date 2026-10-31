package game.effects;

import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.World;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actors.Chicken;
import game.actors.Cow;
import game.actors.Farmer;
import game.grounds.Dirt;
import game.grounds.ImpactCrater;
import game.grounds.Puddle;
import game.grounds.Rock;
import game.grounds.Soil;
import game.grounds.StarliteSoil;
import game.items.Fire;
import game.items.MeteoriteOre;
import game.items.StarliteOre;
import game.items.Stone;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Deterministic tests for celestial environmental fields.
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public class EnvironmentalFieldTest {

    /**
     * Creates a map registered with its own game world.
     *
     * @return an isolated test map
     * @throws Exception if initialization fails
     */
    private GameMap createMap() throws Exception {
        DefaultGroundCreator creator = new DefaultGroundCreator();
        creator.registerGround('.', Dirt::new);

        GameMap map = new GameMap(
                "Field Test", creator, '.', 7, 7
        );

        World world = new World(new Display()) {};
        world.addGameMap(map);

        return map;
    }

    /**
     * Tests radius limits, duration limits and invalid inputs.
     */
    @Test
    void fieldBoundsAndInvalidConfiguration() {

        AstralBlessing shortField = new AstralBlessing(1, 1);
        AstralBlessing normalField = new AstralBlessing(2, 3);
        AstralBlessing longField = new AstralBlessing(9, 20);

        assertEquals(1, shortField.getRadius());
        assertEquals(2, shortField.getRemainingTurns());

        assertEquals(2, normalField.getRadius());
        assertEquals(3, normalField.getRemainingTurns());

        assertEquals(2, longField.getRadius());
        assertEquals(4, longField.getRemainingTurns());

        assertThrows(
                IllegalArgumentException.class,
                () -> new AstralBlessing(0, 3)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new AstralBlessing(1, 0)
        );
    }

    /**
     * Tests radiation damage, range and field expiration.
     *
     * @throws Exception if map setup fails
     */
    @Test
    void radiationDamagesOnlyActorsInRangeAndExpires()
            throws Exception {

        GameMap map = createMap();

        // Prevent fire generation to isolate radiation damage.
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                map.at(x, y).setGround(new Puddle());
            }
        }

        Farmer farmer = new Farmer("Tester");
        Cow cow = new Cow();
        Chicken distantChicken = new Chicken();

        map.addActor(farmer, map.at(3, 3));
        map.addActor(cow, map.at(4, 3));
        map.addActor(distantChicken, map.at(6, 6));

        CosmicRadiation radiation =
                new CosmicRadiation(2, 2, 36, new Random(1));

        radiation.placeAt(map.at(3, 3));

        map.tick();

        assertEquals(
                91,
                farmer.getStatistic(ActorStatistics.HEALTH)
        );

        assertEquals(
                11,
                cow.getStatistic(ActorStatistics.HEALTH)
        );

        assertEquals(
                10,
                distantChicken.getStatistic(ActorStatistics.HEALTH)
        );

        map.tick();

        assertEquals(
                82,
                farmer.getStatistic(ActorStatistics.HEALTH)
        );

        assertEquals(
                2,
                cow.getStatistic(ActorStatistics.HEALTH)
        );

        assertFalse(radiation.isActive());

        assertFalse(
                map.at(3, 3).getItems().contains(radiation)
        );

        // The expired field must not cause further damage.
        map.tick();

        assertEquals(
                82,
                farmer.getStatistic(ActorStatistics.HEALTH)
        );
    }

    /**
     * Tests that radiation generates fire only on eligible ground.
     *
     * @throws Exception if map setup fails
     */
    @Test
    void radiationCreatesFireOnlyOnEligibleGround()
            throws Exception {

        GameMap map = createMap();

        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                map.at(x, y).setGround(new Puddle());
            }
        }

        // The only eligible tile inside radius one.
        map.at(4, 3).setGround(new Dirt());

        CosmicRadiation radiation =
                new CosmicRadiation(1, 2, 36, new Random(0));

        radiation.placeAt(map.at(3, 3));
        map.tick();

        long totalFires = 0;

        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                totalFires += map.at(x, y).getItems()
                        .stream()
                        .filter(Fire.class::isInstance)
                        .count();
            }
        }

        assertEquals(1L, totalFires);

        assertTrue(
                map.at(4, 3).getItems().stream()
                        .anyMatch(Fire.class::isInstance)
        );

        assertTrue(
                map.at(3, 3).getItems().stream()
                        .noneMatch(Fire.class::isInstance)
        );
    }

    /**
     * Tests blessing recovery, crater repair and ore refinement.
     *
     * @throws Exception if map setup fails
     */
    @Test
    void blessingRepairsOnlyCratersAndRefinesNearbyOre()
            throws Exception {

        GameMap map = createMap();
        Farmer farmer = new Farmer("Tester");

        map.addActor(farmer, map.at(3, 3));

        farmer.hurt(20);
        farmer.modifyStatistic(
                ActorStatistics.STAMINA,
                StatisticOperations.DECREASE,
                8
        );

        Soil originalSoil = new Soil();

        map.at(3, 3).setGround(new ImpactCrater());
        map.at(4, 3).setGround(new ImpactCrater());
        map.at(2, 3).setGround(originalSoil);

        // This crater is outside the field.
        map.at(6, 6).setGround(new ImpactCrater());

        map.at(3, 3).addItem(new MeteoriteOre());
        map.at(3, 3).addItem(new Stone());
        map.at(6, 6).addItem(new MeteoriteOre());

        AstralBlessing blessing = new AstralBlessing(2, 2);
        blessing.placeAt(map.at(3, 3));

        map.tick();

        assertEquals(
                84,
                farmer.getStatistic(ActorStatistics.HEALTH)
        );

        assertEquals(
                14,
                farmer.getStatistic(ActorStatistics.STAMINA)
        );

        assertInstanceOf(
                ImpactCrater.class,
                map.at(3, 3).getGround()
        );

        map.tick();

        assertEquals(
                88,
                farmer.getStatistic(ActorStatistics.HEALTH)
        );

        assertEquals(
                16,
                farmer.getStatistic(ActorStatistics.STAMINA)
        );

        assertFalse(blessing.isActive());

        assertInstanceOf(
                StarliteSoil.class,
                map.at(3, 3).getGround()
        );

        assertInstanceOf(
                StarliteSoil.class,
                map.at(4, 3).getGround()
        );

        assertSame(
                originalSoil,
                map.at(2, 3).getGround()
        );

        assertInstanceOf(
                ImpactCrater.class,
                map.at(6, 6).getGround()
        );

        assertTrue(
                map.at(3, 3).getItems().stream()
                        .anyMatch(StarliteOre.class::isInstance)
        );

        assertTrue(
                map.at(3, 3).getItems().stream()
                        .anyMatch(Stone.class::isInstance)
        );

        assertTrue(
                map.at(6, 6).getItems().stream()
                        .anyMatch(MeteoriteOre.class::isInstance)
        );
    }

    /**
     * Tests resource attraction, fuel absorption and stamina drain.
     *
     * @throws Exception if map setup fails
     */
    @Test
    void gravityPullsOreOneTilePerTurnAndUsesItAsFuel()
            throws Exception {

        GameMap map = createMap();
        Farmer farmer = new Farmer("Tester");

        map.addActor(farmer, map.at(3, 3));

        MeteoriteOre near = new MeteoriteOre();
        MeteoriteOre far = new MeteoriteOre();
        Stone unrelated = new Stone();

        map.at(3, 2).addItem(near);
        map.at(3, 1).addItem(far);
        map.at(3, 1).addItem(unrelated);

        GravitationalDistortion gravity =
                new GravitationalDistortion(2, 2);

        gravity.placeAt(map.at(3, 3));

        // First tick: near ore is absorbed, far ore moves.
        map.tick();

        assertFalse(map.at(3, 2).getItems().contains(near));
        assertTrue(map.at(3, 2).getItems().contains(far));
        assertTrue(map.at(3, 1).getItems().contains(unrelated));

        assertEquals(
                2,
                gravity.getRemainingTurns()
        );

        assertEquals(
                17,
                farmer.getStatistic(ActorStatistics.STAMINA)
        );

        // Second tick: the far ore is absorbed.
        map.tick();

        assertFalse(map.at(3, 2).getItems().contains(far));
        assertEquals(2, gravity.getRemainingTurns());

        assertEquals(
                14,
                farmer.getStatistic(ActorStatistics.STAMINA)
        );

        map.tick();
        map.tick();

        assertFalse(gravity.isActive());

        assertEquals(
                8,
                farmer.getStatistic(ActorStatistics.STAMINA)
        );
    }

    /**
     * Tests that protected ground and fire block ore movement.
     *
     * @throws Exception if map setup fails
     */
    @Test
    void gravityCannotMoveOreThroughRockOrFire()
            throws Exception {

        GameMap rockMap = createMap();

        MeteoriteOre blockedByRock = new MeteoriteOre();

        rockMap.at(3, 1).addItem(blockedByRock);
        rockMap.at(3, 2).setGround(new Rock());

        new GravitationalDistortion(2, 2)
                .placeAt(rockMap.at(3, 3));

        rockMap.tick();

        assertTrue(
                rockMap.at(3, 1).getItems().contains(blockedByRock)
        );

        GameMap fireMap = createMap();

        MeteoriteOre blockedByFire = new MeteoriteOre();

        fireMap.at(3, 1).addItem(blockedByFire);
        fireMap.at(3, 2).addItem(new Fire(2, 5, 3));

        new GravitationalDistortion(2, 2)
                .placeAt(fireMap.at(3, 3));

        fireMap.tick();

        assertTrue(
                fireMap.at(3, 1).getItems().contains(blockedByFire)
        );
    }

    /**
     * Tests the four-turn bonus cap for absorbed meteorite ore.
     *
     * @throws Exception if map setup fails
     */
    @Test
    void gravityExtensionsAreCappedAtFourBonusTurns()
            throws Exception {

        GameMap map = createMap();

        for (int i = 0; i < 6; i++) {
            map.at(2, 3).addItem(new MeteoriteOre());
        }

        GravitationalDistortion gravity =
                new GravitationalDistortion(2, 2);

        gravity.placeAt(map.at(3, 3));
        map.tick();

        // Two initial turns + four bonus - one elapsed tick.
        assertEquals(5, gravity.getRemainingTurns());

        // Two extra ore pieces remain collectible.
        assertEquals(
                2L,
                map.at(3, 3).getItems().stream()
                        .filter(MeteoriteOre.class::isInstance)
                        .count()
        );

        for (int i = 0; i < 5; i++) {
            map.tick();
        }

        assertFalse(gravity.isActive());
    }
}
