package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.MinionBehaviour;
import game.behaviours.WanderBehaviour;
import game.capabilities.CombatStatus;
import game.capabilities.NecromancyCapability;
import game.weapons.CreatureWeapon;

import java.util.Map;
import java.util.TreeMap;

/**
 * An undead minion raised by the Farmer from the remains of a cursed creature
 * (REQ3).
 *
 * <p>Every undead is immune to curses, fights hostiles for its master, and
 * crumbles after a fixed number of turns. Each turn follows the same steps
 * (Template Method): leave a trail on the tile it just left, use its own
 * ability, count down its lifespan, then act. Subclasses only fill in their
 * ability, and optionally their trail and what happens when they crumble.</p>
 *
 * <p>Hostile creatures treat undead as targets, so minions can be destroyed in
 * a fight. Whenever a minion is gone, its master may raise a new one.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public abstract class Undead extends Actor {

    /** Priority of serving the master: tried first. */
    private static final int MINION_PRIORITY = 0;

    /** Priority of wandering: tried only when there is nothing else to do. */
    private static final int WANDER_PRIORITY = 1;

    /** The actor that raised this minion. */
    private final Actor master;

    /** Behaviours in priority order; the lowest number is tried first. */
    private final Map<Integer, Behaviour<Actor, Action>> behaviours = new TreeMap<>();

    /** Turns left before this minion crumbles. */
    private int turnsLeft;

    /** Where this minion stood at the start of its previous turn. */
    private Location lastLocation;

    /**
     * Creates an undead minion.
     *
     * @param name        the minion's name
     * @param displayChar the character shown on the map
     * @param hitPoints   starting HP
     * @param weapon      the minion's natural attack
     * @param master      the actor that raised this minion
     * @param lifespan    how many turns the minion lasts before crumbling
     */
    protected Undead(String name, char displayChar, int hitPoints, CreatureWeapon weapon,
                     Actor master, int lifespan) {
        super(name, displayChar, hitPoints);
        setIntrinsicWeapon(weapon);
        this.master = master;
        this.turnsLeft = lifespan;
        this.enableAbility(NecromancyCapability.UNDEAD);
        this.enableAbility(CombatStatus.HOSTILE_TARGET);
        behaviours.put(MINION_PRIORITY, new MinionBehaviour(master));
        behaviours.put(WANDER_PRIORITY, new WanderBehaviour());
    }

    /**
     * Runs one turn: leave a trail if the minion moved, use its ability, count
     * down its lifespan, then attack, move or wander.
     *
     * @param actions    actions the engine offers (not used by minions)
     * @param lastAction the action taken last turn
     * @param map        the map the minion is on
     * @param display    the display used to print messages
     * @return the action to perform this turn
     */
    @Override
    public final Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (!isConscious()) {
            display.println(unconscious(map));
            return new DoNothingAction();
        }

        Location here = map.locationOf(this);
        if (lastLocation != null && lastLocation != here) {
            printIfAny(display, leaveTrail(lastLocation));
        }
        lastLocation = here;
        printIfAny(display, useAbility(here));

        turnsLeft--;
        if (turnsLeft <= 0) {
            display.println(fallApart(map, " crumbles to dust."));
            return new DoNothingAction();
        }

        for (Behaviour<Actor, Action> behaviour : behaviours.values()) {
            Action action = behaviour.operate(this, here);
            if (action != null) {
                return action;
            }
        }
        return new DoNothingAction();
    }

    /**
     * Called when a hostile's blow destroys this minion.
     *
     * @param attacker the actor that destroyed this minion
     * @param map      the map the minion is on
     * @return a description of what happened
     */
    @Override
    public String unconscious(Actor attacker, GameMap map) {
        return fallApart(map, " is destroyed by " + attacker + ".");
    }

    /**
     * Called when this minion falls apart without a killer.
     *
     * @param map the map the minion is on
     * @return a description of what happened
     */
    @Override
    public String unconscious(GameMap map) {
        return fallApart(map, " falls apart.");
    }

    /**
     * Uses this minion's ability at the start of its turn.
     *
     * @param here the minion's current location
     * @return a description of what happened, or an empty string if nothing did
     */
    protected abstract String useAbility(Location here);

    /**
     * Affects the tile the minion just walked off. Does nothing by default.
     *
     * @param left the location the minion left
     * @return a description of what happened, or an empty string if nothing did
     */
    protected String leaveTrail(Location left) {
        return "";
    }

    /**
     * Affects the land when the minion crumbles or is destroyed. Does nothing by default.
     *
     * @param here the location where the minion fell apart
     * @return a description of what happened, or an empty string if nothing did
     */
    protected String onCrumble(Location here) {
        return "";
    }

    /**
     * Returns the actor that raised this minion.
     *
     * @return the master
     */
    protected Actor getMaster() {
        return master;
    }

    /**
     * Frees the master to raise another minion, applies the crumble effect and
     * removes this minion from the map.
     *
     * @param map    the map the minion is on
     * @param reason how the minion fell apart, appended to its name
     * @return a description of what happened
     */
    private String fallApart(GameMap map, String reason) {
        master.disableAbility(NecromancyCapability.HAS_MINION);
        String effect = onCrumble(map.locationOf(this));
        map.removeActor(this);
        return this + reason + (effect.isEmpty() ? "" : System.lineSeparator() + effect);
    }

    /**
     * Prints a message if it is not empty.
     *
     * @param display the display to print to
     * @param message the message
     */
    private static void printIfAny(Display display, String message) {
        if (!message.isEmpty()) {
            display.println(message);
        }
    }
}