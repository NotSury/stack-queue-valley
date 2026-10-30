package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.AttackAction;
import game.utils.NearbyActors;

/**
 * How an undead minion fights for its master (REQ3).
 *
 * <p>In order of priority, the minion:</p>
 * <ol>
 *     <li>attacks an adjacent hostile with its intrinsic weapon;</li>
 *     <li>otherwise steps towards the closest hostile within its hunting range;</li>
 *     <li>otherwise steps back towards its master if it has strayed too far.</li>
 * </ol>
 * <p>If none of these apply, it returns null so the minion's next behaviour
 * (wandering) is tried.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class MinionBehaviour implements Behaviour<Actor, Action> {

    /** How far away the minion notices hostiles. */
    private static final int HUNT_RADIUS = 4;

    /** How far the minion may stray from its master before returning. */
    private static final int FOLLOW_DISTANCE = 2;

    /** The actor that raised this minion. */
    private final Actor master;

    /**
     * Creates the behaviour for a minion serving the given master.
     *
     * @param master the actor that raised the minion
     */
    public MinionBehaviour(Actor master) {
        this.master = master;
    }

    /**
     * Chooses the minion's action this turn.
     *
     * @param actor    the minion
     * @param location the minion's current location
     * @return an attack or a move, or null if there is nothing to do
     */
    @Override
    public Action operate(Actor actor, Location location) {
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            if (destination.containsAnActor() && NearbyActors.HOSTILE.test(destination.getActor())) {
                return new AttackAction(destination.getActor(), exit.getName(), actor.getIntrinsicWeapon());
            }
        }

        Location target = NearbyActors.nearest(location, HUNT_RADIUS, NearbyActors.HOSTILE);
        if (target == null) {
            target = masterLocationIfFar(location);
        }
        return target == null ? null : stepTowards(actor, location, target);
    }

    /**
     * Returns the master's location if the master is on the same map and
     * further away than the follow distance.
     *
     * @param location the minion's current location
     * @return the master's location, or null if the minion should stay
     */
    private Location masterLocationIfFar(Location location) {
        GameMap map = location.map();
        if (!map.contains(master)) {
            return null;
        }
        Location masterLocation = map.locationOf(master);
        return NearbyActors.distance(location, masterLocation) > FOLLOW_DISTANCE ? masterLocation : null;
    }

    /**
     * Picks the enterable exit that brings the minion closest to the target.
     *
     * @param actor    the minion
     * @param location the minion's current location
     * @param target   where the minion wants to go
     * @return a move that gets closer, or null if no exit gets closer
     */
    private Action stepTowards(Actor actor, Location location, Location target) {
        Exit best = null;
        int bestDistance = NearbyActors.distance(location, target);
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            int distance = NearbyActors.distance(destination, target);
            if (destination.canActorEnter(actor) && distance < bestDistance) {
                best = exit;
                bestDistance = distance;
            }
        }
        return best == null ? null : new MoveActorAction(best.getDestination(), best.getName());
    }
}