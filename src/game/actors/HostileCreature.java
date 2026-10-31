package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.afflictions.Affliction;
import game.afflictions.CurseApplier;
import game.behaviours.AttackBehaviour;
import game.behaviours.WanderBehaviour;
import game.grounds.Remains;
import game.interfaces.Cursed;
import game.interfaces.Hostile;
import game.weapons.CreatureWeapon;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
/**
 * Base class for creatures in the mines that the farmer can fight.
 *
 * <p>Every hostile creature attacks the farmer when next to them, and otherwise
 * wanders. Subclasses only set their stats and override {@link #respondToAttack}
 * (or {@code unconscious}) to add their special behaviour.</p>
 *
 * <p>When a creature that is {@link Cursed} dies (REQ3), its killer inherits its
 * curse, its death changes the land around it, and it leaves {@link Remains}.
 * This happens once here for every cursed creature, so the subclasses only
 * define their own curse and death effect.</p>
 *
 * @author <Lee Le Si> <34599223>
 * @version 1.1.0
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

    /**
     * Called when another actor's blow knocks this creature out. A cursed
     * creature passes its curse to the killer and leaves cursed remains before
     * it is removed.
     *
     * @param attacker the actor that defeated this creature
     * @param map      the map the creature is on
     * @return a description of what happened
     */
    @Override
    public String unconscious(Actor attacker, GameMap map) {
        Location here = map.locationOf(this);
        String curseResult = passCurseTo(attacker) + leaveRemains(here);
        return curseResult + super.unconscious(attacker, map);
    }

    /**
     * Called when this creature dies without a killer (for example, from a
     * status effect). A cursed creature still leaves cursed remains, but its
     * curse has nobody to pass to.
     *
     * @param map the map the creature is on
     * @return a description of what happened
     */
    @Override
    public String unconscious(GameMap map) {
        Location here = map.locationOf(this);
        String curseResult = leaveRemains(here);
        return curseResult + super.unconscious(map);
    }

    /**
     * Passes a fresh copy of this creature's curse to its killer, if this
     * creature is cursed.
     *
     * @param killer the actor that defeated this creature
     * @return a description of what happened, or an empty string if this creature is not cursed
     */
    private String passCurseTo(Actor killer) {
        Optional<Cursed> cursed = asCapability(Cursed.class);
        if (cursed.isEmpty()) {
            return "";
        }
        Affliction curse = cursed.get().curse();
        if (CurseApplier.apply(killer, curse)) {
            return killer + " inherits " + curse + " from " + this + "." + System.lineSeparator();
        }
        return "The " + curse + " curse of " + this + " fizzles out." + System.lineSeparator();
    }

    /**
     * Applies this creature's death effect and leaves cursed remains on its
     * tile, if this creature is cursed.
     *
     * @param here the location where this creature died
     * @return a description of what happened, or an empty string if this creature is not cursed
     */
    private String leaveRemains(Location here) {
        Optional<Cursed> cursed = asCapability(Cursed.class);
        if (cursed.isEmpty()) {
            return "";
        }
        String deathEffect = cursed.get().onDeath(here);
        here.setGround(new Remains(here.getGround(), cursed.get()));
        return deathEffect + System.lineSeparator()
                + this + " leaves cursed remains." + System.lineSeparator();
    }
}