package game.companions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.magic.FarmMagic;
import game.effects.LivingSoilEffect;

/**
 * A magical deer that help the farm through
 * Living Soil and Nature's Sanctuary.
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.0.0
 */
public class SanctuaryDeer extends MagicalCompanion {

    /**
     * Creates a PrismFox with 20 hit points.
     */
    public SanctuaryDeer()
    {
        super("SanctuaryDeer", 'D', 20);
    }

    /**
     * Returns the deer's magical ability.
     *
     */
    @Override
    protected FarmMagic getFarmMagic()
    {
        return null;
    }

    /**
     * Returns the deer's unique ability name.
     *
     */
    @Override
    public String getOwnAbilityName()
    {
        return "Living Soil";
    }

    /**
     * Activates Living Soil around the SanctuaryDeer.
     *
     * @param farmer the farmer using the ability
     * @param map the game map
     * @return true when Living Soil is activated
     */
    @Override
    public boolean castOwnAbility(Actor farmer, GameMap map) {
        LivingSoilEffect effect = new LivingSoilEffect();

        effect.apply(map, map.locationOf(this));

        return true;
    }
}
