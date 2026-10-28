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
 * Tests Harvest Feast consumption and permanent statistic increases.
 */
class HarvestFeastTest {

    /**
     * Checks that consumption increases both maximum statistics by one,
     * restores their current values and removes the consumed Feast.
     *
     * @param initialHealth the health before consumption
     * @param initialStamina the stamina before consumption
     */
    @ParameterizedTest(
            name = "Initial health {0}, initial stamina {1}")
    @CsvSource({
            "1, 0",
            "50, 10",
            "100, 20"
    })
    void consumptionIncreasesMaximumsAndFullyRestoresStats(
            int initialHealth, int initialStamina) {

        // Arrange
        Farmer farmer = new Farmer("Test");

        farmer.modifyStatistic(
                ActorStatistics.HEALTH,
                StatisticOperations.UPDATE,
                initialHealth);

        farmer.modifyStatistic(
                ActorStatistics.STAMINA,
                StatisticOperations.UPDATE,
                initialStamina);

        int maximumHealthBefore =
                farmer.getMaximumStatistic(ActorStatistics.HEALTH);

        int maximumStaminaBefore =
                farmer.getMaximumStatistic(ActorStatistics.STAMINA);

        List<Item> originalInventory =
                List.copyOf(farmer.getInventory().getItems());

        HarvestFeast feast = new HarvestFeast();
        farmer.getInventory().add(feast);

        // Act
        feast.consume(farmer);

        // Assert
        int expectedMaximumHealth = maximumHealthBefore + 1;
        int expectedMaximumStamina = maximumStaminaBefore + 1;

        assertEquals(
                expectedMaximumHealth,
                farmer.getMaximumStatistic(ActorStatistics.HEALTH),
                "Maximum health should increase by one");

        assertEquals(
                expectedMaximumStamina,
                farmer.getMaximumStatistic(ActorStatistics.STAMINA),
                "Maximum stamina should increase by one");

        assertEquals(
                expectedMaximumHealth,
                farmer.getStatistic(ActorStatistics.HEALTH),
                "Current health should reach its new maximum");

        assertEquals(
                expectedMaximumStamina,
                farmer.getStatistic(ActorStatistics.STAMINA),
                "Current stamina should reach its new maximum");

        assertEquals(
                originalInventory,
                farmer.getInventory().getItems(),
                "Consumption should remove the Feast and preserve other items");
    }
}