package game.actions;

import static org.junit.jupiter.api.Assertions.*;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actors.Farmer;
import game.capabilities.ToolAbility;
import game.grounds.CauliflowerCrop;
import game.grounds.Dirt;
import game.grounds.Puddle;
import game.grounds.StrawberryCrop;
import game.items.Strawberry;
import game.items.WateringCan;
import game.support.TestMaps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the WateringAction behaviour for REQ1.
 *
 * @author Khoo Lynn
 * @version 1.0.0
 */
class WateringActionTest {

    private GameMap map;
    private Location targetLocation;
    private Farmer farmer;
    private Dirt dirt;

    /**
     * Creates a fresh test map and places a Farmer next to Dirt
     * before each test.
     *
     * @throws GameEngineException if the test map cannot be created
     */
    @BeforeEach
    void setUp() throws GameEngineException {

        // Arrange
        map = TestMaps.dirtMap();

        Location farmerLocation = map.at(4, 4);
        targetLocation = map.at(4, 5);

        farmer = new Farmer("Test");
        map.addActor(farmer, farmerLocation);

        dirt = assertInstanceOf(Dirt.class, targetLocation.getGround());
    }

    /**
     * Tests that watering Dirt succeeds with exactly 2 stamina.
     */
    @Test
    void wateringDirtSucceedsWithExactlyTwoStamina() {

        // Arrange
        setStamina(2);
        WateringAction action = new WateringAction(dirt, targetLocation);

        // Act
        action.execute(farmer, map);

        // Assert
        assertInstanceOf(Puddle.class, targetLocation.getGround());
        assertEquals(0, getStamina());
    }

    /**
     * Tests that watering Dirt succeeds when the Farmer
     * has more than the required stamina.
     */
    @Test
    void wateringDirtSucceedsWithMoreThanRequiredStamina() {

        // Arrange
        setStamina(5);
        WateringAction action = new WateringAction(dirt, targetLocation);

        // Act
        action.execute(farmer, map);

        // Assert
        assertInstanceOf(Puddle.class, targetLocation.getGround());
        assertEquals(3, getStamina());
    }

    /**
     * Tests that watering Dirt fails when the Farmer
     * has only 1 stamina.
     */
    @Test
    void wateringDirtFailsWithOneStamina() {

        // Arrange
        setStamina(1);
        WateringAction action = new WateringAction(dirt, targetLocation);

        // Act
        action.execute(farmer, map);

        // Assert
        assertInstanceOf(Dirt.class, targetLocation.getGround());
        assertEquals(1, getStamina());
    }

    /**
     * Tests that watering Dirt fails when the Farmer
     * has 0 stamina.
     */
    @Test
    void wateringDirtFailsWithZeroStamina() {

        // Arrange
        setStamina(0);
        WateringAction action = new WateringAction(dirt, targetLocation);

        // Act
        action.execute(farmer, map);

        // Assert
        assertInstanceOf(Dirt.class, targetLocation.getGround());
        assertEquals(0, getStamina());
    }

    /**
     * Tests that watering fails when the Farmer does not
     * have a Watering Can.
     */
    @Test
    void wateringFailsWithoutWateringCan() {

        // Arrange
        setStamina(5);
        removeWateringCan();

        assertFalse(farmer.hasAbility(ToolAbility.WATERING));

        WateringAction action = new WateringAction(dirt, targetLocation);

        // Act
        String result = action.execute(farmer, map);

        // Assert
        assertInstanceOf(Dirt.class, targetLocation.getGround());
        assertEquals(5, getStamina());
        assertTrue(result.contains("does not have a watering tool"));
    }

    /**
     * Tests that watering a Strawberry Crop costs exactly
     * 1 stamina and produces two Strawberries.
     */
    @Test
    void wateringStrawberryCropSucceedsWithExactlyOneStamina() {

        // Arrange
        setStamina(1);

        StrawberryCrop crop = new StrawberryCrop();
        targetLocation.setGround(crop);

        WateringAction action = new WateringAction(crop, targetLocation);

        // Act
        action.execute(farmer, map);

        // Assert
        assertSame(crop, targetLocation.getGround());
        assertEquals(0, getStamina());

        assertEquals(
                2,
                countAdjacentStrawberries(),
                "Watering Strawberry Crop should produce exactly two Strawberries"
        );
    }

    /**
     * Tests that watering a Strawberry Crop fails when
     * the Farmer has insufficient stamina.
     */
    @Test
    void wateringStrawberryCropFailsWithZeroStamina() {

        // Arrange
        setStamina(0);

        StrawberryCrop crop = new StrawberryCrop();
        targetLocation.setGround(crop);

        WateringAction action = new WateringAction(crop, targetLocation);

        // Act
        action.execute(farmer, map);

        // Assert
        assertSame(crop, targetLocation.getGround());
        assertEquals(0, getStamina());

        assertEquals(
                0,
                countAdjacentStrawberries(),
                "No Strawberries should appear when watering fails"
        );
    }

    /**
     * Tests that a Cauliflower Crop becomes harvestable
     * after being watered five times.
     */
    @Test
    void cauliflowerCropBecomesHarvestableAfterFiveWaterings() {

        // Arrange
        setStamina(10);

        CauliflowerCrop crop = new CauliflowerCrop();
        targetLocation.setGround(crop);

        WateringAction action = new WateringAction(crop, targetLocation);

        assertFalse(
                crop.canHarvest(farmer, targetLocation),
                "Cauliflower should not be harvestable initially"
        );

        // Act
        for (int i = 0; i < 4; i++) {
            action.execute(farmer, map);
        }

        // Assert - boundary before the fifth watering
        assertFalse(
                crop.canHarvest(farmer, targetLocation),
                "Cauliflower should not be harvestable after four waterings"
        );

        // Fifth watering
        action.execute(farmer, map);

        assertTrue(
                crop.canHarvest(farmer, targetLocation),
                "Cauliflower should become harvestable after five waterings"
        );

        assertEquals(0, getStamina());
    }

    /**
     * Tests that watering a Cauliflower Crop fails
     * when the Farmer has only 1 stamina.
     */
    @Test
    void wateringCauliflowerCropFailsWithOneStamina() {

        // Arrange
        setStamina(1);

        CauliflowerCrop crop = new CauliflowerCrop();
        targetLocation.setGround(crop);

        WateringAction action = new WateringAction(crop, targetLocation);

        // Act
        action.execute(farmer, map);

        // Assert
        assertFalse(
                crop.canHarvest(farmer, targetLocation),
                "Failed watering should not increase the watering count"
        );

        assertEquals(1, getStamina());
    }

    /**
     * Tests that the WateringAction displays the correct menu description.
     */
    @Test
    void wateringActionHasCorrectMenuDescription() {

        // Arrange
        WateringAction action = new WateringAction(dirt, targetLocation);

        // Act
        String description = action.menuDescription(farmer);

        // Assert
        assertEquals(
                farmer + " waters " + dirt,
                description
        );
    }

    /**
     * Sets the Farmer's stamina to the specified value.
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
     * Removes the Watering Can from the Farmer's inventory.
     */
    private void removeWateringCan() {

        WateringCan wateringCan = farmer.getInventory().getItems()
                .stream()
                .filter(WateringCan.class::isInstance)
                .map(WateringCan.class::cast)
                .findFirst()
                .orElseThrow();

        farmer.getInventory().remove(wateringCan);
    }

    /**
     * Counts all Strawberries on the adjacent tiles of the target crop.
     *
     * @return total number of adjacent Strawberries
     */
    private long countAdjacentStrawberries() {
        return targetLocation.getExits().stream()
                .flatMap(exit -> exit.getDestination().getItems().stream())
                .filter(Strawberry.class::isInstance)
                .count();
    }
}