package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.ConsumeAction;
import game.interfaces.Consumable;

/**
 * ConsumableItem is an abstract base class for portable items
 * that can be consumed by an actor.
 *
 * <p>This class centralises the common behaviour of consumable items,
 * including portability, providing a ConsumeAction, applying the item's
 * consumption effect, and removing the item from the actor's inventory
 * after it is consumed.</p>
 *
 * @author <Chin Kai Xuan> <35730374>
 * @version 1.0.0
 */
public abstract class ConsumableItem extends Item implements Consumable {

    /**
     * Constructor for ConsumableItem.
     *
     * @param name the name of the consumable item
     * @param displayChar the character used to display the item
     */
    protected ConsumableItem(String name, char displayChar) {
        super(name, displayChar);
        this.makePortable();
    }

    /**
     * Consumes this item, applies its effect, and removes it
     * from the actor's inventory.
     *
     * @param actor the actor consuming the item
     * @return a description of the consumption effect
     */
    @Override
    public final String consume(Actor actor) {
        String result = applyConsumptionEffect(actor);
        actor.getInventory().remove(this);
        return result;
    }

    /**
     * Applies the specific consumption effect of this item.
     *
     * @param actor the actor consuming the item
     * @return a description of the consumption effect
     */
    protected abstract String applyConsumptionEffect(Actor actor);

    /**
     * Returns the actions available for this item while carried.
     *
     * @param owner the actor carrying the item
     * @param map the current game map
     * @return the available actions for this item
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        actions.add(new ConsumeAction(this));
        return actions;
    }
}