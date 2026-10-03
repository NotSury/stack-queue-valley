package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.Weapon;
import game.actions.AttackAction;
import game.coatings.Coating;
import game.coatings.CoatingSourceScanner;
import game.interfaces.Coatable;
import game.interfaces.Hostile;

import java.util.Random;

/**
 * A sword: 20 damage with an 85% chance to hit, whatever the target.
 * Only hostile creatures can be attacked with it. It can hold one coating,
 * which takes effect on every attack, including misses.
 *
 * @author <Lee Le Si> <34599223>
 * @version 1.0.0
 */

public class Sword extends Item implements Weapon, Coatable {

    /**
     * Damage of one sword hit.
     */
    private static final int DAMAGE = 20;
    /**
     * Chance for a sword attack to hit, as a percentage.
     */
    private static final int HIT_RATE = 85;
    /**
     * Upper bound for percentage rolls (rolls are 0 to 99).
     */
    private static final int PERCENT = 100;
    /**
     * Used for the hit roll.
     */
    private final Random random = new Random();
    /**
     * The current coating, or null until the sword is coated.
     */
    private Coating coating;

    /**
     * Constructor for Sword.
     */
    public Sword(){
        super("Sword",'t');
    }

    /**
     * Attacks a target with the sword.
     *
     * <p>Rolls 0-99: values below 85 hit for 20 damage, the rest miss. Then, if
     * the sword is coated, the coating's effect is applied - on hits AND misses,
     * as the requirement says.</p>
     *
     * @param attacker the actor using the sword
     * @param target   the actor being attacked
     * @param map      the map the attack happens on
     * @return a description of the attack and any coating effect
     */
    @Override
    public String attack(Actor attacker, Actor target, GameMap map){
        String result;
        if (random.nextInt(PERCENT) < HIT_RATE){
            target.hurt(DAMAGE);
            result = attacker + " slashes " + target + " for " + DAMAGE + " damage!";
        }else{
            result = attacker + " misses " + target + ".";
        }
        // coating applies on every attack, so this is outside the hit/miss branches
        if (coating!=null){
            result += System.lineSeparator() + coating.applyEffect(attacker,target,map);
        }
        return result;
    }

    /**
     * Replaces the current coating with a new one.
     *
     * @param coating the new coating
     */
    @Override
    public void setCoating(Coating coating) {
        this.coating = coating;
    }

    /**
     * Offers an attack against an adjacent actor, but only if it is hostile.
     * This is how "hostile creatures are the only valid targets" is enforced.
     *
     * @param otherActor the actor next to the sword's owner
     * @param location   where that actor is
     * @return an AttackAction if the actor is hostile, otherwise an empty list
     */
    @Override
    public ActionList allowableActions(Actor otherActor, Location location){
        ActionList actions = new ActionList();
        if (otherActor.asCapability(Hostile.class).isPresent()){
            actions.add(new AttackAction(otherActor, location.toString(),this));
        }
        return actions;
    }

    /**
     * Offers one coat action for every coating source the owner can reach
     * (carried, or on an adjacent tile).
     *
     * @param owner the actor carrying the sword
     * @param map   the map the owner is on
     * @return the available coat actions, possibly empty
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map){
        return CoatingSourceScanner.coatActions(this, owner, map);
    }

    /**
     * Name shown in the inventory and menus, including the current coating.
     *
     * @return "Sword", or e.g. "Sword (Fire Coating)" when coated
     */
    @Override
    public String toString(){
        return coating == null ? "Sword" : "Sword (" + coating + "-coated)";
    }
}
