package game.support;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.World;
import game.grounds.Dirt;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds small, empty maps for REQ3 unit tests, so each test class sets up its
 * map the same way.
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public final class TestMaps {

    /** Width and height of every test map. */
    private static final int SIZE = 9;

    /**
     * Prevents construction of this utility class.
     */
    private TestMaps() {
    }

    /**
     * Creates a 9x9 map of Dirt attached to a world, so actors can be added to it.
     *
     * @return a new empty map
     * @throws GameEngineException if the map cannot be created
     */
    public static GameMap dirtMap() throws GameEngineException {
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        groundCreator.registerGround('.', Dirt::new);
        List<String> rows = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            rows.add(".".repeat(SIZE));
        }
        GameMap map = new GameMap("Test", groundCreator, rows);
        new World(new Display()) { }.addGameMap(map);
        return map;
    }
}