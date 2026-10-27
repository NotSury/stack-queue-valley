package game.interfaces;

import game.capabilities.IngredientCategory;
import game.cooking.CookingContext;

/**
 * Defines the cooking capability of an ingredient.
 *
 * <p>Ingredients provide their category and their effect when cooked.
 * Cooking effects are separate from raw consumption effects.
 */
public interface Ingredient {

    /**
     * Returns the single category to which this ingredient belongs.
     *
     * @return the ingredient's non-null category
     */
    IngredientCategory getCategory();

    /**
     * Applies this ingredient's effect after the entire recipe is validated.
     *
     * <p>This method must not remove the ingredient from the inventory.
     * Removal is handled by the shared cooking workflow.
     *
     * <p>An effect that cannot occur does not invalidate the ingredient.
     * For example, an Egg is still consumed when no Chicken can hatch.
     *
     * @param context the information and shared operations for this cooking attempt
     * @return a description of the effect, or an empty string if nothing happened
     */
    String applyCookingEffect(CookingContext context);
}
