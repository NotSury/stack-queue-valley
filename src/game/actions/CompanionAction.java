package game.actions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.actions.Action;
import game.companions.MagicalCompanion;

/**
 * Abstract action for activating a magical companion's ability.
 * Handles shared changes and delegates the specific ability
 * to subclasses.
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.0.0
 */
public abstract class CompanionAction extends Action
{
    private final MagicalCompanion companion;

    /**
     * Creates a companion action.
     *
     * @param companion the companion providing the ability
     */
    protected CompanionAction(MagicalCompanion companion)
    {
        this.companion = companion;
    }

    /**
     * Returns the companion associated with this action.
     *
     * @return the magical companion
     */
    protected MagicalCompanion getCompanion()
    {
        return companion;
    }

    /**
     * Activates an ability if charges are available.
     *
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return a description of the activation result.
     */
    @Override
    public String execute(Actor actor, GameMap map)
    {
        if (!companion.hasCharges())
        {
            return companion + " has no magical charges remaining.";
        }

        boolean success = activateAbility(actor, map);

        if (success)
        {
            companion.consumeCharge();

            return actor + " activates " + getAbilityName()
                    + " using " + companion + ".";
        }
        return getAbilityName() + " could not be activated.";
    }

    /**
     * Performs the specific magical ability.
     *
     * @param actor the actor activating the ability.
     * @param map the current game map.
     * @return true if the ability activates successfully
     */
    protected abstract boolean activateAbility(Actor actor, GameMap map);

    /**
     * Returns the name of ability.
     *
     * @return the ability name
     */
    protected abstract String getAbilityName();

    /**
     * Returns the action's menu description.
     *
     * @param actor The actor performing the action.
     * @return the menu description
     */
    @Override
    public String menuDescription(Actor actor)
    {
        return actor + " activates " + getAbilityName()
        + " using " + companion;
    }
}
