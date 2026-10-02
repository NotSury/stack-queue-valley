package game.states;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.CaveMimic;
import game.actors.DecoyMimic;
import game.actors.StatefulCreatureFactory;
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
     * Reveals the Mimic and creates a decoy on an alternative escape tile.
     * Any previous decoy belonging to this state is removed first.
     * No new decoy is created if a Farmer or two valid escape steps
     * cannot be found.
     *
     * @param location the Mimic's location when entering this state
     * @throws IllegalStateException if the engine rejects placement on the
     * selected valid spawn tile
     */
    @Override
    public void onEnter(Location location) {
        mimic.reveal();
        removePreviousDecoy();

        Actor farmer = getScanner().findFarmer(
                location,
                CaveMimic.DETECTION_RADIUS
        );

        if (farmer == null) {
            return;
        }

        Location threatLocation = location.map().locationOf(farmer);

        Exit realEscapeStep = movementPlanner.findStepAway(mimic, location, threatLocation);

        if (realEscapeStep == null) {
            return;
        }

        Exit decoyEscapeStep = movementPlanner.findStepAway(mimic, location, threatLocation, realEscapeStep);

        if (decoyEscapeStep == null) {
            return;
        }

        DecoyMimic decoy = creatureFactory.createDecoy(mimic, decoyEscapeStep.getName());

        Location spawnLocation = decoyEscapeStep.getDestination();

        if (!spawnLocation.canActorEnter(decoy)) {
            return;
        }

        try {
            location.map().addActor(decoy, spawnLocation);
        } catch (GameEngineException exception) {
            throw new IllegalStateException(
                    "Unable to place the Mimic's decoy on its selected escape tile.",
                    exception
            );
        }

        activeDecoy = decoy;
        decoyMap = location.map();
    }

    /**
     * Determines whether the Mimic continues escaping or changes state.
     * An adjacent Farmer with no available escape step causes an ambush.
     * When no Farmer is detected, reachable portable loot causes looting.
     * Otherwise, the Mimic remains in this state.
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

        return CreatureStateType.ESCAPE;
    }

    private void removePreviousDecoy() {
        if (activeDecoy != null) {
            activeDecoy.vanish(decoyMap);
            activeDecoy = null;
            decoyMap = null;
        }
    }
}
