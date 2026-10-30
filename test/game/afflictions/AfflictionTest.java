package game.afflictions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.Bat;
import game.actors.Farmer;
import game.actors.Slime;
import game.actors.SkeletonBat;
import game.grounds.Blight;
import game.grounds.Dirt;
import game.grounds.HauntedGround;
import game.grounds.MireGround;
import game.items.Strawberry;
import game.support.TestMaps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the three REQ3 curses (Contagion, Mire and Soul Tether) and the rules
 * for applying them. Curses have no randomness, so every test is deterministic.
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
class AfflictionTest {

    private GameMap map;
    private Farmer farmer;

    /**
     * Places a Farmer in the middle of a fresh map before each test.
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
     * Normal case: Contagion jumps to an adjacent actor but not a distant one,
     * and turns the host's tile into Blight.
     *
     * @throws Exception if an actor cannot be placed
     */
    @Test
    void contagionSpreadsToNeighboursAndBlightsTile() throws Exception {
        Bat neighbour = new Bat();
        Slime farAway = new Slime();
        map.addActor(neighbour, map.at(5, 4));
        map.addActor(farAway, map.at(8, 8));
        CurseApplier.apply(farmer, new Contagion());

        map.tick();

        assertTrue(CurseApplier.hasCurse(neighbour, CurseType.CONTAGION), "adjacent actor should catch Contagion");
        assertFalse(CurseApplier.hasCurse(farAway, CurseType.CONTAGION), "distant actor should stay healthy");
        assertInstanceOf(Blight.class, map.at(4, 4).getGround(), "host tile should become Blight");
    }

    /**
     * Edge case: an outbreak always ends. After enough turns, nobody carries
     * Contagion and the ground is back to Dirt.
     *
     * @throws Exception if an actor cannot be placed
     */
    @Test
    void contagionOutbreakAlwaysDiesOut() throws Exception {
        Bat neighbour = new Bat();
        map.addActor(neighbour, map.at(5, 4));
        CurseApplier.apply(farmer, new Contagion());

        for (int turn = 0; turn < 40; turn++) {
            map.tick();
        }

        assertFalse(farmer.hasStatus(Contagion.class), "Farmer should recover");
        assertFalse(neighbour.hasStatus(Contagion.class), "neighbour should recover");
        assertInstanceOf(Dirt.class, map.at(4, 4).getGround(), "Blight should wear off");
    }

    /**
     * Edge case: Contagion at the maximum generation no longer spreads.
     *
     * @throws Exception if an actor cannot be placed
     */
    @Test
    void weakestContagionDoesNotSpread() throws Exception {
        Bat neighbour = new Bat();
        map.addActor(neighbour, map.at(5, 4));
        Contagion weakest = new Contagion(2);
        CurseApplier.apply(farmer, weakest);

        map.tick();

        assertFalse(weakest.canSpread(), "generation 2 should be too weak to spread");
        assertFalse(CurseApplier.hasCurse(neighbour, CurseType.CONTAGION), "neighbour should stay healthy");
    }

    /**
     * Invalid cases: undead cannot be cursed, and an actor cannot carry two
     * curses of the same type.
     */
    @Test
    void undeadAndDuplicateCursesAreRejected() {
        SkeletonBat undead = new SkeletonBat(farmer);

        assertFalse(CurseApplier.apply(undead, new Mire()), "undead should be immune");
        assertTrue(CurseApplier.apply(farmer, new Mire()), "first Mire should apply");
        assertFalse(CurseApplier.apply(farmer, new Mire()), "second Mire should be rejected");
        assertEquals(1, farmer.statusesOf(Mire.class).size(), "Farmer should carry exactly one Mire");
    }

    /**
     * Normal case: Mire waits two turns, then floods empty neighbours, skips
     * occupied ones and swallows items. The swamp cannot be entered.
     *
     * @throws Exception if an actor cannot be placed
     */
    @Test
    void mireFloodsEmptyNeighboursEveryTwoTurns() throws Exception {
        Slime blocker = new Slime();
        map.addActor(blocker, map.at(5, 4));
        map.at(3, 3).addItem(new Strawberry(false));
        CurseApplier.apply(farmer, new Mire());

        map.tick();
        assertFalse(map.at(3, 3).getGround() instanceof MireGround, "no swamp after one turn");

        map.tick();
        assertInstanceOf(MireGround.class, map.at(3, 3).getGround(), "empty neighbour should flood");
        assertFalse(map.at(5, 4).getGround() instanceof MireGround, "occupied neighbour should be skipped");
        assertTrue(map.at(3, 3).getItems().isEmpty(), "items should sink");
        assertFalse(map.at(3, 3).canActorEnter(farmer), "swamp should be impassable");
    }

    /**
     * Normal case: Soul Tether copies the host's other curses onto the closest
     * actor, but never copies itself.
     *
     * @throws Exception if an actor cannot be placed
     */
    @Test
    void soulTetherCopiesOtherCursesToClosestActor() throws Exception {
        Slime partner = new Slime();
        Bat further = new Bat();
        map.addActor(partner, map.at(6, 4));
        map.addActor(further, map.at(7, 7));
        CurseApplier.apply(farmer, new SoulTether());
        CurseApplier.apply(farmer, new Mire());

        for (int turn = 0; turn < 3; turn++) {
            map.tick();
        }

        assertTrue(CurseApplier.hasCurse(partner, CurseType.MIRE), "closest actor should receive Mire");
        assertFalse(CurseApplier.hasCurse(partner, CurseType.SOUL_TETHER), "Soul Tether should not copy itself");
        assertFalse(CurseApplier.hasCurse(further, CurseType.MIRE), "further actor should be untouched");
    }

    /**
     * Edge case: with no other curse to share, Soul Tether haunts the closest
     * actor's tile instead.
     *
     * @throws Exception if an actor cannot be placed
     */
    @Test
    void soulTetherHauntsPartnerTileWhenNothingToShare() throws Exception {
        Slime partner = new Slime();
        map.addActor(partner, map.at(6, 4));
        CurseApplier.apply(farmer, new SoulTether());

        for (int turn = 0; turn < 3; turn++) {
            map.tick();
        }

        assertInstanceOf(HauntedGround.class, map.at(6, 4).getGround(), "partner's tile should be haunted");
    }
}