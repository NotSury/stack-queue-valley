package game.actors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.AttackAction;
import game.afflictions.Affliction;
import game.afflictions.Contagion;
import game.afflictions.CurseApplier;
import game.afflictions.CurseType;
import game.afflictions.Mire;
import game.afflictions.SoulTether;
import game.capabilities.NecromancyCapability;
import game.grounds.HauntedGround;
import game.grounds.MireGround;
import game.support.TestMaps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

/**
 * Tests the three undead minions (REQ3). The Wraith is given a seeded Random,
 * so every test is deterministic.
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
class UndeadTest {

    private final Display display = new Display();
    private GameMap map;
    private Farmer farmer;

    /**
     * Places a Farmer in a corner of a fresh map before each test.
     *
     * @throws Exception if the map cannot be created
     */
    @BeforeEach
    void setUp() throws Exception {
        map = TestMaps.dirtMap();
        farmer = new Farmer("Test");
        map.addActor(farmer, map.at(0, 0));
    }

    /**
     * Plays one turn of a minion.
     *
     * @param minion the minion
     * @return the action it chose
     */
    private Action playTurn(Undead minion) {
        return minion.playTurn(new ActionList(), new DoNothingAction(), map, display);
    }

    /**
     * Normal case: the Skeleton Bat infects an adjacent hostile and attacks it.
     *
     * @throws Exception if an actor cannot be placed
     */
    @Test
    void skeletonBatInfectsAndAttacksAdjacentHostile() throws Exception {
        SkeletonBat skeleton = new SkeletonBat(farmer);
        Slime hostile = new Slime();
        map.addActor(skeleton, map.at(4, 4));
        map.addActor(hostile, map.at(5, 4));

        Action action = playTurn(skeleton);

        assertTrue(CurseApplier.hasCurse(hostile, CurseType.CONTAGION), "hostile should catch Contagion");
        assertInstanceOf(AttackAction.class, action, "minion should attack the hostile");
    }

    /**
     * Normal case: the Zombie Slime leaves Mire behind it and melts into a Mire
     * pool when destroyed.
     *
     * @throws Exception if an actor cannot be placed
     */
    @Test
    void zombieSlimeLeavesMireTrailAndPool() throws Exception {
        ZombieSlime zombie = new ZombieSlime(farmer);
        map.addActor(zombie, map.at(4, 4));

        playTurn(zombie);
        map.moveActor(zombie, map.at(5, 4));
        playTurn(zombie);
        assertInstanceOf(MireGround.class, map.at(4, 4).getGround(), "the tile it left should become Mire");

        zombie.hurt(Integer.MAX_VALUE / 2);
        zombie.unconscious(new Slime(), map);
        assertInstanceOf(MireGround.class, map.at(6, 4).getGround(), "it should melt into a Mire pool");
    }

    /**
     * Normal case: every third turn the Wraith lifts every curse off the Farmer
     * and pushes them onto a hostile in range. It also haunts the tile it leaves.
     *
     * @throws Exception if an actor cannot be placed
     */
    @Test
    void wraithMovesFarmersCursesOntoHostile() throws Exception {
        Wraith wraith = new Wraith(farmer, new Random(1));
        Slime hostile = new Slime();
        map.addActor(wraith, map.at(4, 4));
        map.addActor(hostile, map.at(6, 6));
        CurseApplier.apply(farmer, new Mire());
        CurseApplier.apply(farmer, new SoulTether());
        CurseApplier.apply(farmer, new Contagion());

        playTurn(wraith);
        playTurn(wraith);
        assertEquals(3, farmer.statusesOf(Affliction.class).size(), "no wail before the third turn");

        map.moveActor(wraith, map.at(4, 5));
        playTurn(wraith);
        assertTrue(farmer.statusesOf(Affliction.class).isEmpty(), "every curse should be lifted");
        assertEquals(3, hostile.statusesOf(Affliction.class).size(), "the hostile should receive all three");
        assertInstanceOf(HauntedGround.class, map.at(4, 4).getGround(), "the tile it left should be haunted");
    }

    /**
     * Edge case: with no hostile in range, the Wraith's wail still lifts the
     * curse, which fizzles out.
     *
     * @throws Exception if an actor cannot be placed
     */
    @Test
    void wraithCurseFizzlesWithNoHostileInRange() throws Exception {
        Wraith wraith = new Wraith(farmer, new Random(1));
        Slime outOfRange = new Slime();
        map.addActor(wraith, map.at(4, 4));
        map.addActor(outOfRange, map.at(8, 8));
        CurseApplier.apply(farmer, new Mire());

        for (int turn = 0; turn < 3; turn++) {
            playTurn(wraith);
        }

        assertTrue(farmer.statusesOf(Affliction.class).isEmpty(), "the curse should be lifted");
        assertTrue(outOfRange.statusesOf(Affliction.class).isEmpty(), "a hostile out of range gets nothing");
    }

    /**
     * Edge case: a minion crumbles after exactly 15 turns and frees its master.
     *
     * @throws Exception if an actor cannot be placed
     */
    @Test
    void minionCrumblesAfterLifespan() throws Exception {
        farmer.enableAbility(NecromancyCapability.HAS_MINION);
        SkeletonBat skeleton = new SkeletonBat(farmer);
        map.addActor(skeleton, map.at(8, 8));

        for (int turn = 0; turn < 14; turn++) {
            playTurn(skeleton);
        }
        assertTrue(map.contains(skeleton), "still standing after 14 turns");

        playTurn(skeleton);
        assertFalse(map.contains(skeleton), "crumbled on turn 15");
        assertFalse(farmer.hasAbility(NecromancyCapability.HAS_MINION), "master should be freed");
    }

    /**
     * Invalid case: a curse released onto an undead killer fizzles out.
     *
     * @throws Exception if an actor cannot be placed
     */
    @Test
    void curseFizzlesOnUndeadKiller() throws Exception {
        SkeletonBat skeleton = new SkeletonBat(farmer);
        Bat bat = new Bat();
        map.addActor(skeleton, map.at(4, 4));
        map.addActor(bat, map.at(5, 4));

        bat.hurt(Integer.MAX_VALUE / 2);
        String result = bat.unconscious(skeleton, map);

        assertTrue(result.contains("fizzles out"), "the curse should fizzle");
        assertTrue(skeleton.statusesOf(Affliction.class).isEmpty(), "the undead should stay clean");
    }
}