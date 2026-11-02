package game.magic;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import game.actors.Farmer;
import game.companions.TimeRabbit;
import game.effects.TemporalFreezeEffect;
import game.grounds.StrawberrySeedling;
import game.grounds.StrawberrySprout;
import game.interfaces.GrowthAcceleratable;
import game.support.TestMaps;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests TimeRabbit's Growth Magic and Temporal Freeze abilities.
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.0.0
 */
public class GrowthAndFreezeTest {
    private GameMap map;
    private Farmer farmer;
    private TimeRabbit rabbit;

    /**
     * Creates a fresh map with a Farmer and TimeRabbit.
     *
     * @throws Exception if the map cannot be created
     */
    @BeforeEach
    void setUp() throws Exception {
        map = TestMaps.dirtMap();

        farmer = new Farmer("Test");
        rabbit = new TimeRabbit();

        map.addActor(farmer, map.at(4,4));
        map.addActor(rabbit, map.at(5,4));
    }

    /**
     * Normal case where Growth Magic advances a seedling by one stage.
     */
    @Test
    void growthMagicAdvancesSeedling() {
        Location plantTile = map.at(5, 3);
        plantTile.setGround(new StrawberrySeedling(new Random(1)));

        GrowthMagic magic = new GrowthMagic();

        boolean success = magic.cast(farmer, rabbit, map);

        assertTrue(success, "Growth Magic should succeed when a seedling is nearby");

        assertEquals(StrawberrySprout.class, plantTile.getGround().getClass(),
                "Seedling should advance into a Strawberry Sprout");
    }

    /**
     * Edge case where frozen plants cannot advance until the freeze ends.
     */
    @Test
    void temporalFreezeBlocksGrowthUtilExpiry() {
        Location plantTile = map.at(5, 3);
        plantTile.setGround(new StrawberrySprout(new Random(1)));

        TemporalFreezeEffect freeze = new TemporalFreezeEffect(map, map.at(5, 4));

        freeze.tick();

        Optional<GrowthAcceleratable> frozenPlant = plantTile.getGround()
                .asCapability(GrowthAcceleratable.class);

        assertTrue(frozenPlant.isPresent(),
                "The sprout should support growth acceleration");

        assertFalse(frozenPlant.get().advanceGrowth(plantTile),
                "Growth must be blocked while the plant is frozen");

        freeze.tick();
        freeze.tick();

        assertTrue(freeze.isExpired(),
                "Temporal Freeze should expire after three ticks");

        freeze.finish();

        plantTile.getGround().tick(plantTile);

        Optional<GrowthAcceleratable> unfrozenPlant = plantTile.getGround()
                .asCapability(GrowthAcceleratable.class);

        assertTrue(unfrozenPlant.isPresent());

        assertTrue(unfrozenPlant.get().advanceGrowth(plantTile),
                "Growth should be possible after the freeze ends");

    }
}
