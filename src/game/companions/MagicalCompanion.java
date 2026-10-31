package game.companions;

import edu.monash.fit2099.engine.actors.Actor;

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

}
