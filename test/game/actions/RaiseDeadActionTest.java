package game.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actors.Bat;
import game.actors.CaveGhost;
import game.actors.Farmer;
import game.actors.HostileCreature;
import game.actors.SkeletonBat;
import game.actors.Slime;
import game.actors.Wraith;
import game.actors.ZombieSlime;
import game.afflictions.Affliction;
import game.capabilities.NecromancyCapability;
import game.grounds.Dirt;
import game.grounds.Remains;
import game.support.TestMaps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests killing a cursed creature, the remains it leaves, and raising those
 * remains as an undead minion (REQ3).
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
class RaiseDeadActionTest {

    private GameMap map;
    private Farmer farmer;

    /**
     * Places a Farmer on a fresh map before each test.
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
     * Kills a creature next to the Farmer and returns the location of its remains.
     *
     * @param creature the creature to kill
     * @return the location where it died
     * @throws Exception if the creature cannot be placed
     */
    private Location killNextToFarmer(HostileCreature creature) throws Exception {
        Location here = map.at(5, 4);
        map.addActor(creature, here);
        creature.hurt(Integer.MAX_VALUE / 2);
        creature.unconscious(farmer, map);
        return here;
    }

    /**
     * Returns the raise action the remains offer to an actor, if any.
     *
     * @param actor   the actor next to the remains
     * @param remains the location of the remains
     * @return the raise action, or null if none is offered
     */
    private Action raiseActionFor(Actor actor, Location remains) {
        for (Action action : remains.getGround().allowableActions(actor, remains, "East")) {
            return action;
        }
        return null;
    }

    /**
     * Normal case: killing a cursed creature curses the killer and leaves
     * remains, and raising them spends stamina and creates the right minion.
     *
     * @throws Exception if an actor cannot be placed
     */
    @Test
    void eachCursedCreatureRisesAsItsOwnUndead() throws Exception {
        HostileCreature[] creatures = {new Bat(), new Slime(), new CaveGhost()};
        Class<?>[] undeadForms = {SkeletonBat.class, ZombieSlime.class, Wraith.class};

        for (int i = 0; i < creatures.length; i++) {
            setUp();
            Location remains = killNextToFarmer(creatures[i]);
            assertEquals(1, farmer.statusesOf(Affliction.class).size(), "killer should inherit one curse");
            assertInstanceOf(Remains.class, remains.getGround(), "creature should leave remains");

            int staminaBefore = farmer.getStatistic(ActorStatistics.STAMINA);
            raiseActionFor(farmer, remains).execute(farmer, map);

            assertInstanceOf(undeadForms[i], remains.getActor(), "wrong undead form");
            assertFalse(remains.getGround() instanceof Remains, "remains should be cleared");
            assertEquals(staminaBefore - 5, farmer.getStatistic(ActorStatistics.STAMINA), "raising costs 5 stamina");
            assertTrue(farmer.hasAbility(NecromancyCapability.HAS_MINION), "Farmer should now have a minion");
        }
    }

    /**
     * Invalid case: only the Farmer can raise remains, and only one minion at a time.
     *
     * @throws Exception if an actor cannot be placed
     */
    @Test
    void onlyOneMinionAndOnlyTheFarmer() throws Exception {
        Location first = killNextToFarmer(new Bat());
        assertNull(raiseActionFor(new Slime(), first), "a hostile should not be offered the raise");
        raiseActionFor(farmer, first).execute(farmer, map);

        Location second = map.at(3, 4);
        Slime slime = new Slime();
        map.addActor(slime, second);
        slime.hurt(Integer.MAX_VALUE / 2);
        slime.unconscious(farmer, map);
        String result = raiseActionFor(farmer, second).execute(farmer, map);

        assertTrue(result.contains("already commands"), "a second minion should be refused");
        assertInstanceOf(Remains.class, second.getGround(), "refused remains should stay");
    }

    /**
     * Edge case: exactly 5 stamina is enough, 4 is not.
     *
     * @throws Exception if an actor cannot be placed
     */
    @Test
    void staminaBoundary() throws Exception {
        Location remains = killNextToFarmer(new Bat());

        farmer.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.UPDATE, 4);
        String tired = raiseActionFor(farmer, remains).execute(farmer, map);
        assertTrue(tired.contains("too tired"), "4 stamina should not be enough");
        assertEquals(4, farmer.getStatistic(ActorStatistics.STAMINA), "failed raise should cost nothing");

        farmer.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.UPDATE, 5);
        raiseActionFor(farmer, remains).execute(farmer, map);
        assertEquals(0, farmer.getStatistic(ActorStatistics.STAMINA), "5 stamina should be enough");
        assertInstanceOf(SkeletonBat.class, remains.getActor(), "minion should be raised");
    }

    /**
     * Edge case: when a minion crumbles, its master can raise again and the
     * ground under the old remains is restored.
     *
     * @throws Exception if an actor cannot be placed
     */
    @Test
    void crumblingMinionFreesMaster() throws Exception {
        Location remains = killNextToFarmer(new Bat());
        raiseActionFor(farmer, remains).execute(farmer, map);
        Actor minion = remains.getActor();

        minion.hurt(Integer.MAX_VALUE / 2);
        minion.unconscious(map);

        assertFalse(map.contains(minion), "minion should be gone");
        assertFalse(farmer.hasAbility(NecromancyCapability.HAS_MINION), "Farmer should be free to raise again");
        assertInstanceOf(Dirt.class, remains.getGround(), "original ground should be restored");
    }
}