package game.cooking;

import java.util.List;
import java.util.StringJoiner;

/**
 * Holds the validated ingredients selected for one cooking attempt.
 *
 * <p>The recipe must check that the selection satisfies its requirements
 * and contains each original item at most once before creating the batch.
 *
 * @author Surya Muni Peragas 35651628
 * @version 1.0.0
 */
public final class CookingBatch {

    private final List<IngredientEntry> entries;

    /**
     * Creates a batch from a validated ingredient selection.
     *
     * @param entries the non-empty selection of distinct ingredient entries
     */
    public CookingBatch(List<IngredientEntry> entries) {
        this.entries = List.copyOf(entries);
    }

    /**
     * Returns the number of ingredients selected for this batch.
     *
     * @return the number of selected ingredients
     */
    public int size() {
        return entries.size();
    }

    /**
     * Applies each selected ingredient's effect and removes its original item.
     *
     * <p>The recipe calls this operation once after successful validation.
     * Only effects that produce a message are included in the result.
     *
     * @param context the context for the cooking attempt
     * @return the ingredient-effect messages separated by line breaks,
     *     or an empty string if no effects produced messages
     */
    public String applyIngredients(CookingContext context) {
        StringJoiner messages = new StringJoiner(System.lineSeparator());

        for (IngredientEntry entry : entries) {
            String message = entry.applyAndRemove(context);

            if (!message.isEmpty()) {
                messages.add(message);
            }
        }

        return messages.toString();
    }
}
