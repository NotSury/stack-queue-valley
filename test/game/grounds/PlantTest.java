package game.grounds;

import static org.junit.jupiter.api.Assertions.*;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Parsnip;
import game.items.Strawberry;
import game.items.Tomato;
import game.support.TestMaps;
import org.junit.jupiter.api.Test;

import java.util.Random;

/**
 * Tests the automatic growth, spreading and produce behaviours
 * of plants for REQ1.
 *
 * @author Khoo Lynn
 * @version 1.0.0
 */
class PlantTest {

    /**
     * Tests that a Strawberry Seedling does not grow before 5 turns.
     */
    @Test
    void strawberrySeedlingDoesNotGrowBeforeFifthTurn()
            throws GameEngineException {

        // Arrange
        Location location = TestMaps.dirtMap().at(4, 4);
        StrawberrySeedling seedling =
                new StrawberrySeedling(new FixedRandom(0.0, 0));

        location.setGround(seedling);

        // Act
        tick(location, 4);

        // Assert
        assertSame(seedling, location.getGround());
        assertEquals(4, seedling.getAge());
    }

    /**
     * Tests that a Strawberry Seedling grows into a Strawberry Sprout
     * after 5 turns when its growth probability succeeds.
     */
    @Test
    void strawberrySeedlingGrowsOnFifthTurn()
            throws GameEngineException {

        // Arrange
        Location location = TestMaps.dirtMap().at(4, 4);

        StrawberrySeedling seedling =
                new StrawberrySeedling(new FixedRandom(0.0, 0));

        location.setGround(seedling);

        // Act
        tick(location, 5);

        // Assert
        StrawberrySprout sprout =
                assertInstanceOf(StrawberrySprout.class, location.getGround());

        assertNotSame(seedling, sprout);
        assertEquals(0, sprout.getAge(),
                "The new growth stage should start at age 0");
    }

    /**
     * Tests that a failed Strawberry Seedling growth attempt
     * is retried at the next multiple of 5 turns.
     */
    @Test
    void strawberrySeedlingRetriesGrowthAfterFailure()
            throws GameEngineException {

        // Arrange
        Location location = TestMaps.dirtMap().at(4, 4);

        StrawberrySeedling seedling =
                new StrawberrySeedling(new SequenceRandom(0.5, 0.0));

        location.setGround(seedling);

        // Act
        tick(location, 5);

        // First attempt fails at age 5.
        assertSame(seedling, location.getGround());

        tick(location, 5);

        // Assert
        assertInstanceOf(
                StrawberrySprout.class,
                location.getGround(),
                "The seedling should retry growth at age 10"
        );
    }

    /**
     * Tests that a Strawberry Sprout spreads a Strawberry Seedling
     * onto adjacent Soil after 5 turns.
     */
    @Test
    void strawberrySproutSpreadsToAdjacentSoil()
            throws GameEngineException {

        // Arrange
        Location plantLocation = TestMaps.dirtMap().at(4, 4);
        Location destination =
                plantLocation.getExits().get(0).getDestination();

        plantLocation.setGround(
                new StrawberrySprout(new FixedRandom(0.0, 0))
        );

        destination.setGround(new Soil());

        // Act
        tick(plantLocation, 5);

        // Assert
        assertInstanceOf(
                StrawberrySeedling.class,
                destination.getGround()
        );

        assertInstanceOf(
                StrawberrySprout.class,
                plantLocation.getGround()
        );
    }

    /**
     * Tests that a Strawberry Sprout cannot spread onto Dirt or Puddle.
     */
    @Test
    void strawberrySproutCannotSpreadToDirtOrPuddle()
            throws GameEngineException {

        // Arrange
        Location plantLocation = TestMaps.dirtMap().at(4, 4);

        Location destination =
                plantLocation.getExits().get(0).getDestination();

        plantLocation.setGround(
                new StrawberrySprout(new FixedRandom(0.0, 0))
        );

        destination.setGround(new Puddle());

        // Act
        tick(plantLocation, 5);

        // Assert
        assertInstanceOf(
                StrawberrySprout.class,
                plantLocation.getGround()
        );

        assertInstanceOf(Puddle.class, destination.getGround());

        assertTrue(
                plantLocation.getExits().stream()
                        .noneMatch(exit ->
                                exit.getDestination().getGround()
                                        instanceof StrawberrySeedling),
                "The Strawberry Sprout should not spread onto non-plantable ground"
        );
    }

    /**
     * Tests that a Strawberry Sprout spreads to only one adjacent
     * Soil tile during a single turn.
     */
    @Test
    void strawberrySproutSpreadsToOnlyOneAdjacentTile()
            throws GameEngineException {

        // Arrange
        Location plantLocation = TestMaps.dirtMap().at(4, 4);

        Location firstDestination =
                plantLocation.getExits().get(0).getDestination();

        Location secondDestination =
                plantLocation.getExits().get(1).getDestination();

        plantLocation.setGround(
                new StrawberrySprout(new FixedRandom(0.0, 0))
        );

        firstDestination.setGround(new Soil());
        secondDestination.setGround(new Soil());

        // Act
        tick(plantLocation, 5);

        // Assert
        int seedlingCount =
                (firstDestination.getGround() instanceof StrawberrySeedling ? 1 : 0)
                        + (secondDestination.getGround() instanceof StrawberrySeedling ? 1 : 0);

        assertEquals(
                1,
                seedlingCount,
                "Only one new Seedling should be created"
        );
    }

    /**
     * Tests that Strawberry Sprouts can spread onto another ground
     * implementation that supports PlantableGround.
     */
    @Test
    void strawberrySproutCanSpreadToOtherPlantableGround()
            throws GameEngineException {

        // Arrange
        Location plantLocation = TestMaps.dirtMap().at(4, 4);

        Location destination =
                plantLocation.getExits().get(0).getDestination();

        plantLocation.setGround(
                new StrawberrySprout(new FixedRandom(0.0, 0))
        );

        destination.setGround(new StarliteSoil());

        // Act
        tick(plantLocation, 5);

        // Assert
        assertInstanceOf(
                StrawberrySeedling.class,
                destination.getGround(),
                "Spreading should support any PlantableGround implementation"
        );
    }

    /**
     * Tests that Strawberry Sprout growth has priority over spreading
     * when both behaviours are due on turn 10.
     */
    @Test
    void strawberrySproutGrowthHasPriorityOverSpreading()
            throws GameEngineException {

        // Arrange
        Location plantLocation = TestMaps.dirtMap().at(4, 4);

        Location destination =
                plantLocation.getExits().get(0).getDestination();

        plantLocation.setGround(
                new StrawberrySprout(new FixedRandom(0.0, 0))
        );

        // No adjacent Soil during the first 5 turns.
        tick(plantLocation, 5);

        destination.setGround(new Soil());

        // Act
        tick(plantLocation, 5);

        // Assert
        assertInstanceOf(
                StrawberryCrop.class,
                plantLocation.getGround(),
                "Growth should happen before spreading"
        );

        assertInstanceOf(
                Soil.class,
                destination.getGround(),
                "Spreading should not happen after successful growth"
        );
    }

    /**
     * Tests that Strawberry Sprout can spread when its higher-priority
     * growth attempt fails.
     */
    @Test
    void strawberrySproutCanSpreadWhenGrowthAttemptFails()
            throws GameEngineException {

        // Arrange
        Location plantLocation = TestMaps.dirtMap().at(4, 4);

        Location destination =
                plantLocation.getExits().get(0).getDestination();

        plantLocation.setGround(
                new StrawberrySprout(new FixedRandom(0.5, 0))
        );

        // Prevent spreading on turn 5.
        tick(plantLocation, 5);

        destination.setGround(new Soil());

        // Act
        tick(plantLocation, 5);

        // Assert
        assertInstanceOf(
                StrawberrySprout.class,
                plantLocation.getGround(),
                "The plant should remain a Sprout after failed growth"
        );

        assertInstanceOf(
                StrawberrySeedling.class,
                destination.getGround(),
                "Lower-priority spreading should happen after growth fails"
        );
    }

    /**
     * Tests that a Cauliflower Sprout grows after 15 turns
     * when its random value is below 25 percent.
     */
    @Test
    void cauliflowerSproutGrowsOnFifteenthTurn()
            throws GameEngineException {

        // Arrange
        Location location = TestMaps.dirtMap().at(4, 4);

        location.setGround(
                new CauliflowerSprout(new FixedRandom(0.249, 0))
        );

        // Act
        tick(location, 15);

        // Assert
        assertInstanceOf(
                CauliflowerCrop.class,
                location.getGround()
        );
    }

    /**
     * Tests that Cauliflower Sprout growth fails when its random
     * value equals the 25 percent probability threshold.
     */
    @Test
    void cauliflowerSproutDoesNotGrowAtProbabilityBoundary()
            throws GameEngineException {

        // Arrange
        Location location = TestMaps.dirtMap().at(4, 4);

        location.setGround(
                new CauliflowerSprout(new FixedRandom(0.25, 0))
        );

        // Act
        tick(location, 15);

        // Assert
        assertInstanceOf(
                CauliflowerSprout.class,
                location.getGround()
        );
    }

    /**
     * Tests that a Melon Sprout spreads another Melon Sprout
     * onto adjacent Soil after 10 turns.
     */
    @Test
    void melonSproutSpreadsOnTenthTurn()
            throws GameEngineException {

        // Arrange
        Location plantLocation = TestMaps.dirtMap().at(4, 4);

        Location destination =
                plantLocation.getExits().get(0).getDestination();

        plantLocation.setGround(
                new MelonSprout(new FixedRandom(0.0, 0))
        );

        destination.setGround(new Soil());

        // Act
        tick(plantLocation, 10);

        // Assert
        assertInstanceOf(
                MelonSprout.class,
                destination.getGround()
        );
    }

    /**
     * Tests that a Melon Sprout grows into a Melon Crop
     * after 15 turns when its growth probability succeeds.
     */
    @Test
    void melonSproutGrowsOnFifteenthTurn()
            throws GameEngineException {

        // Arrange
        Location location = TestMaps.dirtMap().at(4, 4);

        location.setGround(
                new MelonSprout(new FixedRandom(0.249, 0))
        );

        // Act
        tick(location, 15);

        // Assert
        assertInstanceOf(
                MelonCrop.class,
                location.getGround()
        );
    }

    /**
     * Tests that Melon Sprout growth fails when its random
     * value equals the 25 percent probability threshold.
     */
    @Test
    void melonSproutDoesNotGrowAtProbabilityBoundary()
            throws GameEngineException {

        // Arrange
        Location location = TestMaps.dirtMap().at(4, 4);

        location.setGround(
                new MelonSprout(new FixedRandom(0.25, 0))
        );

        // Act
        tick(location, 15);

        // Assert
        assertInstanceOf(
                MelonSprout.class,
                location.getGround()
        );
    }

    /**
     * Tests that Melon Sprout growth has priority over spreading
     * when both behaviours are due on turn 30.
     */
    @Test
    void melonSproutGrowthHasPriorityOverSpreading()
            throws GameEngineException {

        // Arrange
        Location plantLocation = TestMaps.dirtMap().at(4, 4);

        Location destination =
                plantLocation.getExits().get(0).getDestination();

        plantLocation.setGround(
                new MelonSprout(new SequenceRandom(0.9, 0.0))
        );

        // No Soil is available for the spreading attempts.
        tick(plantLocation, 20);

        // Growth fails at turn 15, then succeeds at turn 30.
        destination.setGround(new Soil());

        // Act
        tick(plantLocation, 10);

        // Assert
        assertInstanceOf(
                MelonCrop.class,
                plantLocation.getGround(),
                "Growth should have priority over spreading"
        );

        assertInstanceOf(
                Soil.class,
                destination.getGround(),
                "The plant should not also spread on the same turn"
        );
    }

    /**
     * Tests that a Strawberry Crop produces a Strawberry
     * on an adjacent tile every 5 turns.
     */
    @Test
    void strawberryCropProducesEveryFiveTurns()
            throws GameEngineException {

        // Arrange
        Location plantLocation = TestMaps.dirtMap().at(4, 4);

        Location destination =
                plantLocation.getExits().get(0).getDestination();

        plantLocation.setGround(
                new StrawberryCrop(new FixedRandom(0.0, 0))
        );

        // Act
        tick(plantLocation, 4);

        // Assert
        assertEquals(0, countItems(destination, Strawberry.class));

        // Fifth turn
        tick(plantLocation, 1);

        assertEquals(
                1,
                countItems(destination, Strawberry.class)
        );

        assertInstanceOf(
                Dirt.class,
                destination.getGround(),
                "Produce should be dropped on an adjacent tile without changing its ground"
        );
    }

    /**
     * Tests that a Tomato Crop produces a Tomato
     * on an adjacent tile every 7 turns.
     */
    @Test
    void tomatoCropProducesEverySevenTurns()
            throws GameEngineException {

        // Arrange
        Location plantLocation = TestMaps.dirtMap().at(4, 4);

        Location destination =
                plantLocation.getExits().get(0).getDestination();

        plantLocation.setGround(
                new TomatoCrop(new FixedRandom(0.0, 0))
        );

        // Act
        tick(plantLocation, 6);

        // Assert
        assertEquals(0, countItems(destination, Tomato.class));

        // Seventh turn
        tick(plantLocation, 1);

        assertEquals(
                1,
                countItems(destination, Tomato.class)
        );
    }

    /**
     * Tests that a Parsnip Crop produces a Parsnip
     * on an adjacent tile every 15 turns.
     */
    @Test
    void parsnipCropProducesEveryFifteenTurns()
            throws GameEngineException {

        // Arrange
        Location plantLocation = TestMaps.dirtMap().at(4, 4);

        Location destination =
                plantLocation.getExits().get(0).getDestination();

        plantLocation.setGround(
                new ParsnipCrop(new FixedRandom(0.0, 0))
        );

        // Act
        tick(plantLocation, 14);

        // Assert
        assertEquals(0, countItems(destination, Parsnip.class));

        // Fifteenth turn
        tick(plantLocation, 1);

        assertEquals(
                1,
                countItems(destination, Parsnip.class)
        );
    }

    /**
     * Tests that a Cauliflower Crop does not perform
     * any automatic growth, spreading or produce behaviour.
     */
    @Test
    void cauliflowerCropDoesNotPerformAutomaticBehaviour()
            throws GameEngineException {

        // Arrange
        Location location = TestMaps.dirtMap().at(4, 4);

        CauliflowerCrop crop = new CauliflowerCrop();
        location.setGround(crop);

        // Act
        tick(location, 30);

        // Assert
        assertSame(
                crop,
                location.getGround(),
                "Cauliflower Crop should remain unchanged"
        );

        assertEquals(30, crop.getAge());
    }

    /**
     * Tests that a Melon Crop spreads a Melon Sprout
     * onto adjacent Soil every 20 turns.
     */
    @Test
    void melonCropSpreadsEveryTwentyTurns()
            throws GameEngineException {

        // Arrange
        Location plantLocation = TestMaps.dirtMap().at(4, 4);

        Location destination =
                plantLocation.getExits().get(0).getDestination();

        plantLocation.setGround(
                new MelonCrop(new FixedRandom(0.0, 0))
        );

        destination.setGround(new Soil());

        // Act
        tick(plantLocation, 19);

        // Assert
        assertInstanceOf(Soil.class, destination.getGround());

        // Twentieth turn
        tick(plantLocation, 1);

        assertInstanceOf(
                MelonSprout.class,
                destination.getGround()
        );
    }

    /**
     * Tests that a Tomato Crop successfully spreads
     * when its random value is below the 10 percent chance.
     */
    @Test
    void tomatoCropSpreadsWhenChanceSucceeds()
            throws GameEngineException {

        // Arrange
        Location plantLocation = TestMaps.dirtMap().at(4, 4);

        Location destination =
                plantLocation.getExits().get(0).getDestination();

        plantLocation.setGround(
                new TomatoCrop(new FixedRandom(0.099, 0))
        );

        destination.setGround(new Soil());

        // Act
        tick(plantLocation, 19);

        // Assert
        assertInstanceOf(Soil.class, destination.getGround());

        // Twentieth turn
        tick(plantLocation, 1);

        assertInstanceOf(
                TomatoCrop.class,
                destination.getGround()
        );
    }

    /**
     * Tests that Tomato Crop spreading fails when its random value
     * equals the 10 percent probability threshold.
     */
    @Test
    void tomatoCropDoesNotSpreadAtProbabilityBoundary()
            throws GameEngineException {

        // Arrange
        Location plantLocation = TestMaps.dirtMap().at(4, 4);

        Location destination =
                plantLocation.getExits().get(0).getDestination();

        plantLocation.setGround(
                new TomatoCrop(new FixedRandom(0.1, 0))
        );

        destination.setGround(new Soil());

        // Act
        tick(plantLocation, 20);

        // Assert
        assertInstanceOf(
                Soil.class,
                destination.getGround(),
                "Spreading should fail at the exact 10 percent threshold"
        );
    }

    /**
     * Tests that Tomato Crop production has priority over spreading
     * when both behaviours are due on turn 140.
     */
    @Test
    void tomatoCropProductionHasPriorityOverSpreading()
            throws GameEngineException {

        // Arrange
        Location plantLocation = TestMaps.dirtMap().at(4, 4);

        Location destination =
                plantLocation.getExits().get(0).getDestination();

        plantLocation.setGround(
                new TomatoCrop(new FixedRandom(0.0, 0))
        );

        // No Soil is available during earlier spreading attempts.
        tick(plantLocation, 139);

        // Make the destination plantable just before turn 140.
        destination.setGround(new Soil());

        long tomatoesBefore =
                countItems(destination, Tomato.class);

        // Act
        tick(plantLocation, 1);

        // Assert
        assertEquals(
                tomatoesBefore + 1,
                countItems(destination, Tomato.class),
                "Tomato production should happen on turn 140"
        );

        assertInstanceOf(
                Soil.class,
                destination.getGround(),
                "Tomato Crop should not also spread on the same turn"
        );
    }

    /**
     * Advances the ground lifecycle by the specified number of turns.
     *
     * @param location location containing the plant
     * @param turns number of turns to advance
     */
    private void tick(Location location, int turns) {
        for (int i = 0; i < turns; i++) {
            location.getGround().tick(location);
        }
    }

    /**
     * Counts items of a specified type at a location.
     *
     * @param location location containing the items
     * @param itemType type of item to count
     * @return number of matching items
     */
    private long countItems(Location location, Class<?> itemType) {
        return location.getItems().stream()
                .filter(itemType::isInstance)
                .count();
    }

    /**
     * Provides fixed random values for deterministic unit tests.
     *
     * @author Khoo Lynn
     * @version 1.0.0
     */
    private static class FixedRandom extends Random {

        private final double doubleValue;
        private final int intValue;

        /**
         * Creates a random generator with fixed values.
         *
         * @param doubleValue fixed value returned by nextDouble()
         * @param intValue fixed value returned by nextInt()
         */
        FixedRandom(double doubleValue, int intValue) {
            this.doubleValue = doubleValue;
            this.intValue = intValue;
        }

        /**
         * Returns the configured random double.
         *
         * @return fixed random double
         */
        @Override
        public double nextDouble() {
            return doubleValue;
        }

        /**
         * Returns a predictable integer within the specified bound.
         *
         * @param bound upper bound
         * @return fixed random integer
         */
        @Override
        public int nextInt(int bound) {
            return Math.floorMod(intValue, bound);
        }
    }

    /**
     * Provides a sequence of random doubles for testing repeated attempts.
     *
     * @author Khoo Lynn
     * @version 1.0.0
     */
    private static class SequenceRandom extends Random {

        private final double[] values;
        private int index;

        /**
         * Creates a random generator with a predefined sequence.
         *
         * @param values random values returned in order
         */
        SequenceRandom(double... values) {
            this.values = values;
            this.index = 0;
        }

        /**
         * Returns the next configured random double.
         *
         * @return next random double
         */
        @Override
        public double nextDouble() {
            if (index >= values.length) {
                throw new IllegalStateException(
                        "No configured random values remain."
                );
            }

            return values[index++];
        }

        /**
         * Returns zero as the predictable random integer.
         *
         * @param bound upper bound
         * @return zero
         */
        @Override
        public int nextInt(int bound) {
            return 0;
        }
    }
}