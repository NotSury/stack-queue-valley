package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.interfaces.Sellable;

/**
 * Represents an action that allows an actor to sell a Sellable
 * product from a merchant.
 *
 * @author Vanessa Liau Jia Yee <34599398>
 * @version 1.0.0
 */
public class SellAction extends Action
{
    private final Sellable target;
    private final Actor merchant;

    /**
     * Creates a new sell action for a specific item and merchant.
     *
     * @param target the sellable item to be sold
     * @param merchant the merchant buying the item
     */
    public SellAction(Sellable target, Actor merchant)
    {
        this.target = target;
        this.merchant = merchant;
    }

    /**
     * Executes the sell by delegating the selling behaviour to
     * the selected Sellable item.
     *
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return the result of the sale
     */
    @Override
    public String execute(Actor actor, GameMap map)
    {
        return target.sell(actor, merchant, map);
    }

    /**
     * Returns the description of the sell action shown in the
     * action menu.
     *
     * @param actor The actor performing the action.
     * @return the menu description of the selling action
     */
    @Override
    public String menuDescription(Actor actor)
    {
        return actor + " sells " + target + " for " + target.getSellPrice() + " coins";
    }
}
