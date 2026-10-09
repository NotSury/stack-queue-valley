package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.states.CreatureState;
import game.states.CreatureStateType;

import java.util.EnumMap;
import java.util.Map;

/**
 * An actor whose turn is controlled by its current state.
 * Manages state registration, initial activation and transitions.
 * Concrete states provide transition conditions, entry effects and ordinary-turn actions.
 *
 * @author Surya Muni Peragas 35651628
 * @version 1.0.0
 */
public abstract class StatefulCreature extends Actor {

    private final Map<CreatureStateType, CreatureState> states = new EnumMap<>(CreatureStateType.class);

    private final CreatureStateType initialStateType;

    private CreatureState currentState;

    private String pendingEntryMessage = "";

    /**
     * Creates a creature with an initial state identifier.
     * States must be registered before initial activation.
     * Construction does not execute entry effects because the
     * creature has not yet been placed on a map.
     *
     * @param name the creature's name
     * @param displayChar the creature's normal display character
     * @param hitPoints the creature's starting health
     * @param initialStateType the identifier of its starting state
     */
    protected StatefulCreature(String name, char displayChar, int hitPoints, CreatureStateType initialStateType) {
        super(name, displayChar, hitPoints);
        this.initialStateType = initialStateType;
    }

    /**
     * Registers a state supported by this creature.
     * Each state must be registered once before initial activation.
     *
     * @param state the state to register
     */
    protected final void registerState(CreatureState state) {
        states.put(state.getType(), state);
    }

    /**
     * Activates the initial state after the creature is placed on the map.
     * Repeated calls have no effect once a state is active.
     *
     * @param map the map containing this creature
     */
    public final void initialiseState(GameMap map) {
        if (currentState == null) {
            changeState(initialStateType, map.locationOf(this));
        }
    }

    /**
     * Returns the identifier of the active state.
     * Must be called after the initial state has been activated.
     *
     * @return the active state's identifier
     */
    public final CreatureStateType getCurrentStateType() {
        return currentState.getType();
    }

    /**
     * Evaluates one transition and selects an ordinary-turn action.
     * Entry effects execute only when a different state is selected.
     * The selected action is returned for the engine to execute.
     *
     * @param actions collection of possible Actions for this Actor
     * @param lastAction The Action this Actor took last turn. Can do
     * interesting things in conjunction with Action.getNextAction()
     * @param map the map containing the Actor
     * @param display the I/O object to which messages may be written
     * @return the action selected by the active state
     */
    @Override
    public final Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (!isConscious()) {
            display.println(unconscious(map));
            return new DoNothingAction();
        }

        initialiseState(map);
        printEntryMessage(display);

        Location location = map.locationOf(this);

        CreatureStateType nextStateType = currentState.determineNextState(location);

        changeState(nextStateType, location);
        printEntryMessage(display);

        display.println(this + " state: " + currentState.getType());

        return currentState.getAction(map.locationOf(this));
    }

    private void changeState(CreatureStateType nextStateType, Location location){

        if (currentState != null && currentState.getType() == nextStateType) {
            return;
        }

        CreatureState nextState = states.get(nextStateType);

        if (nextState == null) {
            throw new IllegalStateException(
                    "State is not registered: " + nextStateType
            );
        }

        currentState = nextState;
        pendingEntryMessage = currentState.onEnter(location);
    }

    private void printEntryMessage(Display display) {
        if (!pendingEntryMessage.isEmpty()) {
            display.println(pendingEntryMessage);
            pendingEntryMessage = "";
        }
    }
}
