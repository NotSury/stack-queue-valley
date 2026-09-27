package game.items;

import edu.monash.fit2099.engine.actors.Actor;

/**
 * Represents one preserved processing layer applied to a product.
 */
public class PreservedProduct extends ProcessableProduct {

    private static final int HEALTH_RESTORED = 1;

    private final ProcessableProduct previousProduct;

    /**
     * Constructs a preserved layer around an existing product.
     *
     * @param previousProduct the complete product before preservation
     */
    public PreservedProduct(ProcessableProduct previousProduct) {
        super("Preserved " + previousProduct, previousProduct.getDisplayChar());
        this.previousProduct = previousProduct;
    }

    /**
     * Applies all previous consumption effects before applying
     * the preserved effect.
     *
     * @param actor the actor consuming the product
     */
    @Override
    protected void applyConsumptionEffects(Actor actor) {
        previousProduct.applyConsumptionEffects(actor);
        actor.heal(HEALTH_RESTORED);
    }
}
