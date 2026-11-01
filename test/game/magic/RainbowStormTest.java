package game.magic;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.actors.ActorStatistics;

import game.actors.Farmer;
import game.actors.Slime;
import game.effects.RainbowStormEffect;
import game.support.TestMaps;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests PrismFox's Rainbow Storm ability.
 *
 * <p>Verifies that hostile creatures inside the storm are
 * damaged and stunned, while creature outside the storm remain
 * unaffected.</p>
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.0.0
 */
public class RainbowStormTest {
    private GameMap map;
    private Farmer farmer;

    /**
     * Creates a fresh map before each test.
     *
     * @throws Exception if the map cannot be created
     */
    @BeforeEach
    void setUp() throws Exception {
        map = TestMaps.dirtMap();

        farmer = new Farmer("Test");
        map.addActor(farmer, map.at(4,4));
    }

    /**
     * Normal case when a hostile inside Rainbow Storm takes
     * damage and cannot act because it is stunned.
     *
     * @throws Exception if the actor cannot be placed
     */
    @Test
    void rainbowStormDamagesAndStunsHostile() throws Exception {
        Slime hostile = new Slime();
        map.addActor(hostile, map.at(5,4));

        int healthBefore = hostile.getStatistic(ActorStatistics.HEALTH);

        RainbowStormEffect storm = new RainbowStormEffect(map, map.at(4,4));

        storm.tick();

        assertEquals(healthBefore - 5, hostile.getStatistic(ActorStatistics.HEALTH),
                "Rainbow Storm should deal 5 damage");

        Action action = hostile.playTurn(new ActionList(), null, map, new Display());

        assertEquals(DoNothingAction.class, action.getClass(),
                "The stunned hostile should be unable to act");
    }

    /**
     * Edge case where a hostile outside the 5x5 storm area
     * should not take any damage.
     *
     * @throws Exception if the actor cannot be placed
     */
    @Test
    void rainbowStormDoesNotDamageOutsideArea() throws Exception {
        Slime hostile = new Slime();

        map.addActor(hostile, map.at(7, 4));

        int healthBefore = hostile.getStatistic(ActorStatistics.HEALTH);

        RainbowStormEffect storm = new RainbowStormEffect(map, map.at(4,4));

        storm.tick();

        assertEquals(healthBefore, hostile.getStatistic(ActorStatistics.HEALTH),
                "Hostiles outside the storm area must not take damage");
    }
}
