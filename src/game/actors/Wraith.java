package game.actors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.afflictions.Affliction;
import game.afflictions.CurseApplier;
import game.grounds.HauntedGround;
import game.grounds.TemporaryGround;
import game.utils.NearbyActors;
import game.weapons.CreatureWeapon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A raised Cave Ghost (REQ3). Every 3 turns it wails: every curse on its master
 * is stripped off and pushed onto a random hostile within 3 tiles of the
 * Wraith. A curse with no hostile able to take it fizzles out. Every tile the
 * Wraith walks off becomes Haunted Ground.
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class Wraith extends Undead {

    /** Starting health. */
    private static final int MAX_HP = 80;

    /** Damage of one touch. */
    private static final int TOUCH_DAMAGE = 20;

    /** Chance for a touch to hit, as a percentage. */
    private static final int HIT_RATE = 75;

    /** How many turns it lasts before crumbling. */
    private static final int LIFESPAN = 15;

    /** The Wraith wails every 3 turns. */
    private static final int WAIL_INTERVAL = 3;

    /** How far the wail reaches. */
    private static final int WAIL_RADIUS = 3;

    /** Picks which hostile receives each curse. */
    private final Random random;

    /** Turns since the Wraith was raised. */
    private int turnsElapsed;

    /**
     * Creates a Wraith serving the given master.
     *
     * @param master the actor that raised it
     */
    public Wraith(Actor master) {
        this(master, new Random());
    }

    /**
     * Creates a Wraith with a given random generator, so tests can control which
     * hostile receives each curse.
     *
     * @param master the actor that raised it
     * @param random the random generator used to pick hostiles
     */
    public Wraith(Actor master, Random random) {
        super("Wraith", 'V', MAX_HP,
                new CreatureWeapon(TOUCH_DAMAGE, "chills", HIT_RATE, "spectral touch"), master, LIFESPAN);
        this.random = random;
    }

    /**
     * Every 3 turns, moves every curse off the master onto random nearby hostiles.
     *
     * @param here the Wraith's current location
     * @return a description of the wail, or an empty string if it did not wail
     */
    @Override
    protected String useAbility(Location here) {
        turnsElapsed++;
        if (turnsElapsed % WAIL_INTERVAL != 0) {
            return "";
        }
        List<Affliction> curses = getMaster().statusesOf(Affliction.class);
        if (curses.isEmpty()) {
            return "";
        }

        int moved = 0;
        for (Affliction curse : curses) {
            getMaster().removeStatus(curse);
            if (pushOntoHostile(here, curse)) {
                moved++;
            }
        }
        return this + " wails, lifting " + curses.size() + " curse(s) from " + getMaster()
                + ": " + moved + " pushed onto hostiles, " + (curses.size() - moved) + " fizzled out.";
    }

    /**
     * Gives a fresh copy of a curse to a random hostile within range that can
     * take it.
     *
     * @param here  the Wraith's current location
     * @param curse the curse lifted from the master
     * @return true if a hostile received the curse
     */
    private boolean pushOntoHostile(Location here, Affliction curse) {
        List<Actor> hostiles = new ArrayList<>(NearbyActors.within(here, WAIL_RADIUS, NearbyActors.HOSTILE));
        Collections.shuffle(hostiles, random);
        for (Actor hostile : hostiles) {
            if (CurseApplier.apply(hostile, curse.copy())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Haunts the tile it just left.
     *
     * @param left the location the Wraith left
     * @return a description of the haunting, or an empty string if the tile was not haunted
     */
    @Override
    protected String leaveTrail(Location left) {
        return TemporaryGround.placeOn(left, HauntedGround::new) ? this + " leaves the ground haunted." : "";
    }
}