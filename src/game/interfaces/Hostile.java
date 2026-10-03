package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * A creature that the farmer is allowed to fight.
 *
 * <p>Only actors that provide this capability can be attacked with a weapon
 * such as the sword. Every time one of them is attacked (hit or miss), it gets
 * a chance to react, e.g. a bat drops a strawberry or a slime splits.</p>
 *
 * <p>Callers find this capability with {@code actor.asCapability(Hostile.class)},
 * so no {@code instanceof} checks or casts are needed.</p>
 *
 * @author <Lee Le Si> <34599223>
 * @version 1.0.0
 */
public interface Hostile {
    /**
     * Reacts to being attacked. Called after every attack, including misses.
     *
     * @param attacker the actor who attacked this creature
     * @param map      the map the creature is on
     * @return a description of the reaction, or an empty string if nothing happened
     */
    String respondToAttack(Actor attacker, GameMap map);
}
