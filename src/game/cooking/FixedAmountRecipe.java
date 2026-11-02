package game.cooking;

import game.capabilities.IngredientCategory;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A recipe that requires an exact quantity from each specified category.
 *
 * <p>Subclasses configure the requirements and provide their cooking effects
 * and dishes. Ingredient selection is shared by all fixed-amount recipes.
 *
 * @author Surya Muni Peragas 35651628
 * @version 1.0.0
 */
public abstract class FixedAmountRecipe extends Recipe {

    private final Map<IngredientCategory, Integer> requirements;

    /**
     * Creates a fixed-amount recipe.
     *
     * @param name the recipe name
     * @param requirements a non-empty map containing positive quantities for the required categories
     *
     */
    protected FixedAmountRecipe(String name, Map<IngredientCategory, Integer> requirements) {
        super(name);
        this.requirements = Map.copyOf(requirements);
    }


    /**
     * Selects exactly the required number of ingredients from each category.
     *
     * <p>Selection only builds a local list. No ingredient effects are applied
     * and no items are removed if any category has insufficient ingredients.
     *
     * @param available the distinct ingredient entries collected by Recipe
     * @return the complete selection, or empty if any requirement is unmet
     */
    @Override
    protected final Optional<List<IngredientEntry>> selectIngredients(
            List<IngredientEntry> available
    ) {
        Map<IngredientCategory, Integer> remaining = new EnumMap<>(IngredientCategory.class);

        remaining.putAll(requirements);

        List<IngredientEntry> selected = new ArrayList<>();

        for (IngredientEntry entry : available) {
            IngredientCategory category = entry.getCategory();
            int amountNeeded = remaining.getOrDefault(category, 0);

            if (amountNeeded > 0) {
                selected.add(entry);
                remaining.put(category, amountNeeded - 1);
            }
        }

        for (int amountNeeded : remaining.values()) {
            if (amountNeeded > 0) {
                return Optional.empty();
            }
        }

        return Optional.of(List.copyOf(selected));
    }
}
