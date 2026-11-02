package game.items;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.World;
import game.actors.Farmer;
import game.cooking.CookingContext;
import game.grounds.CookingStation;
import game.grounds.Dirt;
import game.grounds.Soil;
import java.util.List;
import java.util.random.RandomGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Tests Parsnip's cooking effect using real ground and locations.
 *
 * @author Surya Muni Peragas 35651628
 * @version 1.0.0
 */
class ParsnipCookingTest {

    /**
     * Checks that only eligible adjacent Soil becomes a Parsnip Crop.
     *
     * @param mapSize the width and height of the test map
     * @param soilPresent whether one adjacent Soil tile is provided
     * @throws GameEngineException if the test map cannot be created
     */
    @ParameterizedTest(
            name = "{0} by {0} map, adjacent Soil present = {1}")
    @CsvSource({
            "3, false",
            "3, true",
            "1, false"
    })
    void cookingEffectPlantsOnlyOnAdjacentSoil(
            int mapSize, boolean soilPresent) throws GameEngineException {

        // Arrange
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        groundCreator.registerGround('.', Dirt::new);

        GameMap map = new GameMap(
                "Parsnip test", groundCreator, '.', mapSize, mapSize);

        World world = new World(mock(Display.class)) {};
        world.addGameMap(map);

        RandomGenerator random = mock(RandomGenerator.class);

        Location stationLocation = map.at(mapSize / 2, mapSize / 2);
        stationLocation.setGround(
                new CookingStation(List.of(), random));

        if (soilPresent) {
            map.at(0, 0).setGround(new Soil());
            when(random.nextInt(1)).thenReturn(0);
        }

        Ground[][] originalGrounds = new Ground[mapSize][mapSize];

        for (int x = 0; x < mapSize; x++) {
            for (int y = 0; y < mapSize; y++) {
                originalGrounds[x][y] = map.at(x, y).getGround();
            }
        }

        Farmer farmer = new Farmer("Test");
        CookingContext context =
                new CookingContext(farmer, stationLocation, random);

        Parsnip parsnip = new Parsnip();

        // Act
        parsnip.applyCookingEffect(context);

        // Assert
        for (int x = 0; x < mapSize; x++) {
            for (int y = 0; y < mapSize; y++) {
                Ground currentGround = map.at(x, y).getGround();

                if (soilPresent && x == 0 && y == 0) {
                    assertNotSame(
                            originalGrounds[x][y],
                            currentGround,
                            "The eligible Soil should be replaced");

                    assertEquals(
                            "Parsnip Crop",
                            currentGround.toString(),
                            "The Soil should become a Parsnip Crop");
                } else {
                    assertSame(
                            originalGrounds[x][y],
                            currentGround,
                            "Ground at (" + x + ", " + y + ") should remain unchanged");
                }
            }
        }
    }
}