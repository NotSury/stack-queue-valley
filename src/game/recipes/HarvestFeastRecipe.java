package game.recipes;

import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.IngredientCategory;
import game.cooking.CookingBatch;
import game.cooking.CookingContext;
import game.cooking.FixedAmountRecipe;
import game.items.ConsumableItem;
import game.items.HarvestFeast;

import java.util.Map;
import java.util.Optional;

/**
 * A recipe requiring five Fruit, five Vegetable and
 * five Animal Product ingredients.
 *
 * <p>Cooking places an extra Harvest Feast on a random adjacent
 * tile and produces another for the cook's inventory.</p>
 */
public class HarvestFeastRecipe extends FixedAmountRecipe {

    private static final int REQUIRED_PER_CATEGORY = 5;

    /**
     * Creates a Harvest Feast recipe with its ingredient quantities.
     */
    public HarvestFeastRecipe() {
        super("Harvest Feast", Map.of(
                IngredientCategory.FRUIT, REQUIRED_PER_CATEGORY,
                IngredientCategory.VEGETABLE, REQUIRED_PER_CATEGORY,
                IngredientCategory.ANIMAL_PRODUCT, REQUIRED_PER_CATEGORY
        ));
    }

    /**
     * Places an extra Harvest Feast on a random adjacent tile.
     *
     * @param context the actor, station location and randomness for cooking
     * @param batch the ingredients used for this cooking attempt
     * @return a description of the extra Feast, or an empty string if there is no adjacent tile
     */
    @Override
    protected String applyCookingEffect(CookingContext context, CookingBatch batch) {
        Optional<Location> destination = context.randomAdjacent(location -> true);

        if (destination.isEmpty()) {
            return "";
        }

        Location feastLocation = destination.get();
        feastLocation.addItem(new HarvestFeast());

        return "An extra Harvest Feast appears at " + feastLocation + ".";
    }

    /**
     * Creates the Harvest Feast that will enter the cook's inventory.
     *
     * @param batch the ingredients used for this cooking attempt
     * @return a new Harvest Feast
     */
    @Override
    protected ConsumableItem createDish(CookingBatch batch) {
        return new HarvestFeast();
    }
}
