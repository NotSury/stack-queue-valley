package game.actors;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.weapons.CreatureWeapon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A slime: 150 HP, slams for 15 damage with a 50% chance to hit.
 * Each time it is attacked there is a 50% chance it splits into a new slime nearby.
 *
 * @author <Lee Le Si> <34599223>
 * @version 1.0.0
 */
public class Slime extends HostileCreature{
    /**
     * Starting health of a slime.
     */
    private static final int MAX_HP = 150;

    /**
     * Damage of one slam.
     */
    private static final int SLAM_DAMAGE = 15;

    /**
     * Chance for a slam to hit, as a percentage.
     */
    private static final int HIT_RATE = 50;

    /**
     * Chance to split when attacked, as a percentage.
     */
    private static final int SPLIT_CHANCE = 50;

    /**
     * Upper bound for percentage rolls (rolls are 0 to 99).
     */
    private static final int PERCENT = 100;

    /**
     * Used for the split roll and to pick a free tile.
     */
    private final Random random = new Random();

    /**
     * Constructor for Slime.
     */
    public Slime(){
        super("Slime",'S',MAX_HP,new CreatureWeapon(SLAM_DAMAGE,"slams", HIT_RATE,"body"));

    }

    /**
     * Has a 50% chance to spawn a new slime on a random free adjacent tile.
     *
     * <p>If every adjacent tile is taken (or cannot be entered), no slime is spawned.</p>
     *
     * @param attacker the actor who attacked the slime
     * @param map      the map the slime is on
     * @return a description of the split, or an empty string if it did not split
     */
    @Override
    public String respondToAttack(Actor attacker, GameMap map){
        // roll 0-99: values below 50 (half of them) mean a split
        if (random.nextInt(PERCENT) >= SPLIT_CHANCE){
            return "";
        }

        // collect adjacent tiles the new slime is allowed to stand on
        Slime newSlime = new Slime();
        List<Location> freeSpots = new ArrayList<>();
        for (Exit exit : map.locationOf(this).getExits()){
            if (exit.getDestination().canActorEnter(newSlime)){
                freeSpots.add(exit.getDestination());
            }
        }
        if (freeSpots.isEmpty()){
            return "";
        }

        try{
            freeSpots.get(random.nextInt(freeSpots.size())).addActor(newSlime);
        }catch (GameEngineException e){
            return ""; // spot was taken; no split this time
        }
        return this + " splits into another slime";
    }
}
