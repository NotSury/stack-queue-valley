package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.SpiderlingTurnAction;
import game.capabilities.WebAbility;
import game.interfaces.Hostile;
import game.interfaces.Stunnable;

/**
 * A small, independent Spiderling that wanders without attacking.
 * Disappears after eight completed ordinary turns.
 * Spiderlings are immune to web bindings.
 *
 * @author Surya Muni Peragas 35651628
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.1.0
 */
public class Spiderling extends Actor implements Hostile, Stunnable {

    private static final int STARTING_HEALTH = 10;
    private static final int LIFETIME_TURNS = 8;

    private final Behaviour<Actor, Action> wanderingBehaviour;

    private int remainingTurns = LIFETIME_TURNS;

    private int stunTurns = 0;

    /**
     * Creates an unplaced Spiderling with ordinary NPC movement.
     *
     * @param wanderingBehaviour the behaviour used for wandering
     */
    public Spiderling(Behaviour<Actor, Action> wanderingBehaviour) {
        super("Spiderling", 'z', STARTING_HEALTH);
        this.wanderingBehaviour = wanderingBehaviour;
        enableAbility(WebAbility.IMMUNE_TO_WEBS);
    }

    /**
     * Selects wandering or idling and wraps it with lifetime advancement.
     *
     * @param actions the available actions
     * @param lastAction the previous action, or null
     * @param map the map containing this Spiderling
     * @param display the game's display
     * @return the Spiderling's ordinary turn action
     */

    @Override
    public Action playTurn(
            ActionList actions,
            Action lastAction,
            GameMap map,
            Display display
    ) {
        if (!isConscious()) {
            display.println(unconscious(map));
            return new DoNothingAction();
        }

        if (stunTurns > 0)
        {
            stunTurns--;
            display.println(this + " is stunned and cannot act.");
            return new DoNothingAction();
        }

        Action action = wanderingBehaviour.operate(
                this,
                map.locationOf(this)
        );

        if (action == null) {
            action = new DoNothingAction();
        }

        return new SpiderlingTurnAction(this, action);
    }

    /**
     * Advances the lifetime after one ordinary action has executed.
     *
     * @return true when eight turns have been completed
     */
    public boolean completeTurn() {
        remainingTurns--;
        return remainingTurns <= 0;
    }

    /**
     * Removes this Spiderling when its lifetime ends.
     *
     * @param map the map containing this Spiderling
     */
    public void vanish(GameMap map) {
        if (map.contains(this)) {
            map.removeActor(this);
        }
    }

    /**
     * Applies a temporary stun without shortening an existing stun.
     *
     * @param turns the number of turns to remain stunned
     */
    @Override
    public void stun(int turns) {
        stunTurns = Math.max(stunTurns, turns);
    }

    /**
     * Provides the existing combat capability without a special reaction.
     *
     * @param attacker the attacking actor
     * @param map the map containing this Spiderling
     * @return an empty string
     */
    @Override
    public String respondToAttack(Actor attacker, GameMap map) {
        return "";
    }
}
