package game.states;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.CaveMimic;
import game.actors.DecoyMimic;
import game.actors.StatefulCreatureFactory;
import game.navigation.EscapePlan;
import game.navigation.GridDistance;
import game.navigation.MovementPlanner;
import game.perception.EnvironmentScanner;

/**
 * Allows a threatened Cave Mimic to flee while creating a temporary decoy.
 * Entering this state creates a decoy on an alternative escape tile when
 * one is available. The real Mimic's preferred escape tile is reserved
 * during this selection.
 * The Mimic returns to ambushing when cornered by an adjacent Farmer,
 * or starts looting when no Farmer is detected and reachable loot exists.
 */
public class EscapeState extends BehaviourDrivenState {

    private static final int ADJACENT_RADIUS = 1;
    private static final int MINIMUM_LOOT_DISTANCE = 0;

    private final CaveMimic mimic;
    private final MovementPlanner movementPlanner;
    private final StatefulCreatureFactory creatureFactory;

    private DecoyMimic activeDecoy;
    private GameMap decoyMap;
    private Action firstEscapeAction;

    /**
     * Creates an escape state for the supplied Mimic.
     * The supplied fleeing behaviour should use the same movement planner
     * and detection radius as this state's escape decisions.
     *
     * @param mimic the Mimic controlled by this state
     * @param scanner the scanner used to detect Farmers and items
     * @param movementPlanner the planner used to select escape steps
     * @param creatureFactory the factory used to construct decoys
     * @param fleeingBehaviour the behaviour used during ordinary escape turns
     */
    public EscapeState(CaveMimic mimic,
                       EnvironmentScanner scanner,
                       MovementPlanner movementPlanner,
                       StatefulCreatureFactory creatureFactory,
                       Behaviour<Actor, Action> fleeingBehaviour
    ) {
        super(mimic, scanner, fleeingBehaviour);
        this.mimic = mimic;
        this.movementPlanner = movementPlanner;
        this.creatureFactory = creatureFactory;
    }

    /**
     * Returns the type represented by this state.
     *
     * @return the escape state type
     */
    @Override
    public CreatureStateType getType() {
        return CreatureStateType.ESCAPE;
    }

    /**
     * Reveals the Mimic and creates a decoy using its prepared escape plan.
     * Removes any previous decoy and selects the real Mimic's first movement.
     * The movement is returned later as its ordinary action rather than
     * executed as part of this entry effect.
     * A complete plan must be prepared before transitioning into this state.
     *
     * @param location the Mimic's location when entering this state
     * @throws IllegalStateException if the engine rejects decoy placement
     */
    @Override
    public void onEnter(Location location) {
        EscapePlan escapePlan = mimic.takeEscapePlan();

        mimic.reveal();
        removePreviousDecoy();

        Exit mimicStep = escapePlan.getMimicStep();
        Exit decoyStep = escapePlan.getDecoyStep();

        DecoyMimic decoy = creatureFactory.createDecoy(mimic, decoyStep.getName());

        try {
            location.map().addActor(decoy, decoyStep.getDestination());
        } catch (GameEngineException exception) {
            throw new IllegalStateException(
                    "Unable to place the Mimic's decoy on its selected escape tile.",
                    exception
            );
        }

        activeDecoy = decoy;
        decoyMap = location.map();

        firstEscapeAction = new MoveActorAction(mimicStep.getDestination(), mimicStep.getName());
    }

    /**
     * Determines whether the Mimic continues escaping or changes state.
     * An adjacent Farmer with no available escape route causes an ambush.
     * With no detected Farmer, reachable portable loot causes looting.
     * Otherwise, the Mimic returns to Disguised.
     *
     * @param location the Mimic's current location
     * @return the next state type
     */
    @Override
    public CreatureStateType determineNextState(Location location) {
        Actor farmer = getScanner().findFarmer(
                location,
                CaveMimic.DETECTION_RADIUS
        );

        if (farmer != null) {
            Location threatLocation = location.map().locationOf(farmer);

            if (GridDistance.between(location, threatLocation) == ADJACENT_RADIUS
                    && movementPlanner.findStepAway(
                    mimic,
                    location,
                    threatLocation
            ) == null) {
                return CreatureStateType.AMBUSH;
            }

            return CreatureStateType.ESCAPE;
        }

        Location lootLocation = getScanner().findNearestPortableItemLocation(
                mimic,
                location,
                MINIMUM_LOOT_DISTANCE,
                CaveMimic.DETECTION_RADIUS
        );

        if (lootLocation != null) {
            return CreatureStateType.LOOTING;
        }

        return CreatureStateType.DISGUISED;
    }

    /**
     * Returns the planned movement on the first Escape turn.
     * Subsequent turns delegate to the ordinary fleeing behaviour.
     *
     * @param location the Mimic's current location
     * @return the planned first movement or an ordinary fleeing action
     */
    @Override
    public Action getAction(Location location) {
        if (firstEscapeAction != null) {
            Action action = firstEscapeAction;
            firstEscapeAction = null;
            return action;
        }

        return super.getAction(location);
    }

    private void removePreviousDecoy() {
        if (activeDecoy != null) {
            activeDecoy.vanish(decoyMap);
            activeDecoy = null;
            decoyMap = null;
        }
    }
}
