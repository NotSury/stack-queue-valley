package game.actors;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.BuyAction;
import game.behaviours.WanderBehaviour;
import game.capabilities.FarmerStatistic;
import game.actions.SellAction;
import game.interfaces.Sellable;
import game.interfaces.Buyable;
import game.trading.BuyParsnipCrop;
import game.trading.BuyTomatoCrop;
import game.trading.BuySword;

import java.util.List;

/**
 * This represents the Merchant actor in the game.
 *
 * The Merchant has 100 hit points and wanders around the game map.
 * When an actor with a coin statistic is adjacent to the Merchant,
 * the actor may buy available products or sell items that implement
 * the Sellable interface.
 *
 * The Merchant is represented by the '$' character.
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @author Khoo Lynn 34598901
 * @version 1.1.0
 */
public class Merchant extends Actor
{
    private final Behaviour<Actor, Action> behaviour = new WanderBehaviour();
    private final List<Buyable> buyables = List.of(
            new BuyParsnipCrop(), new BuyTomatoCrop(), new BuySword()
    );

    /**
     * This represents the Merchant actor in the game.
     *
     * The Merchant is displayed on the map using the '$' character.
     * Farmer can buy or sell items from and to the Merchant when it is adjacent.
     */
    public Merchant()
    {
        super("Merchant", '$', 100);
    }

    /**
     * This determines the action performed by the Merchant during its turn.
     *
     * @param actions collection of possible Actions for this Actor
     * @param lastAction The Action this Actor took last turn. Can do
     * interesting things in conjunction with Action.getNextAction()
     * @param map the map containing the Actor
     * @param display the I/O object to which messages may be written
     * @return the action performed by the Merchant
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction,
                           GameMap map, Display display)
    {
        if (!isConscious())
        {
            display.println(unconscious(map));
            return new DoNothingAction();
        }

        Action action = behaviour.operate(this, map.locationOf(this));

        if (action != null)
        {
            return action;
        }

        return new DoNothingAction();
    }

    /**
     * Returns the trading actions available to an actor adjacent to the Merchant.
     *
     * @param otherActor the Actor that might be performing attack
     * @param direction String representing the direction of the other Actor
     * @param map current GameMap
     * @return a list of available buy and sell actions
     */
    @Override
    public ActionList allowableActions(Actor otherActor, String direction, GameMap map)
    {
        ActionList actions = new ActionList();

        if (!otherActor.hasStatistic(FarmerStatistic.COINS))
        {
            return actions;
        }

        for (Buyable buyable : buyables)
        {
            actions.add(new BuyAction(buyable, this));
        }

        // for every sellable object, get it from inventory
        // get the ones that implement Sellable interface
        for (Sellable sellable : otherActor.getInventory().getItemsAs(Sellable.class))
        {
            actions.add(new SellAction(sellable, this));
        }

        return actions;
    }



}
