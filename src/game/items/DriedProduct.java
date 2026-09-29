package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.interfaces.ProcessableProduct;

/**
 * DriedProduct represents a processable product that has been dried.
 *
 * <p>A DriedProduct stores the complete product that existed before
 * the drying process. This allows multiple processing layers to be
 * applied while preserving the effects of all previous layers.</p>
 *
 * <p>When consumed, the effects of the previous product are applied
 * before the dried effect restores stamina to the actor.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class DriedProduct extends ProcessableItem {

    /**
     * The complete product before the drying process was applied.
     */
    private final ProcessableProduct previousProduct;

    /**
     * Constructor for the DriedProduct class.
     *
     * @param previousProduct the complete product before the drying process
     */
    public DriedProduct(ProcessableProduct previousProduct) {
        super("Dried " + previousProduct.getProductName(),'d');
        this.previousProduct = previousProduct;
    }

    /**
     * Returns the full name of the dried product.
     *
     * <p>The dried modifier is added before the complete name of the
     * previous product so that all earlier processing layers are preserved.</p>
     *
     * @return the full name of the dried product
     */
    @Override
    public String getProductName() {
        return "Dried " +  previousProduct.getProductName();
    }

    /**
     * Applies the consumption effects of this dried product.
     *
     * <p>The consumption effects of the previous product are applied first,
     * followed by the dried effect which restores 2 stamina points.</p>
     *
     * @param actor the actor consuming the product
     * @return a description of all consumption effects applied
     */
    @Override
    public String applyConsumptionEffect(Actor actor) {
        String previousEffect = previousProduct.applyConsumptionEffect(actor);

        actor.modifyStatistic(
                ActorStatistics.STAMINA,
                StatisticOperations.INCREASE,
                2
        );

        return previousEffect + " Dried effect restores 2 SP.";
    }
}
