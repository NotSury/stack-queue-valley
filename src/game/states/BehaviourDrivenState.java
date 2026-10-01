package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Location;
import game.perception.EnvironmentScanner;

/**
 * Base class for creature states that delegate ordinary action
 * selection to a behaviour.
 * Concrete states define their transition conditions and entry effects.
 * The assigned behaviour selects the action performed during an ordinary turn.
 */
public abstract class BehaviourDrivenState extends EnvironmentAwareState {

    private final Actor creature;
    private final Behaviour<Actor, Action> behaviour;

    /**
     * Creates a state with an owning creature and an ordinary behaviour.
     *
     * @param creature the creature that owns this state
     * @param scanner the scanner used to inspect the surroundings
     * @param behaviour the behaviour used to select ordinary actions
     */
    protected BehaviourDrivenState(Actor creature, EnvironmentScanner scanner, Behaviour<Actor, Action> behaviour) {
        super(scanner);
        this.creature = creature;
        this.behaviour = behaviour;
    }

    /**
     * Selects the creature's ordinary action through its behaviour.
     * Returns a do-nothing action when the behaviour cannot select a useful action.
     *
     * @param location the creature's current location
     * @return the selected action, or a do-nothing action
     */
    @Override
    public Action getAction(Location location) {
        Action action = behaviour.operate(creature, location);

        if (action == null) {
            return new DoNothingAction();
        }

        return action;
    }
}
