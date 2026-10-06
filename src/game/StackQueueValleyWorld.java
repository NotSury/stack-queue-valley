package game;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.World;
import game.actors.*;
import game.grounds.*;
import game.items.Geode;
import game.items.Melon;

import java.util.Arrays;
import java.util.List;
/**
 * StackQueueValleyWorld represents the game world for Stack-Queue Valley.
 *
 * <p>The world initialises the farm map, places the Farmer, crops,
 * farm animals, and processing facilities, and prepares the game
 * environment before the game begins.</p>
 *
 * <p>Additional areas and features like the Mines map, mining resources, teleportation
 * points and other REQ2 elements are also initialised as port of the game world.</p>
 *
 * @author <Lee Le Si> <34599223>
 * @author <Chin Kai Xuan> <35730374>
 * @author <Khoo Lynn> <34598901>
 * @version 1.3.0
 */
public class StackQueueValleyWorld extends World {

    /**
     * Constructor.
     *
     * @param display the Display that will display this World.
     */
    public StackQueueValleyWorld(Display display) {
        super(display);
    }
    /**
     * Initialises the game world.
     *
     * <p>This method creates the farm map, places the Farmer and farm animals,
     * adds mature crops, and places the available processing facilities.</p>
     *
     * <p>It also creates the Mines map and places mining-related grounds and items
     * required for REQ 2.</p>
     *
     * @throws Exception if an error occurs while initialising the game world
     */
    public void initialise() throws Exception {
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        groundCreator.registerGround('.', Dirt::new);

        List<String> inheritedFarm = Arrays.asList(
                "..........",
                "..........",
                "..........",
                "..........",
                "..........",
                "..........",
                ".........."
        );

        List<String> mines = Arrays.asList(
                "..........",
                "..........",
                "..........",
                "..........",
                "..........",
                "..........",
                ".........."
        );

        GameMap inheritedFarmMap = new GameMap("Inherited Farm", groundCreator, inheritedFarm);
        this.addGameMap(inheritedFarmMap);

        GameMap minesMap = new GameMap("Mines", groundCreator, mines);
        this.addGameMap(minesMap);

        inheritedFarmMap.at(4, 3).setGround(new ParsnipCrop());
        inheritedFarmMap.at(6, 3).setGround(new TomatoCrop());
        inheritedFarmMap.at(3,2).setGround(new ParsnipCrop());
        inheritedFarmMap.at(8, 2).setGround(new TomatoCrop());

        inheritedFarmMap.at(2, 3).setGround(new StrawberryCrop());
        inheritedFarmMap.at(7, 3).setGround(new CauliflowerCrop());
        inheritedFarmMap.at(5, 1).setGround(new MelonCrop());

        inheritedFarmMap.at(9,0).addActor(new Bat());
        inheritedFarmMap.at(0,6).addActor(new Slime());
        inheritedFarmMap.at(9,6).addActor(new CaveGhost());
        inheritedFarmMap.at(5,4).addItem(new Melon());

        minesMap.at(2,1).setGround(new Rock());
        minesMap.at(7,2).setGround(new Rock());
        minesMap.at(3,5).setGround(new Rock());

        minesMap.at(5,2).setGround(new CopperNode());
        minesMap.at(7,5).setGround(new CopperNode());

        minesMap.at(1,4).addItem(new Geode());
        minesMap.at(6,4).addItem(new Geode());

        Farmer farmer = new Farmer("Bob");
        this.addPlayer(farmer, inheritedFarmMap.at(5, 3));

        Cow cow = new Cow();
        Chicken chicken = new Chicken();

        Merchant merchant = new Merchant();
        inheritedFarmMap.at(6, 2).addActor(merchant);

        inheritedFarmMap.at(1,1).addActor(cow);
        inheritedFarmMap.at(4,5).addActor(chicken);

        inheritedFarmMap.at(6,5).setGround(new PreservesJar());
        inheritedFarmMap.at(5,6).setGround(new DryingRack());

    }
}
