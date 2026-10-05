package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.behaviours.AttackBehaviour;
import game.behaviours.WanderBehaviour;
import game.interfaces.Hostile;
import game.weapons.CreatureWeapon;

import java.util.Map;
import java.util.TreeMap;
/**
 * Base class for creatures in the mines that the farmer can fight.
 *
 * <p>Every hostile creature attacks the farmer when next to them, and otherwise
 * wanders. Subclasses only set their stats and override {@link #respondToAttack}
 * (or {@code unconscious}) to add their special behaviour.</p>
 *
 * @author <Lee Le Si> <34599223>
 * @version 1.0.0
 */
public abstract class HostileCreature extends Actor implements Hostile {
    /**
     * Priority of attacking: tried first.
     */
    private static final int ATTACK_PRIORITY = 0;

    /**
     * Priority of wandering: tried only when there is nothing to attack.
     */
    private static final int WANDER_PRIORITY = 1;

    /**
     * Behaviours in priority order: a TreeMap keeps the keys sorted,
     * so the lowest number is tried first.
     */

    private final Map<Integer, Behaviour<Actor, Action>>  behaviours = new TreeMap<>();
    /**
     * Constructor for HostileCreature.
     *
     * @param name        creature name
     * @param displayChar character shown on the map
     * @param hitPoints   starting HP
     * @param weapon      the creature's natural attack
     */
    protected HostileCreature(String name, char displayChar, int hitPoints, CreatureWeapon weapon){
        super(name, displayChar, hitPoints);
        setIntrinsicWeapon(weapon);
        behaviours.put(ATTACK_PRIORITY, new AttackBehaviour());
        behaviours.put(WANDER_PRIORITY, new WanderBehaviour());
    }

    /**
     * Chooses what the creature does this turn.
     *
     * <p>If it has no health left (e.g. it burned to death), it is removed.
     * Otherwise each behaviour is tried in priority order and the first one that
     * returns an action is used. If none applies, the creature does nothing.</p>
     *
     * @param actions    actions the engine offers (not used by creatures)
     * @param lastAction the action taken last turn
     * @param map        the map the creature is on
     * @param display    the display used to print messages
     * @return the action to perform this turn
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (!isConscious()) {
            display.println(unconscious(map));
            return new DoNothingAction();
        }

        for (Behaviour<Actor, Action> behaviour : behaviours.values()) {
            Action action = behaviour.operate(this, map.locationOf(this));
            if (action != null) {
                return action;
            }
        }
        return new DoNothingAction();
        }

    /**
     * By default a hostile creature does nothing special when attacked.
     * Subclasses override this to react (e.g. drop an item or split).
     *
     * @param attacker the actor who attacked this creature
     * @param map      the map the creature is on
     * @return an empty string, as nothing happens
     */
    @Override
    public String respondToAttack(Actor attacker, GameMap map) {
        return "";
    }
}
