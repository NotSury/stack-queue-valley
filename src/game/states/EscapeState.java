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
import game.interfaces.BaitCarrier;
import game.interfaces.DecoyFactory;
import game.interfaces.Disguisable;
import game.navigation.EscapePlan;
import game.navigation.MovementPlanner;
import game.perception.EnvironmentScanner;

/**
 * Allows a threatened Cave Mimic to flee while creating a temporary decoy.
 * Entering this state creates a decoy on an alternative escape tile when
 * one is available. The real Mimic's preferred escape tile is reserved
 * during this selection.
 * The Mimic returns to ambushing when cornered by an adjacent Farmer,
 * or starts looting when no Farmer is detected and reachable loot exists.
 *
 * @author Surya Muni Peragas 35651628
 * @version 1.0.0
 */
public class EscapeState extends BehaviourDrivenState {

    private static final int MINIMUM_LOOT_DISTANCE = 0;

    private final Disguisable disguisable;
    private final int detectionRadius;
    private final BaitCarrier baitCarrier;

    private final MovementPlanner movementPlanner;
    private final DecoyFactory decoyFactory;

    private DecoyMimic activeDecoy;
    private GameMap decoyMap;
    private Action firstEscapeAction;

    /**
     * Creates an Escape state.
     * The supplied role interfaces must operate on the owning creature.
     *
     * @param creature the owning creature
     * @param scanner the environmental scanner
     * @param movementPlanner the escape-route planner
     * @param disguisable the creature's appearance operations
     * @param baitCarrier the creature's bait operations
     * @param decoyFactory the collaborator used to create decoys
     * @param fleeingBehaviour the ordinary fleeing behaviour
     * @param detectionRadius the maximum environmental scan distance
     */
    public EscapeState(
            Actor creature,
            EnvironmentScanner scanner,
            MovementPlanner movementPlanner,
            Disguisable disguisable,
            BaitCarrier baitCarrier,
            DecoyFactory decoyFactory,
            Behaviour<Actor, Action> fleeingBehaviour,
            int detectionRadius
    ) {
        super(creature, scanner, fleeingBehaviour);
        this.movementPlanner = movementPlanner;
        this.disguisable = disguisable;
        this.baitCarrier = baitCarrier;
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
     * @return a description of decoy creation or why no decoy was created
     * @throws IllegalStateException if the engine rejects placement
     * on the selected valid spawn tile
     */
    @Override
    public String onEnter(Location location) {
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
            return getCreature() + " reveals itself but cannot create a decoy";
        }

        Exit mimicStep = plan.getMimicStep();
        Exit decoyStep = plan.getDecoyStep();

        DecoyMimic decoy = decoyFactory.createDecoy(getCreature(), decoyStep.getName());

        Location spawnLocation = decoyStep.getDestination();

        if (!spawnLocation.canActorEnter(decoy)) {
            return getCreature() + " reveals itself but cannot place a decoy on the selected tile.";
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

        return getCreature() + " reveals itself and creates a decoy at ("
                + spawnLocation.x() + ", " + spawnLocation.y()
                + "), preparing to escape " + mimicStep.getName() + ".";
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

            if (canStealFrom(farmer, location)
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

        return baitCarrier.canScatterBait(location)
                ? CreatureStateType.DISGUISED
                : CreatureStateType.ESCAPE;
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
