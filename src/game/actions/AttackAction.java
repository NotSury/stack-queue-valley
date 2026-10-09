package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.weapons.Weapon;
import game.interfaces.Hostile;
/**
 * One actor attacks another with a weapon.
 *
 * <p>Used in both directions: the farmer attacking a hostile creature with the
 * sword, and a hostile creature attacking the farmer with its intrinsic weapon.</p>
 *
 * @author <Lee Le Si> <34599223>
 * @version 1.0.0
 */
public class AttackAction extends Action {
    /**
     * The actor being attacked.
     */
    private final Actor target;
    /**
     * Where the target is, used in the menu text (e.g. "North").
     */
    private final String direction;

    /**
     * The weapon used for the attack.
     */
    private final Weapon weapon;
    /**
     * Constructor for AttackAction.
     *
     * @param target    the actor being attacked
     * @param direction where the target is, for the menu (e.g. "North")
     * @param weapon    the weapon used
     */
    public AttackAction(Actor target, String direction, Weapon weapon){
        this.target = target;
        this.direction = direction;
        this.weapon = weapon;
    }


    /**
     * Performs the attack.
     *
     * <p>Steps: (1) the weapon decides hit or miss and applies its damage and any
     * coating; (2) if the target is hostile, it reacts to the attack; (3) if the
     * target has no health left, it is removed from the map.</p>
     *
     * @param actor the actor performing the attack
     * @param map   the map the attack happens on
     * @return a description of everything that happened
     */
    @Override
    public String execute(Actor actor, GameMap map){
        // 1. the weapon handles hit chance, damage and coating effects
        StringBuilder result = new StringBuilder(weapon.attack(actor, target, map));

        // 2. hostile creatures react to every attack, hit or miss (bat drops, slime splits).
        //    asCapability returns empty for non-hostile targets such as the farmer.
        target.asCapability(Hostile.class).ifPresent(hostile -> {
            String reaction = hostile.respondToAttack(actor, map);
            if (!reaction.isEmpty()){
                result.append(System.lineSeparator()).append(reaction);
            }
        });

        // 3. the target dies if the attack (or its coating) left it with no health
        if (map.contains(target) && !target.isConscious()){
            result.append(System.lineSeparator()).append(target.unconscious(actor, map));
        }
        return result.toString();
    }

    /**
     * Text shown for this action in the menu.
     *
     * @param actor the actor who would perform the attack
     * @return e.g. "Farmer Bob attacks Bat at North with Sword"
     */
    @Override
    public String menuDescription(Actor actor){
        return actor + " attacks " + target + " at " + direction + " with " + weapon;
    }
}
