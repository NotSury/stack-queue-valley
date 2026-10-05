package game.actors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Strawberry;
import game.weapons.CreatureWeapon;

import java.util.List;
import java.util.Random;
/**
 * A bat: 50 HP, bites for 35 damage with a 90% chance to hit.
 * Every time it is attacked it drops a fresh (never rotten) strawberry nearby.
 *
 * @author <Lee Le Si> <34599223>
 * @version 1.0.0
 */
public class Bat extends HostileCreature{
    /**
     * Starting health of a bat.
     */
    private static final int MAX_HP = 50;
    /**
     * Damage of one bite.
     */
    private static final int BITE_DAMAGE = 35;
    /**
     * Damage of one bite.
     */
    private static final int HIT_RATE = 90;
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
}
