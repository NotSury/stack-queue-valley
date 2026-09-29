package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
/**
 * ProcessableProduct represents a consumable product that can be processed
 * by a processing facility.
 *
 * <p>A processable product provides its full product name and defines
 * the consumption effect associated with the complete product. Processed
 * products may delegate their effects to a previous ProcessableProduct,
 * allowing repeated and mixed processing without storing processing history
 * using counters or collections.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public interface ProcessableProduct extends Consumable{
    /**
     * Returns the complete name of this processable product.
     *
     * <p>For processed products, the returned name can include all previous
     * processing layers.</p>
     *
     * @return the complete name of the product
     */
    String getProductName();
    /**
     * Applies the consumption effects of this product to the actor.
     *
     * <p>Processed products may first apply the effects of their predecessor
     * before applying their own processing effect.</p>
     *
     * @param actor the actor consuming the product
     * @return a description of the consumption effects applied
     */
    String applyConsumptionEffect(Actor actor);
}
