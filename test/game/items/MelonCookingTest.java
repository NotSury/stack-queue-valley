package game.items;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actors.Farmer;
import game.companions.TimeRabbit;
import game.cooking.CookingContext;
import game.cooking.IngredientEntry;
import game.grounds.CookingStation;
import game.support.TestMaps;
import java.util.List;
import java.util.random.RandomGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Tests Melon cooking damage, lethal damage and the chance boundary.
 */
class MelonCookingTest {

    /**
     * Checks damage to adjacent actors and removal of a defeated actor.
     *
     * @param roll the controlled chance value
     * @param initialRabbitHealth the rabbit's health before cooking
     * @param expectedRabbitHealth the expected remaining rabbit health
     * @param rabbitRemains whether the rabbit should remain on the map
     * @param expectedCookHealth the expected remaining cook health
     * @throws GameEngineException if the map or actor placement fails
     */
    @ParameterizedTest(
            name = "Roll {0}, rabbit health {1}: remains on map = {3}")
    @CsvSource({
            "0.49, 10, 0, false, 85",
            "0.49, 20, 5, true, 85",
            "0.50, 10, 10, true, 100"
    })
    void cookingHandlesDamageAndUnconsciousActors(
            double roll,
            int initialRabbitHealth,
            int expectedRabbitHealth,
            boolean rabbitRemains,
            int expectedCookHealth) throws GameEngineException {

        // Arrange
        GameMap map = TestMaps.dirtMap();

        RandomGenerator random = mock(RandomGenerator.class);
        when(random.nextDouble()).thenReturn(roll);

        Location stationLocation = map.at(4, 4);
        stationLocation.setGround(
                new CookingStation(List.of(), random));

        Farmer cook = new Farmer("Test");
        map.at(4, 5).addActor(cook);

        TimeRabbit rabbit = new TimeRabbit();
        rabbit.modifyStatistic(
                ActorStatistics.HEALTH,
                StatisticOperations.UPDATE,
                initialRabbitHealth);

        map.at(3, 4).addActor(rabbit);

        Melon melon = new Melon();
        cook.getInventory().add(melon);

        CookingContext context =
                new CookingContext(cook, stationLocation, random);

        IngredientEntry entry =
                IngredientEntry.from(melon).orElseThrow();

        // Act
        entry.applyAndRemove(context);

        // Assert
        assertEquals(
                expectedRabbitHealth,
                rabbit.getStatistic(ActorStatistics.HEALTH),
                "The rabbit should receive damage only when the Melon bursts");

        assertEquals(
                rabbitRemains,
                map.contains(rabbit),
                "An unconscious rabbit should be removed from the map");

        assertEquals(
                expectedCookHealth,
                cook.getStatistic(ActorStatistics.HEALTH),
                "The adjacent cook should also receive burst damage");

        assertFalse(
                cook.getInventory().getItems().contains(melon),
                "The Melon should be consumed whether or not it bursts");
    }
}