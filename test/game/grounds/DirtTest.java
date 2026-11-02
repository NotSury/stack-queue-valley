package game.grounds;

import static org.junit.jupiter.api.Assertions.*;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Farmer;
import game.actions.TillingAction;
import game.actions.WateringAction;
import game.interfaces.PlantableGround;
import game.support.TestMaps;
import org.junit.jupiter.api.Test;

/**
 * Tests the behaviour of Dirt for REQ1.
 *
 * @author Khoo Lynn
 * @version 1.0.0
 */
class DirtTest {

    /**
     * Tests that bare Dirt cannot accept planted crops.
     */
    @Test
    void bareDirtIsNotPlantable() throws GameEngineException {

        // Arrange
        GameMap map = TestMaps.dirtMap();
        Location location = map.at(4, 4);

        // Assert
        assertNull(
                location.getGroundAs(PlantableGround.class),
                "Bare Dirt should not be plantable"
        );
    }

    /**
     * Tests that tilling Dirt transforms it into plantable Soil.
     */
    @Test
    void tillingDirtCreatesPlantableSoil() throws GameEngineException {

        // Arrange
        GameMap map = TestMaps.dirtMap();
        Location location = map.at(4, 4);
        Dirt dirt = assertInstanceOf(Dirt.class, location.getGround());

        // Act
        dirt.till(location);

        // Assert
        assertInstanceOf(
                Soil.class,
                location.getGround(),
                "Tilling Dirt should transform it into Soil"
        );

        assertNotNull(
                location.getGroundAs(PlantableGround.class),
                "Soil should accept planted crops"
        );
    }

    /**
     * Tests that tilling one location does not affect neighbouring ground.
     */
    @Test
    void tillingDirtDoesNotChangeNeighbouringGround()
            throws GameEngineException {

        // Arrange
        GameMap map = TestMaps.dirtMap();

        Location target = map.at(4, 4);
        Location neighbour = map.at(4, 5);

        Dirt dirt = assertInstanceOf(Dirt.class, target.getGround());

        // Act
        dirt.till(target);

        // Assert
        assertInstanceOf(Soil.class, target.getGround());

        assertInstanceOf(
                Dirt.class,
                neighbour.getGround(),
                "Neighbouring Dirt should remain unchanged"
        );

        assertNull(
                neighbour.getGroundAs(PlantableGround.class),
                "Neighbouring Dirt should remain unplantable"
        );
    }

    /**
     * Tests that watering Dirt transforms it into a non-plantable Puddle.
     */
    @Test
    void wateringDirtCreatesNonPlantablePuddle()
            throws GameEngineException {

        // Arrange
        GameMap map = TestMaps.dirtMap();
        Location location = map.at(4, 4);

        Dirt dirt = assertInstanceOf(Dirt.class, location.getGround());
        Farmer farmer = new Farmer("Test");

        // Act
        dirt.water(farmer, location);

        // Assert
        assertInstanceOf(
                Puddle.class,
                location.getGround(),
                "Watering Dirt should transform it into a Puddle"
        );

        assertNull(
                location.getGroundAs(PlantableGround.class),
                "Puddle should not accept planted crops"
        );
    }

    /**
     * Tests that watering one location does not affect neighbouring ground.
     */
    @Test
    void wateringDirtDoesNotChangeNeighbouringGround()
            throws GameEngineException {

        // Arrange
        GameMap map = TestMaps.dirtMap();

        Location target = map.at(4, 4);
        Location neighbour = map.at(4, 5);

        Dirt dirt = assertInstanceOf(Dirt.class, target.getGround());
        Farmer farmer = new Farmer("Test");

        // Act
        dirt.water(farmer, target);

        // Assert
        assertInstanceOf(Puddle.class, target.getGround());

        assertInstanceOf(
                Dirt.class,
                neighbour.getGround(),
                "Neighbouring Dirt should remain unchanged"
        );
    }

    /**
     * Tests that watering Dirt requires exactly 2 stamina.
     */
    @Test
    void dirtHasCorrectWateringCost() {

        // Arrange
        Dirt dirt = new Dirt();

        // Act
        int wateringCost = dirt.getWateringCost();

        // Assert
        assertEquals(
                2,
                wateringCost,
                "Watering Dirt should cost 2 stamina"
        );
    }

    /**
     * Tests that Dirt offers tilling and watering actions
     * when the actor is standing directly on it.
     */
    @Test
    void dirtOffersActionsWhenActorIsStandingOnIt()
            throws GameEngineException {

        // Arrange
        GameMap map = TestMaps.dirtMap();
        Location location = map.at(4, 4);

        Dirt dirt = assertInstanceOf(Dirt.class, location.getGround());
        Farmer farmer = new Farmer("Test");

        // Act
        ActionList actions =
                dirt.allowableActions(farmer, location, "");

        // Assert
        assertEquals(2, actions.size());

        assertTrue(
                actions.getUnmodifiableActionList()
                        .stream()
                        .anyMatch(TillingAction.class::isInstance),
                "Tilling action should be available"
        );

        assertTrue(
                actions.getUnmodifiableActionList()
                        .stream()
                        .anyMatch(WateringAction.class::isInstance),
                "Watering action should be available"
        );
    }

    /**
     * Tests that Dirt does not offer actions when it is adjacent to the actor.
     */
    @Test
    void dirtDoesNotOfferActionsWhenAdjacent()
            throws GameEngineException {

        // Arrange
        GameMap map = TestMaps.dirtMap();
        Location location = map.at(4, 4);

        Dirt dirt = assertInstanceOf(Dirt.class, location.getGround());
        Farmer farmer = new Farmer("Test");

        // Act
        ActionList actions =
                dirt.allowableActions(farmer, location, "North");

        // Assert
        assertEquals(
                0,
                actions.size(),
                "Dirt should not offer actions when adjacent"
        );
    }
}