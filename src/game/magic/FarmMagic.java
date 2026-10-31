package game.magic;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.companions.MagicalCompanion;

/**
 * Represents a magical ability provided by a magical companion.
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.0.0
 */
public interface FarmMagic
{
    /**
     * Returns the name of the magical ability.
     *
     * @return the ability name
     */
    String getName();

    /**
     * This activates the magical ability.
     *
     * @param farmer the Farmer activating the magic
     * @param companion the magical companion
     * @param map the game map
     * @return true unless the magic succeeds and a charge should be consumed
     */
    boolean cast(Actor farmer, MagicalCompanion companion, GameMap map);
}
