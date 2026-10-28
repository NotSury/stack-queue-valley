package game.recipes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actors.Farmer;
import game.capabilities.IngredientCategory;
import game.cooking.CookingContext;
import game.interfaces.Ingredient;
import game.items.ConsumableItem;
import game.items.Egg;
import game.items.Melon;
import game.items.Parsnip;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Tests ingredient validation and cooking behaviour for Omelette recipes.
 */
class OmeletteRecipeTest {

    /**
     * Checks that missing ingredients prevent all cooking effects
     * and leave the actor's inventory and stamina unchanged.
     *
     * @param eggCount the number of carried Eggs
     * @param parsnipCount the number of carried Parsnips
     */
    @ParameterizedTest(
            name = "{0} Eggs and {1} Parsnips: cooking should fail")
    @CsvSource({
            "0, 0",
            "1, 0",
            "0, 1"
    })
    void missingIngredientsLeaveInventoryAndStaminaUnchanged(
            int eggCount, int parsnipCount) {

        // Arrange
        Farmer farmer = new Farmer("Test");
        int initialStamina = 10;

        farmer.modifyStatistic(
                ActorStatistics.STAMINA,
                StatisticOperations.UPDATE,
                initialStamina);

        addIngredients(farmer, eggCount, parsnipCount);

        List<Item> originalInventory =
                List.copyOf(farmer.getInventory().getItems());

        Location stationLocation = mock(Location.class);
        RandomGenerator random = mock(RandomGenerator.class);

        CookingContext context =
                new CookingContext(farmer, stationLocation, random);

        OmeletteRecipe recipe = new OmeletteRecipe();

        // Act
        String result = recipe.cook(context);

        // Assert
        assertEquals(
                originalInventory,
                farmer.getInventory().getItems(),
                "Failed cooking should preserve all items and create no dish");

        assertEquals(
                initialStamina,
                farmer.getStatistic(ActorStatistics.STAMINA),
                "Failed cooking should not change stamina");

        assertTrue(
                result.contains("missing required ingredients"),
                "The result should explain why cooking failed");

        verifyNoInteractions(random, stationLocation);
    }

    /**
     * Checks that successful cooking uses exactly one ingredient
     * from each required category and preserves unrelated items.
     *
     * @param eggCount the number of carried Eggs
     * @param parsnipCount the number of carried Parsnips
     */
    @ParameterizedTest(
            name = "{0} Eggs and {1} Parsnips: use exactly one of each"
    )
    @CsvSource({
            "1, 1",
            "2, 1",
            "2, 3"
    })
    void successfulCookingUsesExactQuantities(
            int eggCount, int parsnipCount
    ) {
        // Arrange
        Farmer farmer = new Farmer("Test");
        int initialStamina = 10;

        farmer.modifyStatistic(
                ActorStatistics.STAMINA,
                StatisticOperations.UPDATE,
                initialStamina
        );

        farmer.getInventory().add(new Melon());

        List<Item> unrelatedItems = List.copyOf(farmer.getInventory().getItems());

        addIngredients(farmer, eggCount, parsnipCount);

        List<Item> inventoryBeforeCooking = List.copyOf(farmer.getInventory().getItems());

        Location stationLocation = mock(Location.class);
        RandomGenerator random = mock(RandomGenerator.class);

        when(stationLocation.getExits()).thenReturn(List.of());
        when(random.nextDouble()).thenReturn(0.99);

        CookingContext context = new CookingContext(farmer, stationLocation, random);

        OmeletteRecipe recipe = new OmeletteRecipe();

        // Act
        recipe.cook(context);

        // Assert
        assertEquals(
                eggCount - 1,
                countIngredients(farmer, IngredientCategory.ANIMAL_PRODUCT),
                "Cooking should consume exactly one Animal Product"
        );

        assertEquals(
                parsnipCount -1,
                countIngredients(farmer, IngredientCategory.VEGETABLE),
                "Cooking should consume exactly one Vegetable"
        );

        List<Item> createdDishes = new ArrayList<>(farmer.getInventory().getItemsAs(ConsumableItem.class));

        createdDishes.removeAll(inventoryBeforeCooking);

        assertEquals(
                1,
                createdDishes.size(),
                "Cooking should create exactly one new consumable dish"
        );

        assertEquals(
                "Omelette",
                createdDishes.get(0).toString(),
                "The newly created dish should be an Omelette"
        );

        assertTrue(
                farmer.getInventory().getItems().containsAll(unrelatedItems),
                "Cooking should preserve the Melon and existing equipment"
        );

        int expectedInventorySize = unrelatedItems.size() + eggCount + parsnipCount - 1;

        assertEquals(
                expectedInventorySize,
                farmer.getInventory().getItems().size(),
                "Cooking should remove two ingredients and add one dish"
        );

        assertEquals(
                initialStamina,
                farmer.getStatistic(ActorStatistics.STAMINA),
                "Cooking should not change stamina"
        );
    }

    /**
     * Adds fresh ingredients to the Farmer's inventory.
     *
     * @param farmer the actor receiving the ingredients
     * @param eggCount the number of Eggs to add
     * @param parsnipCount the number of Parsnips to add
     */
    private void addIngredients(Farmer farmer, int eggCount, int parsnipCount) {
        for (int i = 0; i < eggCount; i++) {
            farmer.getInventory().add(new Egg());
        }

        for (int i = 0; i < parsnipCount; i++) {
            farmer.getInventory().add(new Parsnip());
        }
    }

    private int countIngredients(Farmer farmer, IngredientCategory category) {
        int count = 0;

        for (Ingredient ingredient : farmer.getInventory().getItemsAs(Ingredient.class)) {
            if (ingredient.getCategory() == category) {
                count ++;
            }
        }

        return count;
    }
}