package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

import game.companions.MagicalCompanion;
import game.magic.FarmMagic;

/**
 * An action that allows the Farmer to activate a
 * magical companion's Farm Magic ability
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.0.0
 */
public class ActivateMagicAction extends Action
{
    private final FarmMagic magic;
    private final MagicalCompanion companion;

    /**
     * Creates an action to activate Farm Magic.
     *
     * @param magic the Farm Magic ability
     * @param companion the comparison providing the ability
     */
    public ActivateMagicAction(FarmMagic magic, MagicalCompanion companion)
    {
        this.magic = magic;
        this.companion = companion;
    }

    /**
     * Activates the companion's Farm Magic.
     *
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return a description of the result.
     */
    @Override
    public String execute(Actor actor, GameMap map)
    {
        if (!companion.hasCharges())
        {
            return companion + " has no magical charges remaining.";
        }

        boolean success = magic.cast(actor, companion, map);

        if (success)
        {
            companion.consumeCharge();
            return actor + " activates " + magic.getName()
                    + " using " + companion + ".";
        }
        return magic.getName() + " could not be activated.";
    }

    /**
     * Returns the description displayed in the action menu.
     *
     * @param actor The Farmer performing the action.
     * @return the menu description
     */
    @Override
    public String menuDescription(Actor actor)
    {
        return actor + " activates " + magic.getName()
                + " using " + companion;
    }
}
