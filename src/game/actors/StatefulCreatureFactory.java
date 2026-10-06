package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import game.behaviours.DirectionalMoveBehaviour;
import game.behaviours.FleeBehaviour;
import game.behaviours.SeekItemBehaviour;
import game.navigation.MovementPlanner;
import game.perception.EnvironmentScanner;
import game.states.AmbushState;
import game.states.DisguisedState;
import game.states.EscapeState;
import game.states.LootingState;
import game.weapons.CreatureWeapon;

import java.util.List;

/**
 * Constructs creatures and assembles their states and behaviours.
 * The supplied scanner and movement planner are shared by the states and
 * behaviours created by this factory.
 * Combat behaviour is supplied by the caller so that creature assembly
 * does not depend on a particular combat implementation.
 */
public class StatefulCreatureFactory {

    private final EnvironmentScanner scanner;
    private final MovementPlanner movementPlanner;
    private static final int MIMIC_ATTACK_DAMAGE = 10;
    private static final int MIMIC_HIT_RATE = 75;

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

        mimic.registerState(new DisguisedState(mimic, scanner, disguisedDisplayChar));

        mimic.registerState(new LootingState(mimic, scanner, itemSeekingBehaviour));

        mimic.registerState(new AmbushState(mimic, scanner, movementPlanner, ambushBehaviour));

        mimic.registerState(new EscapeState(mimic, scanner, movementPlanner, this, fleeingBehaviour));

        return mimic;
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
    public DecoyMimic createDecoy(CaveMimic original, String direction) {
        return new DecoyMimic(original, new DirectionalMoveBehaviour(movementPlanner, direction));
    }
}
