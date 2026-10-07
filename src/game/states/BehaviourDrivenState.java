package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Location;
import game.perception.EnvironmentScanner;

/**
 * Base class for creature states that delegate ordinary action
 * selection to an injected behaviour.
 * Concrete states define their transition conditions and entry effects.
 */
public abstract class BehaviourDrivenState extends EnvironmentAwareState {

    private final Behaviour<Actor, Action> behaviour;

    /**
     * Creates a state with an owning creature and an ordinary behaviour.
     *
     * @param creature the actor that owns this state
     * @param scanner the scanner used to inspect the surroundings
     * @param behaviour the behaviour used to select ordinary actions
     */
    protected BehaviourDrivenState(Actor creature, EnvironmentScanner scanner, Behaviour<Actor, Action> behaviour) {
        super(creature, scanner);
        this.behaviour = behaviour;
    }

    /**
     * Selects the creature's ordinary action through its behaviour.
     * Uses the parent's idle action when no useful action is selected.
     *
     * @param location the creature's current location
     * @return the selected action, or an idle action
     */
    @Override
    public Action getAction(Location location) {
        Action action = behaviour.operate(getCreature(), location);

        if (action == null) {
            return super.getAction(location);
        }

        return action;
    }
}
