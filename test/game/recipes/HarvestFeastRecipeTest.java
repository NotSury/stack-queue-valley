package game.recipes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actors.Farmer;
import game.capabilities.IngredientCategory;
import game.cooking.CookingContext;
import game.interfaces.Ingredient;
import game.interfaces.PlantableGround;
import game.items.ConsumableItem;
import game.items.Egg;
import game.items.Melon;
import game.items.Parsnip;
import java.util.List;
import java.util.random.RandomGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;

/**
 * Tests Harvest Feast cooking at its ingredient quantity boundary.
 */
class HarvestFeastRecipeTest {

    /**
     * Checks category-specific quantities and separate dish creation.
     *
     * @param fruitCount the number of Fruit ingredients
     * @param vegetableCount the number of Vegetable ingredients
     * @param animalProductCount the number of Animal Product ingredients
     * @param shouldCook whether the supplied ingredients permit cooking
     */
    @ParameterizedTest(
            name = "{0} Fruit, {1} Vegetables, {2} Animal Products: success = {3}")
    @CsvSource({
            "6, 5, 4, false",
            "5, 5, 5, true",
            "6, 6, 6, true"
    })
    void cookingRespectsQuantitiesAndCreatesSeparateDishes(
            int fruitCount,
            int vegetableCount,
            int animalProductCount,
            boolean shouldCook) {

        // Arrange
        Farmer farmer = new Farmer("Test");
        int initialStamina = 10;

        farmer.modifyStatistic(
                ActorStatistics.STAMINA,
                StatisticOperations.UPDATE,
                initialStamina);

        List<Item> unrelatedItems =
                List.copyOf(farmer.getInventory().getItems());

        for (int i = 0; i < fruitCount; i++) {
            farmer.getInventory().add(new Melon());
        }

        for (int i = 0; i < vegetableCount; i++) {
            farmer.getInventory().add(new Parsnip());
        }

        for (int i = 0; i < animalProductCount; i++) {
            farmer.getInventory().add(new Egg());
        }

        List<Item> originalInventory =
                List.copyOf(farmer.getInventory().getItems());

        List<ConsumableItem> originalConsumables = List.copyOf(
                farmer.getInventory().getItemsAs(ConsumableItem.class));

        Location stationLocation = mock(Location.class);
        Location adjacentLocation = mock(Location.class);
        RandomGenerator random = mock(RandomGenerator.class);

        if (shouldCook) {
            Exit exit = mock(Exit.class);

            when(stationLocation.getExits()).thenReturn(List.of(exit));
            when(exit.getDestination()).thenReturn(adjacentLocation);

            when(adjacentLocation.getGroundAs(PlantableGround.class))
                    .thenReturn(null);

            when(random.nextDouble()).thenReturn(0.99);
            when(random.nextInt(1)).thenReturn(0);
        }

        CookingContext context =
                new CookingContext(farmer, stationLocation, random);

        HarvestFeastRecipe recipe = new HarvestFeastRecipe();

        // Act
        String result = recipe.cook(context);

        // Assert
        assertEquals(
                initialStamina,
                farmer.getStatistic(ActorStatistics.STAMINA),
                "Cooking should not change stamina");

        if (!shouldCook) {
            assertEquals(
                    originalInventory,
                    farmer.getInventory().getItems(),
                    "Failed cooking should preserve every inventory item");

            assertTrue(
                    result.contains("missing required ingredients"),
                    "The result should explain the missing ingredients");

            verifyNoInteractions(random, stationLocation, adjacentLocation);
            return;
        }

        assertEquals(
                fruitCount - 5,
                countIngredients(farmer, IngredientCategory.FRUIT),
                "Cooking should consume exactly five Fruit");

        assertEquals(
                vegetableCount - 5,
                countIngredients(farmer, IngredientCategory.VEGETABLE),
                "Cooking should consume exactly five Vegetables");

        assertEquals(
                animalProductCount - 5,
                countIngredients(farmer, IngredientCategory.ANIMAL_PRODUCT),
                "Cooking should consume exactly five Animal Products");

        assertTrue(
                farmer.getInventory().getItems().containsAll(unrelatedItems),
                "Cooking should preserve existing equipment");

        List<ConsumableItem> createdDishes =
                farmer.getInventory().getItemsAs(ConsumableItem.class);

        createdDishes.removeAll(originalConsumables);

        assertEquals(
                1,
                createdDishes.size(),
                "Cooking should add one fresh dish to the inventory");

        ConsumableItem inventoryFeast = createdDishes.get(0);

        assertEquals("Harvest Feast", inventoryFeast.toString());

        ArgumentCaptor<Item> droppedItem =
                ArgumentCaptor.forClass(Item.class);

        verify(adjacentLocation).addItem(droppedItem.capture());

        Item groundFeast = droppedItem.getValue();

        assertEquals("Harvest Feast", groundFeast.toString());

        assertNotSame(
                inventoryFeast,
                groundFeast,
                "The inventory Feast and ground Feast must be separate objects");

        assertEquals(
                originalInventory.size() - 15 + 1,
                farmer.getInventory().getItems().size(),
                "Cooking should consume fifteen ingredients and add one inventory dish");
    }

    /**
     * Counts carried ingredients belonging to a category.
     *
     * @param farmer the actor whose inventory is checked
     * @param category the ingredient category to count
     * @return the number of matching ingredients
     */
    private int countIngredients(
            Farmer farmer, IngredientCategory category) {

        int count = 0;

        for (Ingredient ingredient :
                farmer.getInventory().getItemsAs(Ingredient.class)) {

            if (ingredient.getCategory() == category) {
                count++;
            }
        }

        return count;
    }
}