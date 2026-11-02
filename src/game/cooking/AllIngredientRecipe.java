package game.cooking;

import game.capabilities.IngredientCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A recipe that uses every available ingredient from one category.
 *
 * <p>At least one matching ingredient is required. Subclasses configure
 * the category and provide their cooking effects and dishes.
 *
 * @author Surya Muni Peragas 35651628
 * @version 1.0.0
 */
public abstract class AllIngredientRecipe extends Recipe {

    private final IngredientCategory category;

    /**
     * Creates a recipe that uses every ingredient from one category.
     *
     * @param name the recipe name
     * @param category the category of ingredients to select
     */
    protected AllIngredientRecipe(String name, IngredientCategory category) {
        super(name);
        this.category = category;
    }

    /**
     * Selects every available ingredient belonging to the configured category.
     *
     * <p>Selection does not apply effects or change the inventory.
     *
     * @param available the distinct ingredient entries collected by Recipe
     * @return all matching entries, or empty if none are available
     */
    @Override
    protected final Optional<List<IngredientEntry>> selectIngredients(
            List<IngredientEntry> available
    ) {
        List<IngredientEntry> selected = new ArrayList<>();

        for (IngredientEntry entry : available) {
            if (entry.getCategory() == category) {
                selected.add(entry);
            }
        }

        if (selected.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(List.copyOf(selected));
    }
}
