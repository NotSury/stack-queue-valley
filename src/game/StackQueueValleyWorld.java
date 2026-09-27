package game;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.World;
import game.actors.Chicken;
import game.actors.Cow;
import game.actors.Farmer;
import game.grounds.*;

import java.util.Arrays;
import java.util.List;

/**
 * Represents the game world for Stack-Queue Valley.
 */
public class StackQueueValleyWorld extends World {

    /**
     * Constructs the Stack-Queue Valley world.
     *
     * @param display the Display that will display this World.
     */
    public StackQueueValleyWorld(Display display) {
        super(display);
    }

    /**
     * Initializes the game world, including the farm map and Farmer.
     *
     * @throws Exception if the world cannot be initialized
     */
    public void initialise() throws Exception {
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        groundCreator.registerGround('.', Dirt::new);
        groundCreator.registerGround('P', ParsnipCrop::new);
        groundCreator.registerGround('T', TomatoCrop::new);
        groundCreator.registerGround('J', PreservesJar::new);
        groundCreator.registerGround('R', DryingRack::new);

        List<String> inheritedFarm = Arrays.asList(
                "..P....T..",
                "..........",
                "......P...",
                "...T......",
                "....J.R...",
                ".P........",
                ".......T.."
        );

        GameMap inheritedFarmMap = new GameMap("Inherited Farm", groundCreator, inheritedFarm);
        this.addGameMap(inheritedFarmMap);

        Farmer farmer = new Farmer("Bob");
        this.addPlayer(farmer, inheritedFarmMap.at(5, 3));

        Cow cow = new Cow();
        inheritedFarmMap.addActor(cow, inheritedFarmMap.at(0, 1));

        Chicken chicken = new Chicken();
        inheritedFarmMap.addActor(chicken, inheritedFarmMap.at(7, 4));
    }
}
