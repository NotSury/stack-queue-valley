package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.interfaces.ProcessableProduct;
/**
 * PreservedProduct represents a processable product that has been
 * processed using a preserving method.
 *
 * <p>A PreservedProduct stores the complete product that existed
 * immediately before preservation. This allows preservation to be
 * combined with previous processing layers without modifying them.</p>
 *
 * <p>When consumed, the effects of the previous product are applied
 * first before the preserved effect restores 1 health point.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class PreservedProduct extends ProcessableItem {
    /**
     * The complete product before the preserving process was applied.
     */
    private final ProcessableProduct previousProduct;
    /**
     * Constructor for the PreservedProduct class.
     *
     * @param previousProduct the complete product before preservation
     */
    public PreservedProduct(ProcessableProduct previousProduct) {
        super("Preserved " + previousProduct.getProductName(),'p');
        this.previousProduct = previousProduct;
    }

    /**
     * Returns the full name of the preserved product.
     *
     * <p>The preserved modifier is added before the complete name of
     * the previous product, preserving all earlier processing names.</p>
     *
     * @return the full name of the preserved product
     */
    @Override
    public String getProductName() {
        return "Preserved " + previousProduct.getProductName();
    }

    /**
     * Applies the consumption effects of this preserved product.
     *
     * <p>The consumption effects of the previous product are applied
     * first, followed by the preserved effect which restores 1 health
     * point to the actor.</p>
     *
     * @param actor the actor consuming the product
     * @return a description of all consumption effects applied
     */
    @Override
    public String applyConsumptionEffect(Actor actor){
        String previousEffect = previousProduct.applyConsumptionEffect(actor);

        actor.modifyStatistic(
                ActorStatistics.HEALTH,
                StatisticOperations.INCREASE,
                1
        );

        return previousEffect + " Preserved effect restores 1 HP.";

    }
}
