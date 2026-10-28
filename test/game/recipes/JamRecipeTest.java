package game.recipes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
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
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests ingredient selection and dish creation for Jam recipes.
 */
class JamRecipeTest {

    /**
     * Checks that Jam uses every carried Fruit, preserves unrelated
     * items and restores stamina according to its cooking batch.
     *
     * @param fruitCount the number of carried Fruit ingredients
     */
    @ParameterizedTest(name = "{0} Fruit should produce one Jam")
    @ValueSource(ints = {1, 3, 5})
    void cookingUsesAllFruitAndPreservesOtherItems(int fruitCount) {

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

        when(stationLocation.getExits()).thenReturn(List.of());
        when(random.nextDouble()).thenReturn(0.99);

        CookingContext context =
                new CookingContext(farmer, stationLocation, random);

        JamRecipe recipe = new JamRecipe();

        // Act
        recipe.cook(context);

        // Assert
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

        assertEquals(
                initialStamina,
                farmer.getStatistic(ActorStatistics.STAMINA),
                "Cooking should not restore or deduct stamina");

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