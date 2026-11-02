package game.recipes;

import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.IngredientCategory;
import game.cooking.CookingBatch;
import game.cooking.CookingContext;
import game.cooking.FixedAmountRecipe;
import game.items.ConsumableItem;
import game.items.Fire;
import game.items.Omelette;

import java.util.Map;
import java.util.Optional;

/**
 * A recipe requiring one Animal Product and one Vegetable.
 *
 * <p>Cooking has a chance to place Fire on a random adjacent tile.
 * Successful cooking produces a fresh Omelette.</p>
 *
 * @author Surya Muni Peragas 35651628
 * @version 1.0.0
 */
public class OmeletteRecipe extends FixedAmountRecipe {

    private static final double FLARE_CHANCE = 0.30;
    private static final int FIRE_DURATION = 2;
    private static final int FIRE_DAMAGE = 5;
    private static final int BURN_DURATION = 3;

    /**
     * Creates an Omelette recipe with its required ingredient quantities.
     */
    public OmeletteRecipe() {
        super("Omelette", Map.of(
                IngredientCategory.ANIMAL_PRODUCT, 1, IngredientCategory.VEGETABLE, 1
        ));
    }

    /**
     * Attempts to place Fire on a random tile adjacent to the station.
     *
     * @param context the actor, station location and randomness for cooking
     * @param batch the ingredients used for this cooking attempt
     * @return a description of the flare, or an empty string if none occurs
     */
    @Override
    protected String applyCookingEffect(CookingContext context, CookingBatch batch) {
        if (!context.chance(FLARE_CHANCE)) {
            return "";
        }

        Optional<Location> destination = context.randomAdjacent(location -> true);

        if (destination.isEmpty()) {
            return "";
        }

        Location fireLocation = destination.get();
        fireLocation.addItem(new Fire(FIRE_DURATION, FIRE_DAMAGE, BURN_DURATION));

        return "The cooking station flares up, creating Fire at " + fireLocation + ".";
    }

    /**
     * Creates a fresh Omelette after the cooking effects finish.
     *
     * @param batch the ingredients used for this cooking attempt
     * @return a new Omelette
     */
    @Override
    protected ConsumableItem createDish(CookingBatch batch) {
        return new Omelette();
    }
}
