package game.companions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.magic.FarmMagic;
import game.effects.HarvestTrailEffect;

/**
 * A magical fox that help the Farmer through harvesting
 * and storm-based abilities.
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.0.0
 */
public class PrismFox extends MagicalCompanion {

    /**
     * Creates a PrismFox with 20 hit points.
     */
    public PrismFox()
    {
        super("PrismFox", 'F', 20);
    }

    /**
     * Returns the fox's magical ability.
     *
     * @return the StormMagic ability
     */
    @Override
    protected FarmMagic getFarmMagic()
    {
        return null;
    }

    /**
     * Returns the fox's unique ability name.
     *
     * @return the ability name
     */
    @Override
    public String getOwnAbilityName()
    {
        return "Magical Harvest Trail";
    }

    /**
     * Activates the fox's unique harvesting ability.
     *
     * @param farmer the farmer using the ability
     * @param map the game map
     * @return true if the ability is successfully activated
     */
    @Override
    public boolean castOwnAbility(Actor farmer, GameMap map) {
        if (isEffectActive("Magical Harvest Trail")) {
            return false;
        }

        startEffect(new HarvestTrailEffect(map, map.locationOf(this), farmer));
        return true;
    }
}
