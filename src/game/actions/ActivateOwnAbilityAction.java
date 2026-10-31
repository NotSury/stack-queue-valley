package game.actions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.companions.MagicalCompanion;


/**
 * An action that activate a
 * magical companion's unique ability
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.0.0
 */
public class ActivateOwnAbilityAction extends CompanionAction
{
    /**
     * Creates an action for the companion's unique ability.
     *
     * @param companion the magical companion
     */
    public ActivateOwnAbilityAction(MagicalCompanion companion)
    {
        super(companion);;
    }

    /**
     * Activates the companion's unique ability.
     *
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return a true if the ability activation succeed
     */
    @Override
    public boolean activateAbility(Actor actor, GameMap map)
    {
        return getCompanion().castOwnAbility(actor, map);

    }

    /**
     * Returns the name of the companion's unique ability.
     *
     * @return the ability name
     */
    @Override
    public String getAbilityName()
    {
        return getCompanion().getOwnAbilityName();
    }
}
