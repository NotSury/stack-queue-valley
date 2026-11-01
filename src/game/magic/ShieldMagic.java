package game.magic;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.companions.MagicalCompanion;
import game.effects.NatureSanctuaryEffect;

/**
 * Provides Nature's Sanctuary magic to SanctuaryDeer.
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.0.0
 */
public class ShieldMagic implements FarmMagic {

    /**
     * Returns the name of this magical ability.
     *
     * @return the ability name
     */
    @Override
    public String getName() {
        return NatureSanctuaryEffect.NAME;
    }

    /**
     * Activates Nature's Sanctuary if it is not already active.
     *
     * @param farmer the Farmer activating the magic
     * @param companion the magical companion
     * @param map the game map
     * @return true if the sanctuary activates successfully
     */
    @Override
    public boolean cast(Actor farmer, MagicalCompanion companion, GameMap map) {
        if (companion.isEffectActive(NatureSanctuaryEffect.NAME)) {
            return false;
        }

        NatureSanctuaryEffect effect = new NatureSanctuaryEffect(map,
                map.locationOf(companion), farmer);

        companion.addMagicEffect(effect);
        return true;
    }
}
