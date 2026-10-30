package game.actors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.MireGround;
import game.grounds.TemporaryGround;
import game.weapons.CreatureWeapon;

/**
 * A raised Slime (REQ3). Every tile it walks off turns into Mire, building a
 * wall behind it that hostiles cannot cross. When it crumbles or is destroyed,
 * its body becomes a Mire pool.
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class ZombieSlime extends Undead {

    /** Starting health. */
    private static final int MAX_HP = 120;

    /** Damage of one slam. */
    private static final int SLAM_DAMAGE = 15;

    /** Chance for a slam to hit, as a percentage. */
    private static final int HIT_RATE = 60;

    /** How many turns it lasts before crumbling. */
    private static final int LIFESPAN = 15;

    /**
     * Creates a Zombie Slime serving the given master.
     *
     * @param master the actor that raised it
     */
    public ZombieSlime(Actor master) {
        super("Zombie Slime", 'Z', MAX_HP,
                new CreatureWeapon(SLAM_DAMAGE, "slams", HIT_RATE, "rotting body"), master, LIFESPAN);
    }

    /**
     * The Zombie Slime has no ability at the start of its turn; its effect is its trail.
     *
     * @param here the Zombie Slime's current location
     * @return an empty string
     */
    @Override
    protected String useAbility(Location here) {
        return "";
    }

    /**
     * Turns the tile it just left into Mire, unless someone is standing there.
     *
     * @param left the location the Zombie Slime left
     * @return a description of the trail, or an empty string if no Mire formed
     */
    @Override
    protected String leaveTrail(Location left) {
        if (left.containsAnActor() || !TemporaryGround.placeOn(left, MireGround::new)) {
            return "";
        }
        return this + " leaves a trail of Mire.";
    }

    /**
     * Melts into a Mire pool around the tile where it fell apart.
     *
     * @param here the location where it fell apart
     * @return a description of the pool forming
     */
    @Override
    protected String onCrumble(Location here) {
        int flooded = TemporaryGround.placeAround(here, MireGround::new, true);
        return this + " melts into a Mire pool, flooding " + flooded + " tile(s).";
    }
}