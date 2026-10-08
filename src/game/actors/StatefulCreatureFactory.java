package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.*;
import game.interfaces.DecoyFactory;
import game.interfaces.SpiderlingFactory;
import game.interfaces.WebNetwork;
import game.navigation.HazardAwareMovementPlanner;
import game.navigation.JumpPlanner;
import game.navigation.MovementPlanner;
import game.navigation.WebPlanner;
import game.perception.EnvironmentScanner;
import game.perception.MapSpiderAwareness;
import game.perception.SpiderTargetTracker;
import game.states.*;
import game.traps.SpiderWebNetwork;
import game.weapons.CreatureWeapon;

import java.util.List;


/**
 * Constructs creatures and assembles their states and behaviours.
 * The supplied scanner and movement planner are shared by the states and
 * behaviours created by this factory.
 * Combat behaviour is supplied by the caller so that creature assembly
 * does not depend on a particular combat implementation.
 *
 * @author Surya Muni Peragas
 * @author Lee Le Si 34599223
 * @version 1.1.0
 */


public class StatefulCreatureFactory implements DecoyFactory, SpiderlingFactory {

    private final EnvironmentScanner scanner;
    private final MovementPlanner movementPlanner;
    private static final int MIMIC_ATTACK_DAMAGE = 10;
    private static final int MIMIC_HIT_RATE = 75;
    private static final int DECOY_LIFETIME_TURNS = 3;

    private static final int SPIDER_ATTACK_DAMAGE = 12;
    private static final int SPIDER_HIT_RATE = 75;

    /**
     * Creates a factory using the supplied environmental collaborators.
     *
     * @param scanner the scanner used to detect Farmers and portable items
     * @param movementPlanner the planner used by creature movement behaviours
     */
    public StatefulCreatureFactory(EnvironmentScanner scanner, MovementPlanner movementPlanner) {
        this.scanner = scanner;
        this.movementPlanner = movementPlanner;
    }

    /**
     * Creates an unplaced Cave Mimic with all four states registered.
     * Item seeking and fleeing use the Mimic's shared detection radius.
     * The supplied ambush behaviour controls its ordinary ambush turns.
     * The initial state's entry effects must run after the Mimic has
     * been placed on a map, using its initialiseState method.
     *
     * @param hitPoints the Mimic's starting and maximum health
     * @param startingBait the item instances placed in its starting inventory
     * @param disguisedDisplayChar the character shown while disguised
     * @param ambushBehaviour the behaviour used during ordinary ambush turns
     * @return the Cave Mimic with its states and behaviours configured
     */
    public CaveMimic createCaveMimic(int hitPoints, List<Item> startingBait, char disguisedDisplayChar, Behaviour<Actor, Action> ambushBehaviour) {
        CaveMimic mimic = new CaveMimic(hitPoints, startingBait, new CreatureWeapon(MIMIC_ATTACK_DAMAGE, "bites", MIMIC_HIT_RATE, "fangs"));

        Behaviour<Actor, Action> itemSeekingBehaviour = new SeekItemBehaviour(scanner, movementPlanner, CaveMimic.DETECTION_RADIUS);

        Behaviour<Actor, Action> fleeingBehaviour = new FleeBehaviour(scanner, movementPlanner, CaveMimic.DETECTION_RADIUS);

        mimic.registerState(new DisguisedState(mimic, scanner, mimic, mimic, disguisedDisplayChar, CaveMimic.DETECTION_RADIUS));

        mimic.registerState(new LootingState(mimic, scanner, movementPlanner, mimic, mimic, itemSeekingBehaviour, CaveMimic.DETECTION_RADIUS));

        mimic.registerState(new AmbushState(mimic, scanner, movementPlanner, mimic, ambushBehaviour, CaveMimic.DETECTION_RADIUS));

        mimic.registerState(new EscapeState(mimic, scanner, movementPlanner, mimic, this, fleeingBehaviour, CaveMimic.DETECTION_RADIUS));

        return mimic;
    }

    /**
     * Creates an unplaced Nest Spider with four distinct states.
     * Shares one web network and target tracker across those states.
     * Initial entry effects run after placement through initialiseState.
     *
     * @param hitPoints the Spider's starting and maximum health
     * @param nestLocation the fixed nest location
     * @param webPlanner the safe walking and strategic placement planner
     * @param jumpPlanner the legal two-tile jump planner
     * @param attackBehaviour the existing adjacent attack behaviour
     * @return the assembled Nest Spider
     */
    public NestSpider createNestSpider(
            int hitPoints,
            Location nestLocation,
            WebPlanner webPlanner,
            JumpPlanner jumpPlanner,
            Behaviour<Actor, Action> attackBehaviour
    ) {
        NestSpider spider = new NestSpider(
                hitPoints,
                new CreatureWeapon(
                        SPIDER_ATTACK_DAMAGE,
                        "bites",
                        SPIDER_HIT_RATE,
                        "fangs"
                )
        );

        WebNetwork network = new SpiderWebNetwork(
                spider,
                nestLocation
        );

        SpiderTargetTracker targetTracker = new MapSpiderAwareness(
                spider,
                scanner,
                webPlanner,
                jumpPlanner,
                network,
                NestSpider.DETECTION_RADIUS,
                NestSpider.PURSUIT_RADIUS
        );

        Behaviour<Actor, Action> weavingBehaviour =
                new WeaveWebBehaviour(webPlanner, network);

        Behaviour<Actor, Action> huntingBehaviour = new SpiderHuntBehaviour(
                  targetTracker,
                  new AmbushBehaviour(
                          scanner,
                          new HazardAwareMovementPlanner(movementPlanner,webPlanner),
                          attackBehaviour,
                          NestSpider.DETECTION_RADIUS
                  )
          );

       Behaviour<Actor, Action> pounceBehaviour = new PounceBehaviour(
              network,
              jumpPlanner,
              webPlanner
       );

       spider.registerState(new NestingState(
                spider,
                scanner,
                network,
                webPlanner,
                targetTracker,
                this
        ));

        spider.registerState(new WeavingState(
                spider,
                scanner,
                network,
                targetTracker,
                weavingBehaviour
        ));

        spider.registerState(new HuntingState(
                spider,
                scanner,
                huntingBehaviour,
                targetTracker,
                network
        ));

        spider.registerState(new PouncingState(
                spider,
                scanner,
                pounceBehaviour,
                targetTracker,
                network
        ));

        return spider;
    }

    /**
     * Creates an unplaced decoy that resembles the original Mimic.
     * The decoy continues in the supplied direction during its own turns.
     * Its existing turn action manages its limited lifetime.
     *
     * @param original the Mimic whose appearance is copied
     * @param direction the direction obtained from the selected spawn exit
     * @return the newly created decoy
     */
    @Override
    public DecoyMimic createDecoy(Actor original, String direction) {
        return new DecoyMimic(original, new DirectionalMoveBehaviour(movementPlanner, direction), DECOY_LIFETIME_TURNS);
    }

    /**
     * Creates an independent Spiderling using existing NPC wandering.
     *
     * @return an unplaced, short-lived Spiderling
     */
    @Override
    public Spiderling createSpiderling() {
        return new Spiderling(new WanderBehaviour());
    }
}
