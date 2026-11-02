package game.grounds;

import static org.junit.jupiter.api.Assertions.*;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.HarvestAction;
import game.actions.WateringAction;
import game.actors.Farmer;
import game.items.Tomato;
import game.support.TestMaps;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

/**
 * Tests plant output messages and interactions for REQ1.
 *
 * <p>Verifies that growth, spreading and production report
 * correct coordinates, inactive turns produce no output,
 * and seedlings and sprouts remain walkable without
 * watering or harvesting actions.</p>
 *
 * @author Khoo Lynn
 * @version 1.0.0
 */
class PlantOutputAndInteractionTest {

    /**
     * Tests that a successful growth message includes
     * the plant's coordinates and its next growth stage.
     *
     * @throws GameEngineException if the map cannot be created
     */
    @Test
    void growthMessageIncludesCorrectCoordinates()
            throws GameEngineException {

        // Arrange
        Location location = TestMaps.dirtMap().at(4, 4);

        location.setGround(
                new StrawberrySeedling(new FixedRandom(0.0, 0))
        );

        // Act
        String output = captureOutput(() -> tick(location, 5));

        // Assert
        assertInstanceOf(StrawberrySprout.class, location.getGround());

        assertTrue(
                output.contains("Strawberry Seedling at (4, 4)"),
                "Growth message should include the correct plant coordinates"
        );

        assertTrue(
                output.contains("Strawberry Sprout"),
                "Growth message should identify the next growth stage. Actual output: " + output
        );
    }

    /**
     * Tests that a successful spreading message includes
     * both the original plant's coordinates and the destination.
     *
     * @throws GameEngineException if the map cannot be created
     */
    @Test
    void spreadingMessageIncludesSourceAndDestinationCoordinates()
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
        String output = captureOutput(() -> tick(plantLocation, 5));

        // Assert
        assertInstanceOf(
                StrawberrySeedling.class,
                destination.getGround()
        );

        assertTrue(
                output.contains("Strawberry Sprout at (4, 4)"),
                "Spreading message should contain the source coordinates"
        );

        assertTrue(
                output.contains("spreads"),
                "Spreading message should describe the action"
        );

        assertTrue(
                output.contains(
                        "at (" + destination.x() + ", "
                                + destination.y() + ")"
                ),
                "Spreading message should contain the destination coordinates"
        );

        assertTrue(
                output.contains("Strawberry Seedling"),
                "Spreading message should identify the new plant"
        );
    }

    /**
     * Tests that a successful production message includes
     * the original crop's coordinates and produce destination.
     *
     * @throws GameEngineException if the map cannot be created
     */
    @Test
    void produceMessageIncludesCorrectCoordinates()
            throws GameEngineException {

        // Arrange
        Location plantLocation = TestMaps.dirtMap().at(4, 4);

        Location destination =
                plantLocation.getExits().get(0).getDestination();

        plantLocation.setGround(
                new TomatoCrop(new FixedRandom(0.0, 0))
        );

        // Act
        String output = captureOutput(() -> tick(plantLocation, 7));

        // Assert
        assertTrue(
                destination.getItems().stream()
                        .anyMatch(Tomato.class::isInstance),
                "A Tomato should be dropped onto the selected tile"
        );

        assertTrue(
                output.contains("Tomato Crop at (4, 4)"),
                "Produce message should contain the crop coordinates"
        );

        assertTrue(
                output.contains("drops a Tomato"),
                "Produce message should identify the produced item"
        );

        assertTrue(
                output.contains(
                        "at (" + destination.x() + ", "
                                + destination.y() + ")"
                ),
                "Produce message should contain the destination coordinates"
        );
    }

    /**
     * Tests that no message is printed when a plant does nothing.
     *
     * <p>This includes turns before the growth interval and
     * turns where a scheduled growth attempt fails.</p>
     *
     * @throws GameEngineException if the map cannot be created
     */
    @Test
    void quietTurnsProduceNoOutput()
            throws GameEngineException {

        // Arrange
        Location location = TestMaps.dirtMap().at(4, 4);

        StrawberrySeedling seedling =
                new StrawberrySeedling(new FixedRandom(0.9, 0));

        location.setGround(seedling);

        // Act
        String output = captureOutput(() -> tick(location, 10));

        // Assert
        assertSame(
                seedling,
                location.getGround(),
                "The seedling should remain when growth attempts fail"
        );

        assertEquals(
                "",
                output,
                "A plant that does nothing should produce no output"
        );

        assertEquals(10, seedling.getAge());
    }

    /**
     * Tests that a spreading plant cannot replace another plant
     * on an adjacent tile.
     *
     * @throws GameEngineException if the map cannot be created
     */
    @Test
    void plantCannotSpreadOverAnotherPlant()
            throws GameEngineException {

        // Arrange
        Location plantLocation = TestMaps.dirtMap().at(4, 4);

        Location occupiedLocation =
                plantLocation.getExits().get(0).getDestination();

        StrawberrySprout sprout =
                new StrawberrySprout(new FixedRandom(0.0, 0));

        StrawberrySeedling existingPlant =
                new StrawberrySeedling(new FixedRandom(0.0, 0));

        plantLocation.setGround(sprout);
        occupiedLocation.setGround(existingPlant);

        // All other adjacent locations remain Dirt.

        // Act
        String output = captureOutput(() -> tick(plantLocation, 5));

        // Assert
        assertSame(
                sprout,
                plantLocation.getGround(),
                "The original plant should remain unchanged"
        );

        assertSame(
                existingPlant,
                occupiedLocation.getGround(),
                "An existing plant must not be replaced by spreading"
        );

        assertEquals(
                "",
                output,
                "Failed spreading should not produce an output message"
        );
    }

    /**
     * Tests that seedlings and sprouts can be entered by the Farmer
     * but do not provide watering or harvesting actions.
     *
     * @throws GameEngineException if the map cannot be created
     */
    @Test
    void seedlingsAndSproutsAreWalkableButNotInteractable()
            throws GameEngineException {

        // Arrange
        Location location = TestMaps.dirtMap().at(4, 4);
        Farmer farmer = new Farmer("Test");

        List<Plant> stages = List.of(
                new StrawberrySeedling(),
                new StrawberrySprout(),
                new CauliflowerSprout(),
                new MelonSprout()
        );

        // Act and Assert
        for (Plant plant : stages) {

            location.setGround(plant);

            assertTrue(
                    plant.canActorEnter(farmer),
                    plant + " should be walkable"
            );

            ActionList actions =
                    plant.allowableActions(farmer, location, "North");

            assertTrue(
                    actions.getUnmodifiableActionList()
                            .stream()
                            .noneMatch(action ->
                                    action instanceof WateringAction
                                            || action instanceof HarvestAction),
                    plant + " should not provide watering or harvesting actions"
            );
        }
    }

    /**
     * Advances a plant by a specified number of turns.
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
     * Captures output printed to System.out while running an action.
     *
     * <p>The original output stream is restored after execution.</p>
     *
     * @param action operation whose output should be captured
     * @return captured output as a String
     */
    private String captureOutput(Runnable action) {

        PrintStream originalOut = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try (PrintStream capture = new PrintStream(
                buffer, true, StandardCharsets.UTF_8)) {

            System.setOut(capture);
            action.run();

        } finally {
            System.setOut(originalOut);
        }

        return buffer.toString(StandardCharsets.UTF_8).trim();
    }

    /**
     * Provides predictable random values for unit testing.
     */
    private static class FixedRandom extends Random {

        private final double doubleValue;
        private final int intValue;

        /**
         * Creates a random generator with fixed values.
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
         * @return fixed double value
         */
        @Override
        public double nextDouble() {
            return doubleValue;
        }

        /**
         * Returns a predictable random integer.
         *
         * @param bound upper bound
         * @return fixed integer within the bound
         */
        @Override
        public int nextInt(int bound) {
            return Math.floorMod(intValue, bound);
        }
    }
}