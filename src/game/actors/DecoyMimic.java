package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.DecoyTurnAction;
import game.interfaces.Hostile;

/**
 * A temporary imitation of a Cave Mimic.
 * Copies the original Mimic's visible appearance at creation.
 * Its ordinary behaviour is supplied by the creature factory, and
 * it expires after the configured number of executed turns.
 */
public class DecoyMimic extends Actor implements Hostile {

    private final Behaviour<Actor, Action> movementBehaviour;
    private final String appearanceDescription;

    private int remainingTurns;

    /**
     * Creates a temporary imitation of a Cave Mimic.
     *
     * @param original the Mimic whose visible appearance is copied
     * @param movementBehaviour the behaviour used to select movement
     * @param lifetimeTurns the number of executed turns before the decoy expires
     */
    public DecoyMimic(Actor original, Behaviour<Actor, Action> movementBehaviour, int lifetimeTurns) {
        super("Cave Mimic", original.getDisplayChar(), 1);

        this.movementBehaviour = movementBehaviour;
        this.appearanceDescription = original.toString();
        this.remainingTurns = lifetimeTurns;
    }

    /**
     * Selects movement and wraps it with lifetime advancement.
     * An idle action is used when the movement behaviour cannot
     * select a useful action.
     *
     * @param actions collection of possible Actions available for this decoy
     * @param lastAction the action performed during the previous turn
     * @param map the map containing the decoy
     * @param display the I/O object to which messages may be written
     * @return an action that performs movement and completes one decoy turn
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        Action movementAction = movementBehaviour.operate(this, map.locationOf(this));

        if (movementAction == null) {
            movementAction = new DoNothingAction();
        }

        return new DecoyTurnAction(this, movementAction);
    }

    /**
     * Advances the lifetime after an ordinary action has executed.
     *
     * @return true when the decoy's lifetime has expired
     */
    public boolean completeTurn() {
        remainingTurns--;
        return remainingTurns <= 0;
    }

    /**
     * Removes the decoy from the supplied map if it is still present.
     * Supports expiry, attack handling and replacement of an older decoy.
     *
     * @param map the map from which to remove the decoy
     */
    public void vanish(GameMap map) {
        if (map.contains(this)) {
            map.removeActor(this);
        }
    }

    /**
     * Removes the decoy when an attack exposes its identity.
     * Called after the weapon and coating effects have resolved,
     * including when the weapon misses.
     *
     * @param attacker the actor who attacked the decoy
     * @param map the map containing the decoy
     * @return a message revealing that the attacked creature was a decoy
     */
    @Override
    public String respondToAttack(Actor attacker, GameMap map) {
        vanish(map);
        return "The attack exposed the decoy Mimic, and it vanishes.";
    }

    /**
     * Returns the original Mimic's visible description at creation.
     * Prevents the decoy's placeholder health from revealing its identity through
     * ordinary action descriptions.
     *
     * @return the copied appearance description
     */
    @Override
    public String toString() {
        return appearanceDescription;
    }
}
