package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.ConsumeAction;
import game.interfaces.ProcessableProduct;
/**
 * ProcessableItem is an abstract base class for portable items that
 * can be consumed and processed by processing facilities.
 *
 * <p>This class connects the engine Item hierarchy with the
 * ProcessableProduct abstraction. It centralises behaviour shared by
 * all processable products, including portability, consumption actions,
 * and removal of the complete product after consumption.</p>
 *
 * <p>Concrete subclasses define their own product names and specific
 * consumption effects.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public abstract class ProcessableItem extends Item implements ProcessableProduct {
    /**
     * Constructor for the ProcessableItem class.
     *
     * <p>The created item is made portable so that all processable
     * products can be stored in an actor's inventory.</p>
     *
     * @param name the name of the processable item
     * @param displayChar the character used to represent the item
     */
    public ProcessableItem(String name, char displayChar) {
        super(name, displayChar);
        this.makePortable();
    }

    /**
     * Consumes the complete processable item.
     *
     * <p>The product-specific consumption effects are applied first.
     * The complete item is then removed from the actor's inventory.
     * Centralising the removal here ensures that a processed product is
     * removed only after all of its processing-layer effects are applied.</p>
     *
     * @param actor the actor consuming the item
     * @return a description of the consumption effects
     */
    @Override
    public final String consume(Actor actor){
        String result = applyConsumptionEffect(actor);
        actor.getInventory().remove(this);
        return result;
    }

    /**
     * Returns the actions available for this processable item while it
     * is carried by an actor.
     *
     * <p>All ProcessableItems can be consumed, so a ConsumeAction is
     * provided for the item.</p>
     *
     * @param owner the actor carrying the item
     * @param map the current game map
     * @return a list containing the available actions for this item
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map){
        ActionList actions = new ActionList();
        actions.add(new ConsumeAction(this));
        return actions;
    }
}
