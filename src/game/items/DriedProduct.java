package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

/**
 * Represents one dried processing layer applied to a product.
 */
public class DriedProduct extends ProcessableProduct {

    private static final int STAMINA_RESTORED = 2;

    private final ProcessableProduct previousProduct;

    /**
     * Constructs a dried layer around an existing product.
     *
     * @param previousProduct the complete product before drying
     */
    public DriedProduct(ProcessableProduct previousProduct) {
        super("Dried " + previousProduct, previousProduct.getDisplayChar());
        this.previousProduct = previousProduct;
    }

    /**
     * Applies all previous consumption effects before applying
     * the dried effect.
     *
     * @param actor the actor consuming the product
     */
    @Override
    protected void applyConsumptionEffects(Actor actor) {
        previousProduct.applyConsumptionEffects(actor);

        actor.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.INCREASE, STAMINA_RESTORED);
    }
}
