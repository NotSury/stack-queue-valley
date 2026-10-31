package game.actions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

import game.companions.MagicalCompanion;
import game.magic.FarmMagic;

/**
 * An action that activate a
 * magical companion's Farm Magic ability
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.0.0
 */
public class ActivateMagicAction extends CompanionAction
{
    private final FarmMagic magic;

    /**
     * Creates an action to activate Farm Magic.
     *
     * @param magic the Farm Magic ability
     * @param companion the comparison providing the ability
     */
    public ActivateMagicAction(FarmMagic magic, MagicalCompanion companion)
    {
        super(companion);
        this.magic = magic;
    }

    /**
     * Activates the companion's Farm Magic.
     *
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return a true if the activation succeed
     */
    @Override
    public boolean activateAbility(Actor actor, GameMap map)
    {
        return magic.cast(actor, getCompanion(), map);

    }

    /**
     * Returns the name of the Farm Magic ability.
     *
     * @return the ability name
     */
    @Override
    public String getAbilityName()
    {
        return magic.getName();
    }
}
