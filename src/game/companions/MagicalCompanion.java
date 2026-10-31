package game.companions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.displays.Display;
import game.effects.TimedAreaEffect;
import game.magic.FarmMagic;
import game.behaviours.WanderBehaviour;
import game.actions.ActivateOwnAbilityAction;
import game.actions.ActivateMagicAction;
import game.capabilities.ActorRole;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for magical farm companions.
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.0.0
 */
public abstract class MagicalCompanion extends Actor
{
    private final List<TimedAreaEffect> activeEffects = new ArrayList<>();
    private static final int MAX_CHARGES = 3;
    private int remainingCharges = MAX_CHARGES;
    private final WanderBehaviour wanderBehaviour = new WanderBehaviour();

    /**
     * Returns the number of remaining ability charges.
     *
     * @return remaining charges
     */
    public int getRemainingCharges()
    {
        return remainingCharges;
    }

    /**
     * Checks if the companion has charges remaining.
     *
     * @return true if at least one charge remains
     */
    public boolean hasCharges()
    {
        return remainingCharges > 0;
    }

    /**
     * Consumes one shared ability charge.
     */
    public void consumeCharge()
    {
        if (hasCharges())
        {
            remainingCharges--;
        }
    }

    /**
     * Returns the magical abilities available when another actor
     * interacts with this companion.
     * Both abilities are available as long as the companion has
     * remaining charges.
     *
     * @param otherActor the Actor that might be performing attack
     * @param direction String representing the direction of the other Actor
     * @param map current GameMap containing the companion
     * @return a list of available actions
     */
    @Override
    public ActionList allowableActions(Actor otherActor,
                                       String direction, GameMap map)
    {
        ActionList actions = new ActionList();

        if (otherActor.hasAbility(ActorRole.FARMER) && hasCharges())
        {
            FarmMagic magic = getFarmMagic();

            if (magic != null)
            {
                actions.add(new ActivateMagicAction(getFarmMagic(), this));
            }
            actions.add(new ActivateOwnAbilityAction(this));
        }
        return actions;
    }

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
     * Activates a timed magical effect and stores it for future turns.
     *
     * @param effect the timed magical effect to activate
     */
    protected void startEffect(TimedAreaEffect effect)
    {
        activeEffects.add(effect);
    }

    /**
     * Registers a timed effect activated by FarmMagic.
     *
     * @param effect the effect to register
     */
    public void addMagicEffect(TimedAreaEffect effect)
    {
        startEffect(effect);
    }

    /**
     * Check if a magical effect is currently active.
     *
     * @param abilityName the name of the effect
     * @return true if the effect is still active
     */
    public boolean isEffectActive(String abilityName)
    {
        for (TimedAreaEffect effect : activeEffects)
        {
            if (!effect.isExpired() && effect.getAbilityName().equals(abilityName))
            {
                return true;
            }

        }
        return false;
    }

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
        for (int i = activeEffects.size() - 1; i >= 0; i--)
        {
            TimedAreaEffect effect = activeEffects.get(i);

            if (!effect.isExpired())
            {
                effect.tick();
            }

            if (effect.isExpired())
            {
                effect.finish();
                activeEffects.remove(i);
            }
        }
        Action wanderAction = wanderBehaviour.operate(this, map.locationOf(this));

        if (wanderAction != null)
        {
            return wanderAction;
        }

        return new DoNothingAction();
    }
}
