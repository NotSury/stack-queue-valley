package game.actors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.afflictions.Contagion;
import game.afflictions.CurseApplier;
import game.utils.NearbyActors;
import game.weapons.CreatureWeapon;

/**
 * A raised Bat (REQ3). It carries a plague aura: every turn, each hostile
 * standing next to it catches Contagion, so the curse spreads through groups
 * of enemies.
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class SkeletonBat extends Undead {

    /** Starting health. */
    private static final int MAX_HP = 40;

    /** Damage of one bite. */
    private static final int BITE_DAMAGE = 20;

    /** Chance for a bite to hit, as a percentage. */
    private static final int HIT_RATE = 85;

    /** How many turns it lasts before crumbling. */
    private static final int LIFESPAN = 15;

    /** How far the plague aura reaches. */
    private static final int AURA_RADIUS = 1;

    /**
     * Creates a Skeleton Bat serving the given master.
     *
     * @param master the actor that raised it
     */
    public SkeletonBat(Actor master) {
        super("Skeleton Bat", 'B', MAX_HP,
                new CreatureWeapon(BITE_DAMAGE, "bites", HIT_RATE, "bone fangs"), master, LIFESPAN);
    }

    /**
     * Plague aura: gives Contagion to every adjacent hostile.
     *
     * @param here the Skeleton Bat's current location
     * @return a description of the infections, or an empty string if nobody was infected
     */
    @Override
    protected String useAbility(Location here) {
        int infected = 0;
        for (Actor hostile : NearbyActors.within(here, AURA_RADIUS, NearbyActors.HOSTILE)) {
            if (CurseApplier.apply(hostile, new Contagion())) {
                infected++;
            }
        }
        return infected == 0 ? "" : this + "'s plague aura infects " + infected + " hostile(s) with Contagion.";
    }
}