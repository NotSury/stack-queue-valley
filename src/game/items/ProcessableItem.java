package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import game.interfaces.ProcessableProduct;

/**
 * ProcessableItem is an abstract base class for consumable items
 * that can also be processed by processing facilities.
 *
 * <p>This class extends ConsumableItem so that processable products
 * reuse the common consumption behaviour while also implementing
 * ProcessableProduct.</p>
 *
 * <p>Concrete subclasses define their own product names and specific
 * consumption effects.</p>
 *
 * @author <Lee Le Si> <34599223>
 * @author <Chin Kai Xuan> <35730374>
 * @version 1.0.0
 */
public abstract class ProcessableItem
        extends ConsumableItem
        implements ProcessableProduct {

    /**
     * Constructor for ProcessableItem.
     *
     * @param name the name of the processable item
     * @param displayChar the character used to represent the item
     */
    protected ProcessableItem(String name, char displayChar) {
        super(name, displayChar);
    }

    /**
     * Applies the specific consumption effect of this processable product.
     *
     * <p>This method remains public because it is part of the
     * ProcessableProduct interface and may also be called by processed
     * products such as DriedProduct and PreservedProduct.</p>
     *
     * @param actor the actor consuming the product
     * @return a description of the consumption effect
     */
    @Override
    public abstract String applyConsumptionEffect(Actor actor);

    /**
     * Returns the complete name of this processable product.
     *
     * @return the complete product name
     */
    @Override
    public abstract String getProductName();
}