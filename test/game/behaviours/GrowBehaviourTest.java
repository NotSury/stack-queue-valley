package game.behaviours;

import static org.junit.jupiter.api.Assertions.*;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Dirt;
import game.grounds.StrawberrySeedling;
import game.grounds.StrawberrySprout;
import game.interfaces.PlantFactory;
import game.support.TestMaps;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tests the growth behaviour of plants for REQ1.
 *
 * @author Khoo Lynn
 * @version 1.0.0
 */
class GrowBehaviourTest {

    /**
     * Tests that growth succeeds when the random value
     * is below the configured probability.
     */
    @Test
    void growthSucceedsWhenRandomValueIsBelowProbability()
            throws GameEngineException {

        // Arrange
        Location location = createLocation();

        GrowBehaviour behaviour = new GrowBehaviour(
                5,
                0.5,
                StrawberrySprout::new,
                new FixedRandom(0.49)
        );

        // Act
        boolean result = behaviour.perform(location);

        // Assert
        assertTrue(result);

        assertInstanceOf(
                StrawberrySprout.class,
                location.getGround()
        );
    }

    /**
     * Tests that growth fails when the random value
     * equals the configured probability.
     */
    @Test
    void growthFailsAtProbabilityBoundary()
            throws GameEngineException {

        // Arrange
        Location location = createLocation();
        Dirt originalGround = (Dirt) location.getGround();

        GrowBehaviour behaviour = new GrowBehaviour(
                5,
                0.5,
                StrawberrySprout::new,
                new FixedRandom(0.5)
        );

        // Act
        boolean result = behaviour.perform(location);

        // Assert
        assertFalse(result);
        assertSame(originalGround, location.getGround());
    }

    /**
     * Tests that growth fails when the random value
     * is greater than the configured probability.
     */
    @Test
    void growthFailsWhenRandomValueExceedsProbability()
            throws GameEngineException {

        // Arrange
        Location location = createLocation();

        GrowBehaviour behaviour = new GrowBehaviour(
                5,
                0.5,
                StrawberrySprout::new,
                new FixedRandom(0.9)
        );

        // Act
        boolean result = behaviour.perform(location);

        // Assert
        assertFalse(result);
        assertInstanceOf(Dirt.class, location.getGround());
    }

    /**
     * Tests that growth never succeeds when the probability is 0%.
     */
    @Test
    void growthFailsWithZeroProbability()
            throws GameEngineException {

        // Arrange
        Location location = createLocation();

        GrowBehaviour behaviour = new GrowBehaviour(
                5,
                0.0,
                StrawberrySprout::new,
                new FixedRandom(0.0)
        );

        // Act
        boolean result = behaviour.perform(location);

        // Assert
        assertFalse(result);
        assertInstanceOf(Dirt.class, location.getGround());
    }

    /**
     * Tests that growth always succeeds when the probability is 100%.
     */
    @Test
    void growthSucceedsWithOneHundredPercentProbability()
            throws GameEngineException {

        // Arrange
        Location location = createLocation();

        GrowBehaviour behaviour = new GrowBehaviour(
                5,
                1.0,
                StrawberrySprout::new,
                new FixedRandom(0.999)
        );

        // Act
        boolean result = behaviour.perform(location);

        // Assert
        assertTrue(result);
        assertInstanceOf(StrawberrySprout.class, location.getGround());
    }

    /**
     * Tests that the next-stage factory is not called
     * when the growth probability check fails.
     */
    @Test
    void factoryIsNotCalledWhenGrowthFails()
            throws GameEngineException {

        // Arrange
        Location location = createLocation();
        AtomicInteger factoryCalls = new AtomicInteger(0);

        GrowBehaviour behaviour = new GrowBehaviour(
                5,
                0.5,
                () -> {
                    factoryCalls.incrementAndGet();
                    return new StrawberrySprout();
                },
                new FixedRandom(0.9)
        );

        // Act
        boolean result = behaviour.perform(location);

        // Assert
        assertFalse(result);
        assertEquals(0, factoryCalls.get());
        assertInstanceOf(Dirt.class, location.getGround());
    }

    /**
     * Tests that the next-stage factory is called exactly once
     * when growth succeeds.
     */
    @Test
    void factoryIsCalledExactlyOnceWhenGrowthSucceeds()
            throws GameEngineException {

        // Arrange
        Location location = createLocation();
        AtomicInteger factoryCalls = new AtomicInteger(0);

        GrowBehaviour behaviour = new GrowBehaviour(
                5,
                0.5,
                () -> {
                    factoryCalls.incrementAndGet();
                    return new StrawberrySprout();
                },
                new FixedRandom(0.0)
        );

        // Act
        boolean result = behaviour.perform(location);

        // Assert
        assertTrue(result);
        assertEquals(1, factoryCalls.get());
    }

    /**
     * Tests that advanceGrowth() immediately advances the plant
     * without checking its normal growth probability.
     */
    @Test
    void advanceGrowthIgnoresGrowthProbability()
            throws GameEngineException {

        // Arrange
        Location location = createLocation();
        FixedRandom random = new FixedRandom(0.0);

        GrowBehaviour behaviour = new GrowBehaviour(
                5,
                0.0,
                StrawberrySprout::new,
                random
        );

        // Act
        boolean result = behaviour.advanceGrowth(location);

        // Assert
        assertTrue(result);

        assertInstanceOf(
                StrawberrySprout.class,
                location.getGround()
        );

        assertEquals(
                0,
                random.getCalls(),
                "advanceGrowth() should not perform a probability check"
        );
    }

    /**
     * Tests that advanceGrowth() replaces the existing ground
     * with the plant created by the factory.
     */
    @Test
    void advanceGrowthReplacesCurrentGround()
            throws GameEngineException {

        // Arrange
        Location location = createLocation();

        StrawberrySeedling nextStage = new StrawberrySeedling();

        GrowBehaviour behaviour = new GrowBehaviour(
                5,
                0.5,
                () -> nextStage,
                new FixedRandom(0.9)
        );

        // Act
        boolean result = behaviour.advanceGrowth(location);

        // Assert
        assertTrue(result);
        assertSame(nextStage, location.getGround());
    }

    /**
     * Tests that the configured growth interval is returned correctly.
     */
    @Test
    void returnsCorrectGrowthInterval() {

        // Arrange
        GrowBehaviour behaviour = new GrowBehaviour(
                10,
                0.5,
                StrawberrySprout::new,
                new FixedRandom(0.0)
        );

        // Act
        int interval = behaviour.getInterval();

        // Assert
        assertEquals(10, interval);
    }

    /**
     * Tests that zero and negative growth intervals are rejected.
     */
    @Test
    void rejectsInvalidGrowthIntervals() {

        // Arrange, Act and Assert
        assertAll(
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> new GrowBehaviour(
                                0,
                                0.5,
                                StrawberrySprout::new,
                                new FixedRandom(0.0)
                        )
                ),

                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> new GrowBehaviour(
                                -1,
                                0.5,
                                StrawberrySprout::new,
                                new FixedRandom(0.0)
                        )
                )
        );
    }

    /**
     * Tests that probabilities outside the range of 0 to 1
     * and NaN are rejected.
     */
    @Test
    void rejectsInvalidGrowthProbabilities() {

        // Arrange, Act and Assert
        assertAll(
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> new GrowBehaviour(
                                5,
                                -0.01,
                                StrawberrySprout::new,
                                new FixedRandom(0.0)
                        )
                ),

                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> new GrowBehaviour(
                                5,
                                1.01,
                                StrawberrySprout::new,
                                new FixedRandom(0.0)
                        )
                ),

                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> new GrowBehaviour(
                                5,
                                Double.NaN,
                                StrawberrySprout::new,
                                new FixedRandom(0.0)
                        )
                )
        );
    }

    /**
     * Tests that null factory and random dependencies are rejected.
     */
    @Test
    void rejectsNullDependencies() {

        // Arrange, Act and Assert
        assertAll(
                () -> assertThrows(
                        NullPointerException.class,
                        () -> new GrowBehaviour(
                                5,
                                0.5,
                                (PlantFactory) null,
                                new FixedRandom(0.0)
                        )
                ),

                () -> assertThrows(
                        NullPointerException.class,
                        () -> new GrowBehaviour(
                                5,
                                0.5,
                                StrawberrySprout::new,
                                null
                        )
                )
        );
    }

    /**
     * Tests that a factory returning null is rejected
     * without replacing the existing ground.
     */
    @Test
    void rejectsNullNextGrowthStage()
            throws GameEngineException {

        // Arrange
        Location location = createLocation();
        Dirt originalGround = (Dirt) location.getGround();

        GrowBehaviour behaviour = new GrowBehaviour(
                5,
                1.0,
                () -> null,
                new FixedRandom(0.0)
        );

        // Act and Assert
        assertThrows(
                NullPointerException.class,
                () -> behaviour.advanceGrowth(location)
        );

        assertSame(
                originalGround,
                location.getGround(),
                "The existing ground should remain unchanged"
        );
    }

    /**
     * Creates a test map and returns its central location.
     *
     * @return the central location containing Dirt
     * @throws GameEngineException if the test map cannot be created
     */
    private Location createLocation() throws GameEngineException {
        return TestMaps.dirtMap().at(4, 4);
    }

    /**
     * Provides predictable random values for deterministic unit tests.
     */
    private static class FixedRandom extends Random {

        private final double value;
        private int calls;

        /**
         * Creates a random generator with a fixed double value.
         *
         * @param value value returned by nextDouble()
         */
        FixedRandom(double value) {
            this.value = value;
        }

        /**
         * Returns the configured random value.
         *
         * @return fixed random value
         */
        @Override
        public double nextDouble() {
            calls++;
            return value;
        }

        /**
         * Returns the number of random calls.
         *
         * @return number of calls
         */
        int getCalls() {
            return calls;
        }
    }
}