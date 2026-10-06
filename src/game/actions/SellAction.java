package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.interfaces.Sellable;

public class SellAction extends Action
{
    private final Sellable target;
    private final Actor merchant;

    public SellAction(Sellable target, Actor merchant)
    {
        this.target = target;
        this.merchant = merchant;
    }

    @Override
    public String execute(Actor actor, GameMap map)
    {
        return target.sell(actor, merchant, map);
    }

    @Override
    public String menuDescription(Actor actor)
    {
        return actor + " sells " + target + " for " + target.getSellPrice() + " coins";
    }
}
