package game.behaviours;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.ProduceFactory;
import game.items.Parsnip;
import game.items.Tomato;
import game.support.TestMaps;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tests the automatic produce generation behaviour for REQ1.
 *
 * @author Khoo Lynn
 * @version 1.0.0
 */
class ProduceBehaviourTest {

    /**
     * Tests that produce is successfully dropped onto an adjacent tile.
     */
    @Test
    void producesItemOnAdjacentTile() throws GameEngineException {

        // Arrange
        Location center = createPlantLocation();
        List<Location> adjacent = getAdjacentLocations(center);

        int selectedIndex = 2;
        Location destination = adjacent.get(selectedIndex);

        Tomato tomato = new Tomato();
        FixedRandom random = new FixedRandom(selectedIndex);

        ProduceBehaviour behaviour = new ProduceBehaviour(
                5,
                () -> tomato,
                random
        );

        // Act
        boolean result = behaviour.perform(center);

        // Assert
        assertTrue(result);
        assertTrue(destination.getItems().contains(tomato));

        assertEquals(
                1,
                destination.getItems().size(),
                "Exactly one produce item should be dropped"
        );

        assertEquals(1, random.getCalls());
        assertEquals(adjacent.size(), random.getLastBound());
    }

    /**
     * Tests that only one produce item is created during one execution.
     */
    @Test
    void producesOnlyOneItemPerExecution()
            throws GameEngineException {

        // Arrange
        Location center = createPlantLocation();
        List<Location> adjacent = getAdjacentLocations(center);

        AtomicInteger factoryCalls = new AtomicInteger(0);

        ProduceBehaviour behaviour = new ProduceBehaviour(
                5,
                () -> {
                    factoryCalls.incrementAndGet();
                    return new Tomato();
                },
                new FixedRandom(0)
        );

        // Act
        boolean result = behaviour.perform(center);

        // Assert
        assertTrue(result);
        assertEquals(1, factoryCalls.get());

        long totalItems = adjacent.stream()
                .mapToLong(location -> location.getItems().size())
                .sum();

        assertEquals(
                1,
                totalItems,
                "Only one item should be produced per execution"
        );
    }

    /**
     * Tests that the behaviour can produce different item types
     * through its ProduceFactory.
     */
    @Test
    void producesItemCreatedByFactory()
            throws GameEngineException {

        // Arrange
        Location center = createPlantLocation();
        Location destination = getAdjacentLocations(center).get(0);

        Parsnip parsnip = new Parsnip();

        ProduceBehaviour behaviour = new ProduceBehaviour(
                15,
                () -> parsnip,
                new FixedRandom(0)
        );

        // Act
        boolean result = behaviour.perform(center);

        // Assert
        assertTrue(result);

        assertTrue(
                destination.getItems().contains(parsnip),
                "The item created by the factory should be dropped"
        );
    }

    /**
     * Tests that existing items on the destination tile
     * are preserved when new produce is dropped.
     */
    @Test
    void preservesExistingItemsOnDestination()
            throws GameEngineException {

        // Arrange
        Location center = createPlantLocation();
        Location destination = getAdjacentLocations(center).get(0);

        Tomato existingTomato = new Tomato();
        Parsnip newParsnip = new Parsnip();

        destination.addItem(existingTomato);

        ProduceBehaviour behaviour = new ProduceBehaviour(
                5,
                () -> newParsnip,
                new FixedRandom(0)
        );

        // Act
        boolean result = behaviour.perform(center);

        // Assert
        assertTrue(result);
        assertTrue(destination.getItems().contains(existingTomato));
        assertTrue(destination.getItems().contains(newParsnip));

        assertEquals(
                2,
                destination.getItems().size(),
                "Existing items should not be removed"
        );
    }

    /**
     * Tests that produce is not created when the location
     * has no adjacent exits.
     */
    @Test
    void doesNotProduceWhenNoAdjacentLocationsExist() {

        // Arrange
        Location location = mock(Location.class);
        when(location.getExits()).thenReturn(List.of());

        AtomicInteger factoryCalls = new AtomicInteger(0);
        FixedRandom random = new FixedRandom(0);

        ProduceBehaviour behaviour = new ProduceBehaviour(
                5,
                () -> {
                    factoryCalls.incrementAndGet();
                    return new Tomato();
                },
                random
        );

        // Act
        boolean result = behaviour.perform(location);

        // Assert
        assertFalse(result);

        assertEquals(
                0,
                factoryCalls.get(),
                "Factory should not be called when no destination exists"
        );

        assertEquals(
                0,
                random.getCalls(),
                "Random generator should not be called when no destination exists"
        );
    }

    /**
     * Tests that the behaviour returns its configured production interval.
     */
    @Test
    void returnsCorrectProductionInterval() {

        // Arrange
        ProduceBehaviour behaviour = new ProduceBehaviour(
                7,
                Tomato::new,
                new FixedRandom(0)
        );

        // Act
        int interval = behaviour.getInterval();

        // Assert
        assertEquals(7, interval);
    }

    /**
     * Tests that a zero production interval is rejected.
     */
    @Test
    void rejectsZeroProductionInterval() {

        // Arrange, Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> new ProduceBehaviour(
                        0,
                        Tomato::new,
                        new FixedRandom(0)
                )
        );
    }

    /**
     * Tests that a negative production interval is rejected.
     */
    @Test
    void rejectsNegativeProductionInterval() {

        // Arrange, Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> new ProduceBehaviour(
                        -1,
                        Tomato::new,
                        new FixedRandom(0)
                )
        );
    }

    /**
     * Tests that a null ProduceFactory is rejected.
     */
    @Test
    void rejectsNullProduceFactory() {

        // Arrange, Act and Assert
        assertThrows(
                NullPointerException.class,
                () -> new ProduceBehaviour(
                        5,
                        (ProduceFactory) null,
                        new FixedRandom(0)
                )
        );
    }

    /**
     * Tests that a null Random generator is rejected.
     */
    @Test
    void rejectsNullRandomGenerator() {

        // Arrange, Act and Assert
        assertThrows(
                NullPointerException.class,
                () -> new ProduceBehaviour(
                        5,
                        Tomato::new,
                        null
                )
        );
    }

    /**
     * Tests that a factory returning null causes an exception
     * and does not add an invalid item to the destination.
     */
    @Test
    void rejectsNullProducedItem()
            throws GameEngineException {

        // Arrange
        Location center = createPlantLocation();
        Location destination = getAdjacentLocations(center).get(0);

        ProduceBehaviour behaviour = new ProduceBehaviour(
                5,
                () -> null,
                new FixedRandom(0)
        );

        // Act and Assert
        assertThrows(
                NullPointerException.class,
                () -> behaviour.perform(center)
        );

        assertTrue(
                destination.getItems().isEmpty(),
                "No item should be added when the factory returns null"
        );
    }

    /**
     * Creates a test map and returns its central location.
     *
     * @return the central location containing Dirt
     * @throws GameEngineException if the test map cannot be created
     */
    private Location createPlantLocation() throws GameEngineException {
        return TestMaps.dirtMap().at(4, 4);
    }

    /**
     * Gets all adjacent locations surrounding the supplied location.
     *
     * @param center the central location
     * @return list of adjacent locations
     */
    private List<Location> getAdjacentLocations(Location center) {
        return center.getExits().stream()
                .map(Exit::getDestination)
                .toList();
    }

    /**
     * Provides predictable random values for unit testing.
     */
    private static class FixedRandom extends Random {

        private final int selectedIndex;
        private int calls;
        private int lastBound;

        /**
         * Creates a random generator with a fixed index.
         *
         * @param selectedIndex index to return
         */
        FixedRandom(int selectedIndex) {
            this.selectedIndex = selectedIndex;
        }

        /**
         * Returns the configured index within the specified bound.
         *
         * @param bound upper bound
         * @return selected index
         */
        @Override
        public int nextInt(int bound) {
            calls++;
            lastBound = bound;
            return Math.floorMod(selectedIndex, bound);
        }

        /**
         * Returns the number of random calls.
         *
         * @return number of calls
         */
        int getCalls() {
            return calls;
        }

        /**
         * Returns the last upper bound passed to nextInt().
         *
         * @return last random bound
         */
        int getLastBound() {
            return lastBound;
        }
    }
}