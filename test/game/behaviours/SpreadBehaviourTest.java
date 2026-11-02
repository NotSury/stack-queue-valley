package game.behaviours;

import static org.junit.jupiter.api.Assertions.*;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Dirt;
import game.grounds.Puddle;
import game.grounds.Soil;
import game.grounds.StarliteSoil;
import game.grounds.StrawberrySeedling;
import game.grounds.StrawberrySprout;
import game.interfaces.PlantFactory;
import game.support.TestMaps;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Tests the spreading behaviour of plants for REQ1.
 *
 * @author Khoo Lynn
 * @version 1.0.0
 */
class SpreadBehaviourTest {

    /**
     * Tests that a plant can spread onto ordinary Soil.
     */
    @Test
    void spreadsOntoSoil() throws GameEngineException {

        // Arrange
        Location center = createPlantLocation();
        Location destination = getAdjacentLocations(center).get(0);
        destination.setGround(new Soil());

        SpreadBehaviour behaviour = new SpreadBehaviour(
                5,
                StrawberrySeedling::new,
                new FixedRandom(0.0, 0)
        );

        // Act
        boolean result = behaviour.perform(center);

        // Assert
        assertTrue(result);
        assertInstanceOf(StrawberrySeedling.class, destination.getGround());
    }

    /**
     * Tests that a plant can spread onto Starlite Soil because
     * it implements PlantableGround.
     */
    @Test
    void spreadsOntoStarliteSoil() throws GameEngineException {

        // Arrange
        Location center = createPlantLocation();
        Location destination = getAdjacentLocations(center).get(0);
        destination.setGround(new StarliteSoil());

        SpreadBehaviour behaviour = new SpreadBehaviour(
                5,
                StrawberrySeedling::new,
                new FixedRandom(0.0, 0)
        );

        // Act
        boolean result = behaviour.perform(center);

        // Assert
        assertTrue(result);
        assertInstanceOf(StrawberrySeedling.class, destination.getGround());
    }

    /**
     * Tests that a plant cannot spread onto bare Dirt.
     */
    @Test
    void doesNotSpreadOntoBareDirt() throws GameEngineException {

        // Arrange
        Location center = createPlantLocation();

        SpreadBehaviour behaviour = new SpreadBehaviour(
                5,
                StrawberrySeedling::new,
                new FixedRandom(0.0, 0)
        );

        // Act
        boolean result = behaviour.perform(center);

        // Assert
        assertFalse(result);

        for (Location location : getAdjacentLocations(center)) {
            assertInstanceOf(Dirt.class, location.getGround());
        }
    }

    /**
     * Tests that a plant cannot spread onto Puddle.
     */
    @Test
    void doesNotSpreadOntoPuddle() throws GameEngineException {

        // Arrange
        Location center = createPlantLocation();
        Location destination = getAdjacentLocations(center).get(0);
        destination.setGround(new Puddle());

        SpreadBehaviour behaviour = new SpreadBehaviour(
                5,
                StrawberrySeedling::new,
                new FixedRandom(0.0, 0)
        );

        // Act
        boolean result = behaviour.perform(center);

        // Assert
        assertFalse(result);
        assertInstanceOf(Puddle.class, destination.getGround());
    }

    /**
     * Tests that spreading fails when no adjacent plantable
     * location is available.
     */
    @Test
    void doesNotSpreadWhenNoPlantableLocationExists()
            throws GameEngineException {

        // Arrange
        Location center = createPlantLocation();

        FixedRandom random = new FixedRandom(0.0, 0);
        AtomicInteger factoryCalls = new AtomicInteger(0);

        SpreadBehaviour behaviour = new SpreadBehaviour(
                5,
                () -> {
                    factoryCalls.incrementAndGet();
                    return new StrawberrySeedling();
                },
                random
        );

        // Act
        boolean result = behaviour.perform(center);

        // Assert
        assertFalse(result);
        assertEquals(0, factoryCalls.get());

        assertEquals(0, random.getDoubleCalls());
        assertEquals(0, random.getIntCalls());
    }

    /**
     * Tests that spreading onto multiple available Soil tiles
     * creates only one new plant.
     */
    @Test
    void spreadsOntoOnlyOneRandomAdjacentTile()
            throws GameEngineException {

        // Arrange
        Location center = createPlantLocation();
        List<Location> destinations = getAdjacentLocations(center);

        for (Location destination : destinations) {
            destination.setGround(new Soil());
        }

        AtomicInteger factoryCalls = new AtomicInteger(0);

        SpreadBehaviour behaviour = new SpreadBehaviour(
                5,
                () -> {
                    factoryCalls.incrementAndGet();
                    return new StrawberrySeedling();
                },
                new FixedRandom(0.0, 2)
        );

        // Act
        boolean result = behaviour.perform(center);

        // Assert
        assertTrue(result);
        assertEquals(1, factoryCalls.get());

        long newPlantCount = destinations.stream()
                .filter(location ->
                        location.getGround() instanceof StrawberrySeedling)
                .count();

        assertEquals(
                1,
                newPlantCount,
                "Only one adjacent tile should receive a new plant"
        );
    }

    /**
     * Tests that spreading never succeeds when the probability is 0%.
     */
    @Test
    void doesNotSpreadWithZeroProbability()
            throws GameEngineException {

        // Arrange
        Location center = createPlantLocation();
        Location destination = getAdjacentLocations(center).get(0);
        destination.setGround(new Soil());

        SpreadBehaviour behaviour = new SpreadBehaviour(
                5,
                0.0,
                StrawberrySeedling::new,
                new FixedRandom(0.0, 0)
        );

        // Act
        boolean result = behaviour.perform(center);

        // Assert
        assertFalse(result);
        assertInstanceOf(Soil.class, destination.getGround());
    }

    /**
     * Tests that spreading succeeds when the random value
     * is below the configured 10% probability.
     */
    @Test
    void spreadsWhenRandomValueIsBelowTenPercent()
            throws GameEngineException {

        // Arrange
        Location center = createPlantLocation();
        Location destination = getAdjacentLocations(center).get(0);
        destination.setGround(new Soil());

        SpreadBehaviour behaviour = new SpreadBehaviour(
                5,
                0.1,
                StrawberrySeedling::new,
                new FixedRandom(0.099, 0)
        );

        // Act
        boolean result = behaviour.perform(center);

        // Assert
        assertTrue(result);
        assertInstanceOf(StrawberrySeedling.class, destination.getGround());
    }

    /**
     * Tests that spreading fails when the random value equals
     * the configured 10% probability threshold.
     */
    @Test
    void doesNotSpreadAtTenPercentProbabilityBoundary()
            throws GameEngineException {

        // Arrange
        Location center = createPlantLocation();
        Location destination = getAdjacentLocations(center).get(0);
        destination.setGround(new Soil());

        SpreadBehaviour behaviour = new SpreadBehaviour(
                5,
                0.1,
                StrawberrySeedling::new,
                new FixedRandom(0.1, 0)
        );

        // Act
        boolean result = behaviour.perform(center);

        // Assert
        assertFalse(result);
        assertInstanceOf(Soil.class, destination.getGround());
    }

    /**
     * Tests that spreading always succeeds when the probability is 100%
     * and an adjacent plantable location exists.
     */
    @Test
    void alwaysSpreadsWithOneHundredPercentProbability()
            throws GameEngineException {

        // Arrange
        Location center = createPlantLocation();
        Location destination = getAdjacentLocations(center).get(0);
        destination.setGround(new Soil());

        FixedRandom random = new FixedRandom(0.99, 0);

        SpreadBehaviour behaviour = new SpreadBehaviour(
                5,
                1.0,
                StrawberrySeedling::new,
                random
        );

        // Act
        boolean result = behaviour.perform(center);

        // Assert
        assertTrue(result);
        assertInstanceOf(StrawberrySeedling.class, destination.getGround());

        assertEquals(
                0,
                random.getDoubleCalls(),
                "A guaranteed spreading attempt should not need a probability roll"
        );
    }

    /**
     * Tests that the spreading interval is stored correctly.
     */
    @Test
    void returnsCorrectSpreadingInterval() {

        // Arrange
        SpreadBehaviour behaviour = new SpreadBehaviour(
                10,
                StrawberrySeedling::new,
                new FixedRandom(0.0, 0)
        );

        // Act
        int interval = behaviour.getInterval();

        // Assert
        assertEquals(10, interval);
    }

    /**
     * Tests that a zero or negative spreading interval is rejected.
     */
    @Test
    void rejectsInvalidSpreadingInterval() {

        // Arrange & Act & Assert
        assertAll(
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> new SpreadBehaviour(
                                0,
                                StrawberrySeedling::new,
                                new FixedRandom(0.0, 0)
                        )
                ),

                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> new SpreadBehaviour(
                                -1,
                                StrawberrySeedling::new,
                                new FixedRandom(0.0, 0)
                        )
                )
        );
    }

    /**
     * Tests that probabilities outside the range of 0 to 1
     * and NaN are rejected.
     */
    @Test
    void rejectsInvalidSpreadingProbabilities() {

        // Arrange & Act & Assert
        assertAll(
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> new SpreadBehaviour(
                                5,
                                -0.01,
                                StrawberrySeedling::new,
                                new FixedRandom(0.0, 0)
                        )
                ),

                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> new SpreadBehaviour(
                                5,
                                1.01,
                                StrawberrySeedling::new,
                                new FixedRandom(0.0, 0)
                        )
                ),

                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> new SpreadBehaviour(
                                5,
                                Double.NaN,
                                StrawberrySeedling::new,
                                new FixedRandom(0.0, 0)
                        )
                )
        );
    }

    /**
     * Tests that null factory and random dependencies are rejected.
     */
    @Test
    void rejectsNullDependencies() {

        // Arrange
        FixedRandom random = new FixedRandom(0.0, 0);

        // Act & Assert
        assertAll(
                () -> assertThrows(
                        NullPointerException.class,
                        () -> new SpreadBehaviour(
                                5,
                                (PlantFactory) null,
                                random
                        )
                ),

                () -> assertThrows(
                        NullPointerException.class,
                        () -> new SpreadBehaviour(
                                5,
                                StrawberrySeedling::new,
                                null
                        )
                )
        );
    }

    /**
     * Tests that spreading fails safely when the factory
     * unexpectedly returns null.
     */
    @Test
    void rejectsNullPlantCreatedByFactory()
            throws GameEngineException {

        // Arrange
        Location center = createPlantLocation();
        Location destination = getAdjacentLocations(center).get(0);
        destination.setGround(new Soil());

        SpreadBehaviour behaviour = new SpreadBehaviour(
                5,
                () -> null,
                new FixedRandom(0.0, 0)
        );

        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> behaviour.perform(center)
        );

        assertInstanceOf(
                Soil.class,
                destination.getGround(),
                "The destination should remain unchanged"
        );
    }

    /**
     * Creates a test map with a Strawberry Sprout at its centre.
     *
     * @return location containing the Strawberry Sprout
     * @throws GameEngineException if the test map cannot be created
     */
    private Location createPlantLocation() throws GameEngineException {

        Location location = TestMaps.dirtMap().at(4, 4);

        location.setGround(new StrawberrySprout(new Random(0)));

        return location;
    }

    /**
     * Gets all adjacent locations surrounding the supplied location.
     *
     * @param center the centre location
     * @return list of adjacent locations
     */
    private List<Location> getAdjacentLocations(Location center) {

        return center.getExits().stream()
                .map(Exit::getDestination)
                .collect(Collectors.toList());
    }

    /**
     * Provides predictable random values for deterministic unit tests.
     *
     * @author Khoo Lynn
     * @version 1.0.0
     */
    private static class FixedRandom extends Random {

        private final double doubleValue;
        private final int intValue;

        private int doubleCalls;
        private int intCalls;

        /**
         * Creates a random generator with fixed return values.
         *
         * @param doubleValue value returned by nextDouble()
         * @param intValue value returned by nextInt()
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
            doubleCalls++;
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
            intCalls++;
            return Math.floorMod(intValue, bound);
        }

        /**
         * Returns the number of times nextDouble() was called.
         *
         * @return number of double calls
         */
        int getDoubleCalls() {
            return doubleCalls;
        }

        /**
         * Returns the number of times nextInt() was called.
         *
         * @return number of integer calls
         */
        int getIntCalls() {
            return intCalls;
        }
    }
}