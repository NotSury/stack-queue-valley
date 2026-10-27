package game.capabilities;

/**
 * Categories used to match ingredients to recipe requirements.
 *
 * <p>Recipes depend on categories rather than particular produce classes,
 * allowing new ingredients to work with existing recipes.
 */
public enum IngredientCategory {

    /**
     * Fruit ingredients, such as Melon.
     */
    FRUIT,

    /**
     * Vegetable ingredients, such as Parsnip.
     */
    VEGETABLE,

    /**
     * Animal products, such as Egg.
     */
    ANIMAL_PRODUCT
}
