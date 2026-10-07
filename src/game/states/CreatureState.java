package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Each concrete state receives its owning creature through its constructor.
 */
public interface CreatureState{

    /**
     * Returns the identifier of this state.
     *
     * @return this state's identifier
     */
    CreatureStateType getType();

    /**
     * Determines the next state using the creature's current circumstances.
     * Returns this state's identifier when the creature should remain
     * in its current state. Implementations must evaluate transition
     * conditions deterministically and must not execute entry effects.
     *
     * @param location the creature's current location
     * @return the identifier of the selected state
     */
    CreatureStateType determineNextState(Location location);

    /**
     * Performs the immediate effects of entering this state.
     * Called once on initial activation or when the creature changes state.
     * Remaining in the same state must not repeat these effects.
     *
     * @param location the creature's current location
     * @return a description of the entry effects, or an empty string
     * when there is nothing to report
     */
    String onEnter(Location location);

    /**
     * Selects the creature's ordinary action for this turn without executing it.
     * Implementations return a DoNothingAction when no useful action
     * is available, rather than returning null.
     *
     * @param location the creature's current location
     * @return a non-null action for the engine to execute
     */
    Action getAction(Location location);
}
