package game.actors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.afflictions.Affliction;
import game.afflictions.Contagion;
import game.afflictions.CurseApplier;
import game.grounds.Blight;
import game.grounds.TemporaryGround;
import game.interfaces.Cursed;
import game.items.Strawberry;
import game.utils.NearbyActors;
import game.weapons.CreatureWeapon;

import java.util.List;
import java.util.Random;
/**
 * A bat: 50 HP, bites for 35 damage with a 90% chance to hit.
 * Every time it is attacked it drops a fresh (never rotten) strawberry nearby.
 *
 * <p>The Bat carries Contagion (REQ3). Its killer inherits Contagion, and its
 * bones burst when it dies, giving Contagion to every hostile within 2 tiles
 * and turning the nearby ground into Blight. Its remains can be raised as a
 * Skeleton Bat.</p>
 *
 * @author <Lee Le Si> <34599223>
 * @version 1.3.0
 */
public class Bat extends HostileCreature implements Cursed {
    /**
     * Starting health of a bat.
     */
    private static final int MAX_HP = 50;
    /**
     * Damage of one bite.
     */
    private static final int BITE_DAMAGE = 35;
    /**
     * Chance for a bite to hit, as a percentage.
     */
    private static final int HIT_RATE = 90;
    /**
     * How far the bone burst spreads Contagion when the bat dies.
     */
    private static final int BONE_BURST_RADIUS = 2;
    /**
     * Used to pick a random adjacent tile for the dropped strawberry.
     */
    private final Random random = new Random();

    /**
     * Constructor for Bat.
     */
    public Bat(){
        super("Bat",'b', MAX_HP, new CreatureWeapon(BITE_DAMAGE, "bites", HIT_RATE, "fangs"));
    }

    /**
     * Drops a fresh strawberry on a random adjacent tile.
     *
     * <p>Called on every attack, hit or miss. It also runs when the blow kills
     * the bat, because AttackAction calls this before removing a dead target.</p>
     *
     * @param attacker the actor who attacked the bat
     * @param map      the map the bat is on
     * @return a description of the drop, or an empty string if there is no tile
     */
    @Override
    public String respondToAttack(Actor attacker, GameMap map){
        List<Exit> exits = map.locationOf(this).getExits();
        if(exits.isEmpty()){
            return "";
        }

        // items can share a tile with actors, so any adjacent tile works
        Location dropSpot = exits.get(random.nextInt(exits.size())).getDestination();

        dropSpot.addItem(new Strawberry(false)); // false = not rotten
        return this + " drops a strawberry.";
    }

    /**
     * Creates a fresh Contagion to pass to the bat's killer.
     *
     * @return a new Contagion
     */
    @Override
    public Affliction curse() {
        return new Contagion();
    }

    /**
     * Bone burst: gives Contagion to every hostile within 2 tiles and turns the
     * tiles next to the remains into Blight.
     *
     * @param remains the location where the bat died
     * @return a description of the bone burst
     */
    @Override
    public String onDeath(Location remains) {
        int infected = CurseApplier.applyToAll(
                NearbyActors.within(remains, BONE_BURST_RADIUS, NearbyActors.HOSTILE), Contagion::new);
        TemporaryGround.placeAround(remains, Blight::new, false);
        return this + "'s bones burst, infecting " + infected + " hostile(s) with Contagion and blighting the ground.";
    }

    /**
     * Raises this creature as a Skeleton Bat serving the given master.
     *
     * @param master the actor raising the remains
     * @return a new Skeleton Bat
     */
    @Override
    public Undead rise(Actor master) {
        return new SkeletonBat(master);
    }
}