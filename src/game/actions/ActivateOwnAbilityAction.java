package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.companions.MagicalCompanion;


/**
 * An action that allows the Farmer to activate a
 * magical companion's unique ability
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.0.0
 */
public class ActivateOwnAbilityAction extends Action
{
    private final MagicalCompanion companion;
    /**
     * Creates an action for the companion's unique ability.
     *
     * @param companion the magical companion
     */
    public ActivateOwnAbilityAction(MagicalCompanion companion)
    {
        this.companion = companion;
    }

    /**
     * Activates the companion's unique ability.
     *
     * @param actor The Farmer performing the action.
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

        boolean success = companion.castOwnAbility(actor, map);

        if (success)
        {
            companion.consumeCharge();
            return actor + " activates " + companion.getOwnAbilityName()
                    + " using " + companion + ".";
        }
        return companion.getOwnAbilityName() + " could not be activated.";
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
        return actor + " activates " + companion.getOwnAbilityName()
                + " using " + companion;
    }
}
