package game;

import static org.junit.jupiter.api.Assertions.*;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.grounds.CauliflowerCrop;
import game.grounds.CauliflowerSprout;
import game.grounds.MelonCrop;
import game.grounds.MelonSprout;
import game.grounds.ParsnipCrop;
import game.grounds.StrawberryCrop;
import game.grounds.StrawberrySeedling;
import game.grounds.StrawberrySprout;
import game.grounds.TomatoCrop;
import org.junit.jupiter.api.Test;

/**
 * Tests the initial plant setup on the farm map for REQ1.
 *
 * @author Khoo Lynn
 * @version 1.0.0
 */
class StackQueueValleyWorldTest {

    /**
     * Tests that the initial farm contains at least one of every
     * plant required by REQ1.
     *
     * @throws Exception if the game world cannot be initialised
     */
    @Test
    void initialFarmContainsEveryRequiredPlant() throws Exception {

        // Arrange
        TestableStackQueueValleyWorld world =
                new TestableStackQueueValleyWorld();

        // Act
        world.initialise();
        GameMap farmMap = world.getFarmMap();

        // Assert
        assertAll(
                () -> assertAtLeastOne(farmMap, StrawberrySeedling.class),
                () -> assertAtLeastOne(farmMap, StrawberrySprout.class),
                () -> assertAtLeastOne(farmMap, StrawberryCrop.class),
                () -> assertAtLeastOne(farmMap, CauliflowerSprout.class),
                () -> assertAtLeastOne(farmMap, CauliflowerCrop.class),
                () -> assertAtLeastOne(farmMap, MelonSprout.class),
                () -> assertAtLeastOne(farmMap, MelonCrop.class),
                () -> assertAtLeastOne(farmMap, TomatoCrop.class),
                () -> assertAtLeastOne(farmMap, ParsnipCrop.class)
        );
    }

    /**
     * Checks that the map contains at least one instance
     * of the specified plant type.
     *
     * @param map the farm map to inspect
     * @param plantType the required plant class
     */
    private void assertAtLeastOne(GameMap map, Class<?> plantType) {
        assertTrue(
                countPlants(map, plantType) >= 1,
                "The initial farm must contain at least one "
                        + plantType.getSimpleName()
        );
    }

    /**
     * Counts the number of ground tiles containing the specified plant.
     *
     * @param map the map to inspect
     * @param plantType the plant class to count
     * @return number of matching plants
     */
    private long countPlants(GameMap map, Class<?> plantType) {

        long count = 0;

        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {

                if (plantType.isInstance(map.at(x, y).getGround())) {
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * Exposes the initial farm map for unit testing.
     */
    private static class TestableStackQueueValleyWorld
            extends StackQueueValleyWorld {

        /**
         * Creates a testable world with a Display instance.
         */
        TestableStackQueueValleyWorld() {
            super(new Display());
        }

        /**
         * Returns the first map added during world initialisation.
         *
         * @return the inherited farm map
         */
        GameMap getFarmMap() {
            return gameMaps.get(0);
        }
    }
}