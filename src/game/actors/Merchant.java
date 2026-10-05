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

public class Merchant extends Actor
{
    private final Behaviour<Actor, Action> behaviour = new WanderBehaviour();
    private final List<Buyable> buyables = List.of(
            new BuyParsnipCrop(), new BuyTomatoCrop(), new BuySword()
    );

    public Merchant()
    {
        super("Merchant", 'M', 100);
    }

    @Override
    public Action playTurn(ActionList actions, Action lastAction,
                           GameMap map, Display display)
    {
        Action action = behaviour.operate(this, map.locationOf(this));

        if (action != null)
        {
            return action;
        }

        return new DoNothingAction();
    }

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
