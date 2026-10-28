package game.recipes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actors.Farmer;
import game.cooking.CookingContext;
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
 * Tests Jam's minimum Fruit requirement, ingredient selection
 * and dish creation.
 */
class JamRecipeTest {

    /**
     * Checks failure without Fruit and successful use of every Fruit,
     * while preserving unrelated items and cooking stamina.
     *
     * @param fruitCount the number of carried Fruit ingredients
     * @param shouldCook whether the supplied ingredients permit cooking
     */
    @ParameterizedTest(
            name = "{0} Fruit: cooking succeeds = {1}")
    @CsvSource({
            "0, false",
            "1, true",
            "5, true"
    })
    void cookingRequiresFruitAndPreservesOtherItems(
            int fruitCount, boolean shouldCook) {

        // Arrange
        Farmer farmer = new Farmer("Test");
        int initialStamina = 10;

        farmer.modifyStatistic(
                ActorStatistics.STAMINA,
                StatisticOperations.UPDATE,
                initialStamina);

        farmer.getInventory().add(new Egg());
        farmer.getInventory().add(new Parsnip());

        List<Item> preservedItems =
                List.copyOf(farmer.getInventory().getItems());

        for (int i = 0; i < fruitCount; i++) {
            farmer.getInventory().add(new Melon());
        }

        List<ConsumableItem> originalConsumables = List.copyOf(
                farmer.getInventory().getItemsAs(ConsumableItem.class));

        Location stationLocation = mock(Location.class);
        RandomGenerator random = mock(RandomGenerator.class);

        if (shouldCook) {
            when(stationLocation.getExits()).thenReturn(List.of());
            when(random.nextDouble()).thenReturn(0.99);
        }

        CookingContext context =
                new CookingContext(farmer, stationLocation, random);

        JamRecipe recipe = new JamRecipe();

        // Act
        String result = recipe.cook(context);

        // Assert
        assertEquals(
                initialStamina,
                farmer.getStatistic(ActorStatistics.STAMINA),
                "Cooking should not restore or deduct stamina");

        if (!shouldCook) {
            assertEquals(
                    preservedItems,
                    farmer.getInventory().getItems(),
                    "Cooking without Fruit should preserve all items and create no dish");

            assertTrue(
                    result.contains("missing required ingredients"),
                    "The result should explain why cooking failed");

            verifyNoInteractions(random, stationLocation);
            return;
        }

        List<ConsumableItem> createdDishes =
                farmer.getInventory().getItemsAs(ConsumableItem.class);

        createdDishes.removeAll(originalConsumables);

        assertEquals(
                1,
                createdDishes.size(),
                "Cooking should create exactly one new consumable dish");

        ConsumableItem jam = createdDishes.get(0);

        assertEquals(
                "Jam",
                jam.toString(),
                "The newly created dish should be Jam");

        List<Item> expectedInventory = new ArrayList<>(preservedItems);
        expectedInventory.add(jam);

        assertEquals(
                expectedInventory,
                farmer.getInventory().getItems(),
                "Cooking should replace all Fruit with Jam and preserve other items");

        // Consume the created dish to check its stored Fruit count.
        jam.consume(farmer);

        assertEquals(
                initialStamina + fruitCount,
                farmer.getStatistic(ActorStatistics.STAMINA),
                "The created Jam should restore stamina equal to its Fruit count");

        assertEquals(
                preservedItems,
                farmer.getInventory().getItems(),
                "Eating the Jam should leave only the preserved items");
    }
}