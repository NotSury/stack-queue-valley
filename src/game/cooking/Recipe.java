package game.cooking;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.items.Item;
import game.items.ConsumableItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Defines the shared cooking workflow for all recipes.
 *
 * <p>Subclasses provide ingredient selection, recipe cooking effects
 * and dish creation. The final cooking operation ensures these steps
 * run in the required order.
 *
 * @author Surya Muni Peragas 35651628
 * @version 1.0.0
 */
public abstract class Recipe {

    private final String name;

    /**
     * Creates a named recipe.
     *
     * @param name the recipe name displayed to the player
     */
    protected Recipe(String name) {
        this.name = name;
    }

    /**
     * Returns the recipe name.
     *
     * @return the recipe name
     */
    public final String getName() {
        return name;
    }

    /**
     * Attempts to cook this recipe.
     *
     * <p>All ingredient requirements are checked before any effects occur
     * or items are removed. Successful cooking processes ingredient effects,
     * applies the recipe effect, then creates and adds the finished dish.
     *
     * @param context the context for this cooking attempt
     * @return a description of the failure or successful cooking events
     */
    public final String cook(CookingContext context) {
        Actor cook = context.getCook();
        Inventory inventory = cook.getInventory();

        List<IngredientEntry> available = collectIngredients(inventory);
        Optional<List<IngredientEntry>> selection =
                selectIngredients(available);

        if (selection.isEmpty()) {
            return cook + " cannot cook " + name
                    + ": missing required ingredients.";
        }

        CookingBatch batch = new CookingBatch(selection.get());

        String ingredientMessage = batch.applyIngredients(context);
        String recipeMessage = applyCookingEffect(context, batch);

        ConsumableItem dish = createDish(batch);
        inventory.add(dish);

        StringJoiner messages =
                new StringJoiner(System.lineSeparator());

        for (String message : List.of(
                ingredientMessage,
                recipeMessage,
                cook + " cooks " + name + ".")) {

            if (!message.isEmpty()) {
                messages.add(message);
            }
        }

        return messages.toString();
    }

    /**
     * Collects the cooking ingredients currently carried in the inventory.
     *
     * <p>Each original item is considered once. Collection does not apply
     * effects or modify the inventory.
     *
     * @param inventory the inventory to inspect
     * @return an unmodifiable snapshot of the available ingredient entries
     */
    private List<IngredientEntry> collectIngredients(
            Inventory inventory) {

        List<IngredientEntry> entries = new ArrayList<>();

        Set<Item> seenItems =
                Collections.newSetFromMap(new IdentityHashMap<>());

        for (Item item : inventory.getItems()) {
            if (seenItems.add(item)) {
                IngredientEntry.from(item).ifPresent(entries::add);
            }
        }

        return List.copyOf(entries);
    }

    /**
     * Selects all ingredients required by this recipe.
     *
     * <p>A successful selection must be non-empty, satisfy every requirement
     * and contain each available entry at most once. Selection must not
     * apply effects or change the inventory or world.
     *
     * @param available the available ingredient entries
     * @return the complete selection, or empty if requirements cannot be met
     */
    protected abstract Optional<List<IngredientEntry>> selectIngredients(
            List<IngredientEntry> available);

    /**
     * Applies the recipe-specific effect after ingredient processing.
     *
     * @param context the context for the cooking attempt
     * @param batch the ingredients used to cook the recipe
     * @return an effect description, or an empty string if nothing happened
     */
    protected abstract String applyCookingEffect(
            CookingContext context,
            CookingBatch batch);

    /**
     * Creates a fresh dish for this cooking attempt.
     *
     * @param batch the ingredients used to determine any dish-specific values
     * @return a new consumable dish
     */
    protected abstract ConsumableItem createDish(CookingBatch batch);
}