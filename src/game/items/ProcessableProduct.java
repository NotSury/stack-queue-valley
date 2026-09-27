package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.ConsumeAction;
import game.capabilities.Consumable;

/**
 * Represents an item that can be consumed and processed by artisan facilities.
 */
public abstract class ProcessableProduct extends Item implements Consumable {

    /**
     * Constructs a portable processable product.
     *
     * @param name the name of the product.
     * @param displayChar the character representing the product
     */
    protected ProcessableProduct(String name, char displayChar) {
        super(name, displayChar);
        this.makePortable();
    }

    /**
     * Applies all consumption effects belonging to this product.
     *
     * @param actor the actor consuming the product
     */
    protected abstract void applyConsumptionEffects(Actor actor);

    /**
     * Consumes this complete product and removes it from the inventory once.
     *
     * @param actor the actor consuming the product
     * @return a description of the result
     */
    @Override
    public final String consumedBy(Actor actor) {
        applyConsumptionEffects(actor);
        actor.getInventory().remove(this);

        return actor + " consumes " + this + ".";
    }

    /**
     * Returns the actions available while this product is carried.
     * All processable products can be consumed.
     *
     * @param owner the actor carrying this product
     * @param map the map where the actor is performing the action on
     * @return the available actions
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        actions.add(new ConsumeAction(this));
        return actions;
    }
}
