package game.recipes;

import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.GroundAbility;
import game.capabilities.IngredientCategory;
import game.cooking.AllIngredientRecipe;
import game.cooking.CookingBatch;
import game.cooking.CookingContext;
import game.interfaces.Tillable;
import game.items.ConsumableItem;
import game.items.Jam;
import game.utils.AdjacentLocations;

/**
 * Cooks Jam using every Fruit ingredient carried by the cook.
 *
 * <p>Cooking enriches every adjacent bare Dirt tile into Soil.
 * Each resulting Jam stores the number of fruits used in its batch.
 */
public class JamRecipe extends AllIngredientRecipe {

    /**
     * Creates the recipe that uses every available Fruit ingredient.
     */
    public JamRecipe() {
        super("Jam", IngredientCategory.FRUIT);
    }

    /**
     * Enriches adjacent bare Dirt using its existing tilling behaviour.
     *
     * @param context the context for the cooking attempt
     * @param batch the ingredients used to cook the Jam
     * @return an enrichment description, or an empty string if nothing changed
     */
    @Override
    protected String applyCookingEffect(CookingContext context, CookingBatch batch) {

        int enrichedTiles = 0;

        for (Location destination : AdjacentLocations.matchingAround(
                context.getStationLocation(),
                location -> location.getGround()
                        .hasAbility(GroundAbility.BARE_DIRT))) {

            Tillable tillable = destination.getGroundAs(Tillable.class);

            if (tillable != null) {
                tillable.till(destination);
                enrichedTiles++;
            }
        }

        if (enrichedTiles == 0) {
            return "";
        }

        return "Jam pulp enriches nearby Dirt patches: "
                + enrichedTiles + ".";
    }

    /**
     * Creates a fresh Jam using the batch's Fruit count.
     *
     * @param batch the ingredients used to cook the Jam
     * @return a new Jam with its own fruit count
     */
    @Override
    protected ConsumableItem createDish(CookingBatch batch) {
        return new Jam(batch.count(IngredientCategory.FRUIT));
    }
}