package game.magic;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.displays.Display;

import game.actors.Farmer;
import game.actors.Slime;
import game.effects.LivingSoilEffect;
import game.effects.HarvestTrailEffect;
import game.effects.NatureSanctuaryEffect;
import game.grounds.Soil;
import game.grounds.StrawberryCrop;
import game.grounds.CauliflowerSprout;
import game.interfaces.PlantFactory;
import game.items.Strawberry;
import game.support.TestMaps;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the magical companion abilities introduced in REQ4.
 *
 * <p>Covers Living Soil, Magical Harvest Trail and Nature's Sanctuary.</p>
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.0.0
 */
public class MagicalCompanionsTest {
    private GameMap map;
    private Farmer farmer;

    /**
     * Creates a fresh map and Farmer before each test.
     *
     * @throws Exception if the map cannot be created
     */
    @BeforeEach
    void setUp() throws Exception {
        map = TestMaps.dirtMap();
        farmer = new Farmer("Test");
        map.addActor(farmer, map.at(4, 4));
    }

    /**
     * Tests that Living Soil converts Dirt but never overwrites
     * an existing crop.
     */
    @Test
    void livingSoilPreservesExistingCrops() {
        Location cropTile = map.at(5, 4);
        StrawberryCrop crop = new StrawberryCrop();
        cropTile.setGround(crop);

        List<PlantFactory> regrowth = List.<PlantFactory>of(
                StrawberryCrop::new,
                CauliflowerSprout::new
        );

        Random random = new Random(1);

        LivingSoilEffect effect = new LivingSoilEffect(
                map, map.at(4, 4), random, regrowth
        );

        effect.tick();

        assertSame(crop, cropTile.getGround(),
                "Living Soil must not overwrite existing crops");

        assertInstanceOf(Soil.class, map.at(3, 4).getGround(),
                "Nearby Dirt should become Soil");
    }

    /**
     * Tests that Magical Harvest Trail collects a maximum of
     * five fresh produce items.
     */
    @Test
    void harvestTrailCollectsAtMostFiveFreshProduce() {
        Location location = map.at(4, 3);

        int initialInventorySize = farmer.getInventory().getItems().size();

        for (int i = 0; i < 7; i++) {
            location.addItem(new Strawberry(false));
        }

        HarvestTrailEffect effect = new HarvestTrailEffect(
                map, map.at(4,4), farmer);

        effect.tick();

        int collected = farmer.getInventory().getItems().size() - initialInventorySize;

        assertEquals(5, collected,
                "Harvest Trail should collect exactly five produce");

        assertEquals(2, location.getItems().size(),
                "Two produce items should remain on the ground");

        assertTrue(effect.isExpired(),
                "Harvest Trail should stop after collecting five items");
    }

    /**
     * Tests the Nature's Sanctuary prevents hostile movement
     * into the shield for one creature turn, then expires.
     *
     * @throws Exception if the hostile creature cannot be placed on the map
     */
    @Test
    void sanctuaryRestrictionExpiresAfterOneTurn() throws  Exception {
        Slime hostile = new Slime();
        map.addActor(hostile, map.at(4, 2));

        NatureSanctuaryEffect effect = new NatureSanctuaryEffect(map, map.at(4, 4));

        effect.tick();

        Location protectedTile = map.at(4,3);

        assertTrue(hostile.isForbidden(protectedTile),
                "Hostile should initially be restricted");

        hostile.playTurn(new ActionList(), null, map, new Display());

        assertTrue(hostile.isForbidden(protectedTile),
                "Restriction must remain during the first creature turn");

        hostile.playTurn(new ActionList(), null, map, new Display());

        assertFalse(hostile.isForbidden(protectedTile),
                "Restriction should expire on the following turn");
    }

}
