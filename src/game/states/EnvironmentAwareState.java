package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.positions.Location;
import game.perception.EnvironmentScanner;

/**
 * Base class for creature states that inspect their surroundings.
 * Receives an environmental scanner through its constructor.
 * States remain stationary unless they override ordinary action selection.
 */
public abstract class EnvironmentAwareState implements CreatureState {

    private final EnvironmentScanner scanner;

    /**
     * Creates a state with access to environmental information.
     *
     * @param scanner the scanner used to inspect the surroundings
     */
    protected EnvironmentAwareState(EnvironmentScanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Returns the environmental scanner available to this state.
     *
     * @return the state's scanner
     */
    protected final EnvironmentScanner getScanner() {
        return scanner;
    }

    /**
     * Selects an action that keeps the creature stationary.
     * Subclasses can override this method to provide ordinary behaviour.
     *
     * @param location the creature's current location
     * @return an action that performs no operation
     */
    @Override
    public Action getAction(Location location) {
        return new DoNothingAction();
    }
}
