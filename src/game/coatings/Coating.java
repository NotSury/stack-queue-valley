package game.coatings;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * An extra effect applied by a coated weapon on every attack, including misses.
 *
 * <p>New coatings are added by implementing this interface; the weapon and the
 * attack logic do not change (Open-Closed Principle).</p>
 *
 * @author <Lee Le Si> <34599223>
 * @version 1.0.0
 */
public interface Coating {
    /**
     * Applies this coating's effect to the target of an attack.
     *
     * @param attacker the actor who attacked
     * @param target   the actor who was attacked
     * @param map      the map the attack happened on
     * @return a description of what the coating did
     */
    String applyEffect(Actor attacker, Actor target, GameMap map);
}
