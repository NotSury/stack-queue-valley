package game.items;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import game.actors.Farmer;
import game.statuses.HealthOverTimeStatus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

/**
 * Tests Omelette consumption with different combinations of statuses.
 */
class OmeletteTest {

    /**
     * Checks that consumption removes all statuses and the Omelette,
     * while preserving other items and current statistics.
     *
     * @param harmfulCount the number of harmful statuses
     * @param beneficialCount the number of beneficial statuses
     */
    @ParameterizedTest(
            name = "{0} harmful and {1} beneficial statuses"
    )
    @CsvSource({
            "0, 0",
            "1, 0",
            "2, 1"
    })
    void consumptionRemovesEveryStatus(int harmfulCount, int beneficialCount) {
        // Arrange
        Farmer farmer = new Farmer("Test");

        for (int i = 0; i < harmfulCount; i++) {
            farmer.addStatus(new HealthOverTimeStatus(-5, 3));
        }

        for (int i = 0; i < beneficialCount; i++) {
            farmer.addStatus(new HealthOverTimeStatus(5, 3));
        }

        int originalHealth = farmer.getStatistic(ActorStatistics.HEALTH);

        int originalStamina = farmer.getStatistic(ActorStatistics.STAMINA);

        List<Item> originalInventory = List.copyOf(farmer.getInventory().getItems());

        Omelette omelette = new Omelette();
        farmer.getInventory().add(omelette);

        // Act
        omelette.consume(farmer);

        // Assert
        assertTrue(
                farmer.statuses().isEmpty(),
                "Eating an Omelette should remove every status"
        );

        assertEquals(
                originalInventory,
                farmer.getInventory().getItems(),
                "Consumption should remove the Omelette and preserve other items"
        );

        assertEquals(
                originalHealth,
                farmer.getStatistic(ActorStatistics.HEALTH),
                "Removing statuses should not directly change current health"
        );

        assertEquals(
                originalStamina,
                farmer.getStatistic(ActorStatistics.STAMINA),
                "Eating an Omelette should not change stamina"
        );
    }
}
