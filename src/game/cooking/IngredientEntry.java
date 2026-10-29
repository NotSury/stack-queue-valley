package game.cooking;

import edu.monash.fit2099.engine.items.Item;
import game.capabilities.IngredientCategory;
import game.interfaces.Ingredient;

import java.util.Optional;

/**
 * Pairs an original inventory item with its cooking capability.
 *
 * <p>Both references represent the same object. The Ingredient reference
 * provides cooking behavior, while the Item reference allows inventory
 * removal without downcasting.
 */
public final class IngredientEntry {

    private final Item item;
    private final Ingredient ingredient;

    /**
     * Creates an entry from an item and its own ingredient capability.
     *
     * @param item the original inventory item
     * @param ingredient the cooking capability obtained from that item
     */
    private IngredientEntry(Item item, Ingredient ingredient) {
        this.item = item;
        this.ingredient = ingredient;
    }

    /**
     * Creates an entry if the item provides the Ingredient capability.
     *
     * <p>This operation does not apply effects or change the inventory.
     *
     * @param item the inventory item to inspect
     * @return an entry for the ingredient, or empty if the item is not an ingredient
     */
    public static Optional<IngredientEntry> from(Item item) {
        return item.asCapability(Ingredient.class)
                .map(ingredient -> new IngredientEntry(item, ingredient));
    }

    /**
     * Returns the category provided by the ingredient.
     *
     * @return the ingredient category
     */
    public IngredientCategory getCategory() {
        return ingredient.getCategory();
    }

    /**
     * Applies the ingredient effect and then removes the original item.
     *
     * <p>The cooking workflow must validate the entire recipe before calling
     * this operation, and must call it once per selected entry.
     * The item is removed even when its effect produces no event.
     *
     * @param context the context for the cooking attempt
     * @return the description returned by the ingredient effect
     */
    public String applyAndRemove(CookingContext context) {
        String result = ingredient.applyCookingEffect(context);
        context.getCook().getInventory().remove(item);
        return result;
    }
}
