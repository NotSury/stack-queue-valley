package game.magic;

import edu.monash.fit2099.engine.positions.GameMap;

import game.actors.Farmer;
import game.actions.ActivateMagicAction;
import game.companions.TimeRabbit;
import game.support.TestMaps;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the shared magical charge system for REQ4 companion.
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.0.0
 */
public class MagicalCompanionChargesTest {
    private GameMap map;
    private Farmer farmer;
    private TimeRabbit rabbit;

    /**
     * Creates a fresh map with the Farmer and TimeRabbit.
     *
     * @throws Exception if the map cannot be created
     */
    @BeforeEach
    void setUp() throws Exception {
        map = TestMaps.dirtMap();
        farmer = new Farmer("Test");
        rabbit = new TimeRabbit();

        map.addActor(farmer, map.at(4, 4));
        map.addActor(rabbit, map.at(5, 4));
    }

    /**
     * Normal case where each companion starts with three charges.
     */
    @Test
    void companionStartsWithThreeCharges() {
        assertEquals(3, rabbit.getRemainingCharges());
    }

    /**
     * Boundary case where charges cannot become negative.
     */
    @Test
    void chargesCannotGoBelowZero() {
        for (int i = 0; i < 5; i++) {
            rabbit.consumeCharge();
        }

        assertEquals(0, rabbit.getRemainingCharges());
        assertFalse(rabbit.hasCharges());
    }

    /**
     * Invalid case where unsuccessful magic does not spend a charge.
     */
    @Test
    void failedMagicDoesNotConsumeCharge() {
        ActivateMagicAction action =
                new ActivateMagicAction(new GrowthMagic(), rabbit);

        action.execute(farmer, map);

        assertEquals(3, rabbit.getRemainingCharges(),
                "Failed magic should not consume a charge");
    }
}
