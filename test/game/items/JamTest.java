package game.items;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actors.Farmer;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Tests Jam consumption using normal and boundary stamina values.
 *
 * @author Surya Muni Peragas 35651628
 * @version 1.0.0
 */
class JamTest {

    /**
     * Checks stamina restoration and removal of the consumed Jam,
     * while preserving the actor's other inventory items.
     *
     * @param initialStamina the stamina before consumption
     * @param fruitCount the number of Fruit used to make the Jam
     * @param expectedStamina the expected stamina after consumption
     */
    @ParameterizedTest(
            name = "Initial stamina {0}, Fruit count {1}: expected stamina {2}")
    @CsvSource({
            "10, 3, 13",
            "19, 5, 20",
            "20, 2, 20"
    })
    void consumptionRestoresStaminaAndRemovesJam(
            int initialStamina,
            int fruitCount,
            int expectedStamina) {

        // Arrange
        Farmer farmer = new Farmer("Test");
        farmer.modifyStatistic(
                ActorStatistics.STAMINA,
                StatisticOperations.UPDATE,
                initialStamina);

        List<Item> originalInventory =
                List.copyOf(farmer.getInventory().getItems());

        Jam jam = new Jam(fruitCount);
        farmer.getInventory().add(jam);

        // Act
        jam.consume(farmer);

        // Assert
        assertEquals(
                expectedStamina,
                farmer.getStatistic(ActorStatistics.STAMINA),
                "Jam should restore stamina without exceeding the maximum");

        assertEquals(
                originalInventory,
                farmer.getInventory().getItems(),
                "Consumption should remove the Jam and preserve other items");
    }
}