package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.capabilities.FarmerStatistic;
import game.interfaces.Buyable;

/**
 * Represents an action that allows an actor to buy a Buyable
 * product from a merchant.
 *
 * @author Vanessa Liau Jia Yee <34599398>
 * @version 1.0.0
 */
public class BuyAction extends Action
{
    private final Buyable target;
    private final Actor merchant;

    /**
     * Creates a new buy action for a specific product and merchant.
     *
     * @param target the buyable product to be purchased
     * @param merchant the merchant selling the product
     */
    public BuyAction(Buyable target, Actor merchant)
    {
        this.target = target;
        this.merchant = merchant;
    }

    /**
     * Executes the purchase by delegating the buying behaviour to
     * the selected Buyable product.
     *
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return the result of the purchase
     */
    @Override
    public String execute(Actor actor, GameMap map)
    {
        if (actor.getStatistic(FarmerStatistic.COINS) < target.getBuyPrice())
        {
            return actor + " does not have enough coins to buy " +
                    target + ".";
        }
        return target.buy(actor, merchant, map);
    }

    /**
     * Returns the description of the buy action shown in the
     * action menu.
     *
     * @param actor The actor performing the action.
     * @return the menu description of the purchase action
     */
    @Override
    public String menuDescription(Actor actor)
    {
        return actor + " buys " + target + " for " + target.getBuyPrice() + " coins";
    }
}
