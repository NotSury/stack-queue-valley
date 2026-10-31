package game.companions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.displays.Display;

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

    /**
     * Determines what the magical companion does each turn.
     *
     * @param actions collection of possible Actions for this Actor
     * @param lastAction The Action this Actor took last turn. Can do
     * interesting things in conjunction with Action.getNextAction()
     * @param map the map containing the Actor
     * @param display the I/O object to which messages may be written
     * @return the action performed
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction,
                           GameMap map, Display display)
    {
        return new DoNothingAction();
    }

}
