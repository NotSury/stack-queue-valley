package game.actions;

import static org.junit.jupiter.api.Assertions.*;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actors.Farmer;
import game.capabilities.ToolAbility;
import game.grounds.Dirt;
import game.grounds.Soil;
import game.items.Hoe;
import game.support.TestMaps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the TillingAction behaviour for REQ1.
 *
 * @author Khoo Lynn
 * @version 1.0.0
 */
class TillingActionTest {

    private GameMap map;
    private Location location;
    private Farmer farmer;
    private Dirt dirt;

    /**
     * Creates a fresh map and places a Farmer on Dirt before each test.
     *
     * @throws GameEngineException if the test map cannot be created
     */
    @BeforeEach
    void setUp() throws GameEngineException {

        // Arrange
        map = TestMaps.dirtMap();
        location = map.at(4, 4);

        farmer = new Farmer("Test");
        map.addActor(farmer, location);

        dirt = assertInstanceOf(Dirt.class, location.getGround());
    }

    /**
     * Tests that tilling succeeds when the Farmer has exactly 2 stamina.
     */
    @Test
    void tillingSucceedsWithExactlyTwoStamina() {

        // Arrange
        setStamina(2);
        TillingAction action = new TillingAction(dirt, location);

        // Act
        String result = action.execute(farmer, map);

        // Assert
        assertInstanceOf(Soil.class, location.getGround());
        assertEquals(0, getStamina());
        assertTrue(result.contains("tills the dirt into soil"));
    }

    /**
     * Tests that tilling succeeds when the Farmer has more than 2 stamina.
     */
    @Test
    void tillingSucceedsWithMoreThanTwoStamina() {

        // Arrange
        setStamina(5);
        TillingAction action = new TillingAction(dirt, location);

        // Act
        action.execute(farmer, map);

        // Assert
        assertInstanceOf(Soil.class, location.getGround());
        assertEquals(3, getStamina());
    }

    /**
     * Tests that tilling fails when the Farmer has only 1 stamina.
     */
    @Test
    void tillingFailsWithOneStamina() {

        // Arrange
        setStamina(1);
        TillingAction action = new TillingAction(dirt, location);

        // Act
        String result = action.execute(farmer, map);

        // Assert
        assertInstanceOf(Dirt.class, location.getGround());
        assertEquals(1, getStamina());
        assertTrue(result.contains("too tired to till dirt"));
    }

    /**
     * Tests that tilling fails when the Farmer has 0 stamina.
     */
    @Test
    void tillingFailsWithZeroStamina() {

        // Arrange
        setStamina(0);
        TillingAction action = new TillingAction(dirt, location);

        // Act
        String result = action.execute(farmer, map);

        // Assert
        assertInstanceOf(Dirt.class, location.getGround());
        assertEquals(0, getStamina());
        assertTrue(result.contains("too tired to till dirt"));
    }

    /**
     * Tests that tilling fails when the Farmer does not have a Hoe.
     */
    @Test
    void tillingFailsWithoutHoe() {

        // Arrange
        setStamina(5);
        removeHoe();

        assertFalse(farmer.hasAbility(ToolAbility.TILLING));

        TillingAction action = new TillingAction(dirt, location);

        // Act
        String result = action.execute(farmer, map);

        // Assert
        assertInstanceOf(Dirt.class, location.getGround());
        assertEquals(5, getStamina());
        assertTrue(result.contains("don't have a Hoe"));
    }

    /**
     * Tests that the TillingAction displays the correct menu description.
     */
    @Test
    void tillingActionHasCorrectMenuDescription() {

        // Arrange
        TillingAction action = new TillingAction(dirt, location);

        // Act
        String description = action.menuDescription(farmer);

        // Assert
        assertTrue(description.contains("tills Dirt"));
    }

    /**
     * Sets the Farmer's stamina to a specified value.
     *
     * @param stamina the stamina value to set
     */
    private void setStamina(int stamina) {
        farmer.modifyStatistic(
                ActorStatistics.STAMINA,
                StatisticOperations.UPDATE,
                stamina
        );
    }

    /**
     * Returns the Farmer's current stamina.
     *
     * @return current stamina
     */
    private int getStamina() {
        return farmer.getStatistic(ActorStatistics.STAMINA);
    }

    /**
     * Removes the Hoe from the Farmer's inventory.
     */
    private void removeHoe() {
        Hoe hoe = (Hoe) farmer.getInventory().getItems()
                .stream()
                .filter(Hoe.class::isInstance)
                .findFirst()
                .orElseThrow();

        farmer.getInventory().remove(hoe);
    }
}