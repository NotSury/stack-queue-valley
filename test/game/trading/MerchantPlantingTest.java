package game.trading;

import static org.junit.jupiter.api.Assertions.*;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.BuyAction;
import game.actors.Farmer;
import game.actors.Merchant;
import game.capabilities.FarmerStatistic;
import game.grounds.Dirt;
import game.grounds.ParsnipCrop;
import game.grounds.Puddle;
import game.grounds.Soil;
import game.grounds.StarliteSoil;
import game.grounds.TomatoCrop;
import game.interfaces.Buyable;
import game.support.TestMaps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the Soil-only Merchant planting restrictions introduced in REQ1.
 *
 * <p>Verifies that Tomato and Parsnip Crops can only be planted
 * on ground implementing PlantableGround, and that failed
 * planting attempts do not deduct coins.</p>
 *
 * @author Khoo Lynn
 * @version 1.0.0
 */
class MerchantPlantingTest {

    private GameMap map;
    private Farmer farmer;
    private Merchant merchant;
    private Location merchantLocation;
    private Location targetLocation;

    /**
     * Creates a fresh Dirt map with a Farmer and Merchant
     * before each test.
     *
     * @throws GameEngineException if the test map cannot be created
     */
    @BeforeEach
    void setUp() throws GameEngineException {

        map = TestMaps.dirtMap();

        merchantLocation = map.at(4, 4);
        targetLocation = map.at(4, 5);

        farmer = new Farmer("Test");
        merchant = new Merchant();

        map.addActor(merchant, merchantLocation);
        map.addActor(farmer, map.at(3, 4));
    }

    /**
     * Tests that buying a Tomato Crop fails on bare Dirt
     * without deducting coins.
     */
    @Test
    void tomatoCannotBePlantedOnBareDirt() {

        // Arrange
        int initialCoins = getCoins();

        // Act
        String result = buy(new BuyTomatoCrop());

        // Assert
        assertInstanceOf(Dirt.class, targetLocation.getGround());
        assertEquals(initialCoins, getCoins());

        assertTrue(
                result.contains("no available location"),
                "Purchase should fail when there is no Soil"
        );

        assertEquals(0, countAdjacentCrops(TomatoCrop.class));
    }

    /**
     * Tests that buying a Tomato Crop fails when
     * the only available destination is Puddle.
     */
    @Test
    void tomatoCannotBePlantedOnPuddle() {

        // Arrange
        targetLocation.setGround(new Puddle());
        int initialCoins = getCoins();

        // Act
        String result = buy(new BuyTomatoCrop());

        // Assert
        assertInstanceOf(Puddle.class, targetLocation.getGround());
        assertEquals(initialCoins, getCoins());

        assertTrue(result.contains("no available location"));
        assertEquals(0, countAdjacentCrops(TomatoCrop.class));
    }

    /**
     * Tests that a Tomato Crop can be purchased
     * and planted on adjacent Soil.
     */
    @Test
    void tomatoCanBePlantedOnSoil() {

        // Arrange
        targetLocation.setGround(new Soil());

        // Act
        String result = buy(new BuyTomatoCrop());

        // Assert
        assertInstanceOf(
                TomatoCrop.class,
                targetLocation.getGround(),
                "Tomato Crop should be planted on Soil"
        );

        assertEquals(1, countAdjacentCrops(TomatoCrop.class));
        assertFalse(result.contains("no available location"));
    }

    /**
     * Tests that a Tomato Crop can also be planted on
     * another implementation of PlantableGround.
     */
    @Test
    void tomatoCanBePlantedOnStarliteSoil() {

        // Arrange
        targetLocation.setGround(new StarliteSoil());

        // Act
        buy(new BuyTomatoCrop());

        // Assert
        assertInstanceOf(
                TomatoCrop.class,
                targetLocation.getGround()
        );

        assertEquals(1, countAdjacentCrops(TomatoCrop.class));
    }

    /**
     * Tests that buying a Parsnip Crop fails on bare Dirt
     * and refunds the purchase price.
     */
    @Test
    void parsnipCannotBePlantedOnBareDirt() {

        // Arrange
        int initialCoins = getCoins();

        // Act
        String result = buy(new BuyParsnipCrop());

        // Assert
        assertInstanceOf(Dirt.class, targetLocation.getGround());
        assertEquals(
                initialCoins,
                getCoins(),
                "Failed planting should not reduce coins"
        );

        assertTrue(result.contains("no available location"));
        assertEquals(0, countAdjacentCrops(ParsnipCrop.class));
    }

    /**
     * Tests that buying a Parsnip Crop fails when
     * the only possible destination is Puddle.
     */
    @Test
    void parsnipCannotBePlantedOnPuddle() {

        // Arrange
        targetLocation.setGround(new Puddle());
        int initialCoins = getCoins();

        // Act
        String result = buy(new BuyParsnipCrop());

        // Assert
        assertInstanceOf(Puddle.class, targetLocation.getGround());
        assertEquals(initialCoins, getCoins());

        assertTrue(result.contains("no available location"));
        assertEquals(0, countAdjacentCrops(ParsnipCrop.class));
    }

    /**
     * Tests that a Parsnip Crop can be purchased
     * and planted on adjacent Soil.
     */
    @Test
    void parsnipCanBePlantedOnSoil() {

        // Arrange
        targetLocation.setGround(new Soil());

        BuyParsnipCrop product = new BuyParsnipCrop();
        int initialCoins = getCoins();

        // Act
        String result = buy(product);

        // Assert
        assertInstanceOf(
                ParsnipCrop.class,
                targetLocation.getGround(),
                "Parsnip Crop should be planted on Soil"
        );

        assertEquals(1, countAdjacentCrops(ParsnipCrop.class));

        assertEquals(
                initialCoins - product.getBuyPrice(),
                getCoins()
        );

        assertFalse(result.contains("no available location"));
    }

    /**
     * Tests that a Parsnip Crop can also be planted
     * on Starlite Soil.
     */
    @Test
    void parsnipCanBePlantedOnStarliteSoil() {

        // Arrange
        targetLocation.setGround(new StarliteSoil());

        // Act
        buy(new BuyParsnipCrop());

        // Assert
        assertInstanceOf(
                ParsnipCrop.class,
                targetLocation.getGround()
        );

        assertEquals(1, countAdjacentCrops(ParsnipCrop.class));
    }

    /**
     * Executes a purchase through the normal BuyAction.
     *
     * @param product the crop being purchased
     * @return result of the purchase
     */
    private String buy(Buyable product) {
        BuyAction action = new BuyAction(product, merchant);
        return action.execute(farmer, map);
    }

    /**
     * Returns the Farmer's current coins.
     *
     * @return current coin balance
     */
    private int getCoins() {
        return farmer.getStatistic(FarmerStatistic.COINS);
    }

    /**
     * Counts adjacent crops of a specified type.
     *
     * @param cropType the crop class to count
     * @return number of matching crops around the Merchant
     */
    private long countAdjacentCrops(Class<?> cropType) {
        return merchantLocation.getExits().stream()
                .map(exit -> exit.getDestination().getGround())
                .filter(cropType::isInstance)
                .count();
    }
}