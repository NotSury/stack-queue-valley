package game.companions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.magic.FarmMagic;

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
     * Activates the deer's unique harvesting ability.
     *
     */
    @Override
    public boolean castOwnAbility(Actor farmer, GameMap map) {
        return false;
    }
}
