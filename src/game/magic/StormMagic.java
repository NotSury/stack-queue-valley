package game.magic;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.companions.MagicalCompanion;

/**
 * Provides Rainbow Storm magic to PrismFox.
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.0.0
 */
public class StormMagic implements FarmMagic {

    /**
     * Returns the name of this magical ability.
     *
     * @return the ability name
     */
    @Override
    public String getName() {
        return "Rainbow Storm";
    }

    /**
     * Attempts to activate Rainbow Storm.
     *
     * @param farmer the Farmer activating the magic
     * @param companion the magical companion
     * @param map the game map
     * @return true if the Rainbow Storm is activated successfully
     */
    @Override
    public boolean cast(Actor farmer, MagicalCompanion companion, GameMap map) {
        return false;
    }
}
