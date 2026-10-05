package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.interfaces.Buyable;

public class BuyAction extends Action
{
    private final Buyable target;
    private final Actor merchant;

    public BuyAction(Buyable target, Actor merchant)
    {
        this.target = target;
        this.merchant = merchant;
    }

    @Override
    public String execute(Actor actor, GameMap map)
    {
        return target.buy(actor, merchant, map);
    }

    @Override
    public String menuDescription(Actor actor)
    {
        return actor + " buys " + target + " for " + target.getBuyPrice() + " coins";
    }
}
