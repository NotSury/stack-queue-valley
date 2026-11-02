package game;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.World;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actors.*;
import game.behaviours.AmbushBehaviour;
import game.behaviours.AttackBehaviour;
import game.cooking.Recipe;
import game.grounds.*;
import game.items.Parsnip;
import game.items.Tomato;
import game.navigation.*;
import game.perception.EnvironmentScanner;
import game.perception.MapEnvironmentScanner;
import game.items.AlienDevice;
import game.items.Geode;
import game.items.Melon;
import game.recipes.HarvestFeastRecipe;
import game.recipes.JamRecipe;
import game.recipes.OmeletteRecipe;
import game.teleportation.TeleportDestination;

import game.api.NasaAsteroidClient;
import game.api.NasaAsteroidFeed;
import game.api.NasaAsteroidParser;
import game.api.ObservatoryScanPlanner;

import game.events.CelestialEventManager;
import game.events.MeteorShower;
import game.events.AsteroidFragment;
import game.events.CelestialStorm;

import game.interfaces.AsteroidFeed;
import game.interfaces.CelestialEvent;
import game.interfaces.CelestialReaction;

import game.reactions.CropMutationReaction;
import game.reactions.TerrainReaction;
import game.reactions.ImpactDamageReaction;
import game.reactions.ResourceReaction;
import game.reactions.GuardianReaction;
import game.companions.TimeRabbit;
import game.companions.PrismFox;
import game.companions.SanctuaryDeer;
import game.items.Strawberry;

import java.time.Clock;
import java.util.Random;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * StackQueueValleyWorld represents the game world for Stack-Queue Valley.
 *
 * <p>The world initialises the farm map, places the Farmer, crops,
 * farm animals, and processing facilities, and prepares the game
 * environment before the game begins.</p>
 *
 * <p>Additional areas and features like the Mines map, mining resources, teleportation
 * points and other REQ2 elements are also initialised as part of the game world.</p>
 *
 * @author <Lee Le Si> <34599223>
 * @author <Chin Kai Xuan> <35730374>
 * @author <Khoo Lynn> <34598901>
 * @author <Vanessa Liau Jia Yee> <34599398>
 * @author Surya Muni Peragas 35651628
 * @version 1.6.0
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
     * <p>It also creates the Mines map and places mining resources,
     * teleportation points and Alien Device required for REQ2.</p>
     *
     * @throws Exception if an error occurs while initialising the game world
     */
    public void initialise() throws Exception {
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        groundCreator.registerGround('.', Dirt::new);

        List<String> inheritedFarm = Arrays.asList(
                "....................",
                "....................",
                "....................",
                "....................",
                "....................",
                "....................",
                "...................."
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

        inheritedFarmMap.at(10, 1).setGround(new StrawberrySeedling());
        inheritedFarmMap.at(11, 1).setGround(new StrawberrySprout());
        inheritedFarmMap.at(12, 1).setGround(new CauliflowerSprout());
        inheritedFarmMap.at(13, 1).setGround(new MelonSprout());

        minesMap.at(4,1).addActor(new Bat());
        minesMap.at(2,4).addActor(new Slime());
        minesMap.at(8,3).addActor(new CaveGhost());
        inheritedFarmMap.at(5,4).addItem(new Melon());

        minesMap.at(2,1).setGround(new Rock());
        minesMap.at(7,2).setGround(new Rock());
        minesMap.at(3,5).setGround(new Rock());

        minesMap.at(5,2).setGround(new CopperNode());
        minesMap.at(7,5).setGround(new CopperNode());

        minesMap.at(1,4).addItem(new Geode());
        minesMap.at(6,4).addItem(new Geode());

        TeleportDestination farmDestination = new TeleportDestination("Inherited Farm Ladder", inheritedFarmMap.at(0,0));
        TeleportDestination minesDestination = new TeleportDestination("Mines Ladder", minesMap.at(0,0));
        TeleportDestination farmDestination2 = new TeleportDestination("Inherited Farm Ladder 2", inheritedFarmMap.at(9,4));
        TeleportDestination minesDestination2 = new TeleportDestination("Mines Ladder 2", minesMap.at(9,0));

        inheritedFarmMap.at(0,0).setGround(new Ladder(minesDestination));
        minesMap.at(0,0).setGround(new Ladder(farmDestination));
        inheritedFarmMap.at(9,4).setGround(new Ladder(minesDestination2));
        minesMap.at(9,0).setGround(new Ladder(farmDestination2));

        TeleportDestination farmCircleDestination = new TeleportDestination("Inherited Farm Circle", inheritedFarmMap.at(1,6));
        TeleportDestination minesCircleDestination = new TeleportDestination("Mines Circle", minesMap.at(8,6));
        TeleportDestination farmCircleDestination2 = new TeleportDestination("Inherited Farm Circle 2", inheritedFarmMap.at(8,6));
        TeleportDestination minesCircleDestination2 = new TeleportDestination("Mines Circle 2", minesMap.at(1,6));

        inheritedFarmMap.at(1,6).setGround(new TeleportationCircle(minesCircleDestination));
        minesMap.at(8,6).setGround(new TeleportationCircle(farmCircleDestination));
        inheritedFarmMap.at(8,6).setGround(new TeleportationCircle(minesCircleDestination2));
        minesMap.at(1,6).setGround(new TeleportationCircle(farmCircleDestination2));

        TeleportDestination farmDeviceDestination = new TeleportDestination("Farm Clearing", inheritedFarmMap.at(8,5));
        TeleportDestination minesDeviceDestination = new TeleportDestination("Mines Clearing", minesMap.at(4,3));

        AlienDevice alienDevice = new AlienDevice(Arrays.asList(farmDeviceDestination, minesDeviceDestination));

        inheritedFarmMap.at(2,6).addItem(alienDevice);

        Farmer farmer = new Farmer("Bob");
        this.addPlayer(farmer, inheritedFarmMap.at(0, 3));

// NASA data source: HTTP and JSON are kept outside gameplay logic.
        AsteroidFeed asteroidFeed = new NasaAsteroidFeed(
                new NasaAsteroidClient(),
                new NasaAsteroidParser()
        );

// The date range depends on the current game state.
        ObservatoryScanPlanner scanPlanner =
                new ObservatoryScanPlanner(Clock.systemDefaultZone());

// Celestial events are selected polymorphically.
        List<CelestialEvent> celestialEvents = List.of(
                new MeteorShower(),
                new AsteroidFragment(),
                new CelestialStorm()
        );

// Order matters: mutation happens before crater creation,
// and guardians spawn after impact damage.
        List<CelestialReaction> celestialReactions = List.of(
                new CropMutationReaction(),
                new TerrainReaction(),
                new ImpactDamageReaction(),
                new ResourceReaction(),
                new GuardianReaction()
        );

// Assemble the scanning system.
        CelestialEventManager celestialManager =
                new CelestialEventManager(
                        asteroidFeed,
                        scanPlanner,
                        celestialEvents,
                        celestialReactions,
                        new Random()
                );

// Place the Observatory near the Farmer's starting location.
        inheritedFarmMap.at(1, 3).setGround(
                new Observatory(celestialManager)
        );

        // REQ4: Magical farm companions
        TimeRabbit timeRabbit = new TimeRabbit();
        inheritedFarmMap.at(3,5).addActor(timeRabbit);

        PrismFox prismFox = new PrismFox();
        inheritedFarmMap.at(1, 4). addActor(prismFox);

        SanctuaryDeer sanctuaryDeer = new SanctuaryDeer();
        inheritedFarmMap.at(1, 2).addActor(sanctuaryDeer);

        Cow cow = new Cow();
        Chicken chicken = new Chicken();

        Merchant merchant = new Merchant();
        inheritedFarmMap.at(6, 2).addActor(merchant);

        inheritedFarmMap.at(1,1).addActor(cow);
        inheritedFarmMap.at(4,5).addActor(chicken);

        inheritedFarmMap.at(6,5).setGround(new PreservesJar());
        inheritedFarmMap.at(5,6).setGround(new DryingRack());

        List<Recipe> cookingRecipes = List.of(
                new JamRecipe(),
                new OmeletteRecipe(),
                new HarvestFeastRecipe()
        );

        inheritedFarmMap.at(2, 4).setGround(
                new CookingStation(cookingRecipes, new Random())
        );

        MovementPlanner movementPlanner = new MapMovementPlanner();

        EnvironmentScanner scanner =
                new MapEnvironmentScanner(movementPlanner);

        StatefulCreatureFactory creatureFactory =
                new StatefulCreatureFactory(scanner, movementPlanner);

        WebPlanner spiderWebPlanner = new MapWebPlanner(
                NestSpider.WEB_PLACEMENT_RADIUS
        );

        JumpPlanner spiderJumpPlanner = new MapJumpPlanner();

        Location spiderNest = minesMap.at(5, 3);
        spiderNest.setGround(new SpiderNest());

        NestSpider spider = creatureFactory.createNestSpider(
                80,
                spiderNest,
                spiderWebPlanner,
                spiderJumpPlanner
        );

        minesMap.addActor(spider, spiderNest);
        spider.initialiseState(minesMap);

        Behaviour<Actor, Action> ambushBehaviour = new AmbushBehaviour(scanner, movementPlanner, new AttackBehaviour(), CaveMimic.DETECTION_RADIUS);

        List<Item> startingBait = Arrays.asList(
                new Parsnip(),
                new Tomato(),
                new Parsnip()
        );

        CaveMimic mimic = creatureFactory.createCaveMimic(
                100,
                startingBait,
                new Geode().getDisplayChar(),
                ambushBehaviour
        );

        inheritedFarmMap.addActor(mimic, minesMap.at(1, 4));
        mimic.initialiseState(inheritedFarmMap);

    }
}
