package game.companions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.magic.FarmMagic;

/**
 * Abstract base class for magical farm companions.
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.0.0
 */
public abstract class MagicalCompanion extends Actor
{
    /**
     * Creates a magical companion.
     *
     * @param name companion name
     * @param displayChar character displayed on the map
     * @param hitPoints starting health points
     */
    protected MagicalCompanion(String name, char displayChar, int hitPoints)
    {
        super(name, displayChar, hitPoints);
    }

    /**
     * Returns the companion's farm magic.
     *
     * @return the companion's magic ability
     */
    protected abstract FarmMagic getFarmMagic();

    /**
     * Returns the name of the companion's own ability.
     *
     * @return the ability name
     */
    public abstract String getOwnAbilityName();

    /**
     * Activates the companion's own ability.
     *
     * @param farmer the farmer using the ability
     * @param map the game map
     * @return true if the ability activates successfully
     */
    public abstract boolean castOwnAbility(Actor farmer, GameMap map);

}
