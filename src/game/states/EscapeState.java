package game.states;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.DecoyMimic;
import game.capabilities.ActorRole;
import game.interfaces.DecoyFactory;
import game.interfaces.Disguisable;
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

    private static final int MINIMUM_LOOT_DISTANCE = 0;

    private final Disguisable disguisable;
    private final int detectionRadius;

    private final MovementPlanner movementPlanner;
    private final DecoyFactory decoyFactory;

    private DecoyMimic activeDecoy;
    private GameMap decoyMap;
    private Action firstEscapeAction;

    /**
     * Creates an escape state with appearance, movement and decoy support.
     * The supplied appearance operations must operate on the owning creature.
     * The fleeing behaviour should use the same planner and detection radius.
     *
     * @param creature the actor that owns this state
     * @param scanner the scanner used to detect Farmers and portable items
     * @param movementPlanner the planner used to select escape steps
     * @param disguisable the owning creature's appearance operations
     * @param decoyFactory the collaborator used to construct decoys
     * @param fleeingBehaviour the behaviour used during ordinary escape turns
     * @param detectionRadius the maximum distance used for environmental scans
     */
    public EscapeState(
            Actor creature,
            EnvironmentScanner scanner,
            MovementPlanner movementPlanner,
            Disguisable disguisable,
            DecoyFactory decoyFactory,
            Behaviour<Actor, Action> fleeingBehaviour,
            int detectionRadius
    ) {
        super(creature, scanner, fleeingBehaviour);
        this.movementPlanner = movementPlanner;
        this.disguisable = disguisable;
        this.decoyFactory = decoyFactory;
        this.detectionRadius = detectionRadius;
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
     * Reveals the Mimic and creates a decoy on a separate escape tile.
     * Calculates the plan before applying entry effects and reserves
     * the Mimic's first movement action before the decoy can affect routing.
     * Removes any previous decoy belonging to this state.
     *
     * @param location the Mimic's location when entering this state
     * @throws IllegalStateException if the engine rejects placement
     * on the selected valid spawn tile
     */
    @Override
    public void onEnter(Location location) {
        Actor farmer = getScanner().findNearestActorWith(location, detectionRadius, ActorRole.FARMER);

        EscapePlan plan = null;

        if (farmer != null) {
            Location threatLocation = location.map().locationOf(farmer);

            plan = movementPlanner.planEscape(getCreature(), location, threatLocation);
        }

        disguisable.reveal();
        removePreviousDecoy();
        firstEscapeAction = null;

        if (plan == null) {
            return;
        }

        Exit mimicStep = plan.getMimicStep();
        Exit decoyStep = plan.getDecoyStep();

        DecoyMimic decoy = decoyFactory.createDecoy(getCreature(), decoyStep.getName());

        Location spawnLocation = decoyStep.getDestination();

        if (!spawnLocation.canActorEnter(decoy)) {
            return;
        }

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
     * Determines whether the creature continues escaping or changes state.
     * An adjacent Farmer with no available escape route causes an ambush.
     * Other detected Farmers cause the creature to remain in Escape.
     * With no detected Farmer, reachable portable loot causes Looting.
     * Otherwise, the creature returns to Disguised.
     *
     * @param location the Mimic's current location
     * @return the next state type
     */
    @Override
    public CreatureStateType determineNextState(Location location) {
        Actor farmer = getScanner().findNearestActorWith(
                location,
                detectionRadius,
                ActorRole.FARMER
        );

        if (farmer != null) {
            Location threatLocation = location.map().locationOf(farmer);

            if (GridDistance.between(location, threatLocation) == ADJACENT_RADIUS
                    && movementPlanner.findStepAway(
                    getCreature(),
                    location,
                    threatLocation
            ) == null) {
                return CreatureStateType.AMBUSH;
            }

            return CreatureStateType.ESCAPE;
        }

        Location lootLocation = getScanner().findNearestPortableItemLocation(
                getCreature(),
                location,
                MINIMUM_LOOT_DISTANCE,
                detectionRadius
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
