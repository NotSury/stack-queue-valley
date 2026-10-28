package game.recipes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actors.Farmer;
import game.cooking.CookingContext;
import game.items.Egg;
import game.items.Parsnip;
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

        for (int i = 0; i < eggCount; i++) {
            farmer.getInventory().add(new Egg());
        }

        for (int i = 0; i < parsnipCount; i++) {
            farmer.getInventory().add(new Parsnip());
        }

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
}