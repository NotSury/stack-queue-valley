package game.coatings;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.CoatAction;
import game.interfaces.Coatable;
import game.interfaces.CoatingSource;

import java.util.HashSet;
import java.util.Set;

/**
 * Finds every coating source an actor can use right now and offers one
 * {@link CoatAction} per kind of coating.
 *
 * <p>A source counts if the actor is carrying it, or if it lies on (or is) the
 * ground of an adjacent tile. Kept separate from the sword so any future
 * {@link Coatable} reuses the same rules (SRP, DRY).</p>
 *
 * @author <Lee Le Si> <34599223>
 * @version 1.1.0
 */
public class CoatingSourceScanner {
    /**
     * Private constructor: this is a utility class with only static methods,
     * so it is never meant to be instantiated.
     */
    private CoatingSourceScanner(){}

    /**
     * Builds the coat actions available to an actor for one coatable thing.
     *
     * <p>Checks, in order: the items the actor is carrying, then for every
     * adjacent tile the items lying there and the ground itself. The engine's
     * getItemsAs / getGroundAs only return things that implement
     * {@link CoatingSource}, so no type checks are needed here.</p>
     *
     * @param coatable the thing that would be coated (e.g. the sword)
     * @param actor    the actor applying the coating
     * @param map      the map the actor is on
     * @return one CoatAction per distinct coating available
     */
    public static ActionList coatActions(Coatable coatable, Actor actor, GameMap map){
        ActionList actions = new ActionList();
        Set<CoatingKind> offered = new HashSet<>();

        // 1. sources the actor is carrying (source location = null)
        for (CoatingSource source : actor.getInventory().getItemsAs(CoatingSource.class)){
            offer(actions, offered, coatable, source, null);
        }

        // 2. sources on the tiles around the actor
        for (Exit exit : map.locationOf(actor).getExits()){
            Location nearby = exit.getDestination();

            // items lying on the adjacent tile (e.g. fire)
            for (CoatingSource source : nearby.getItemsAs(CoatingSource.class)){
                offer(actions, offered, coatable, source, nearby);
            }

            // the adjacent ground itself (e.g. charged ground); null if it is not a source
            CoatingSource ground = nearby.getGroundAs(CoatingSource.class);
            if (ground != null){
                offer(actions, offered, coatable, ground, nearby);
            }
        }
        return actions;
    }

    /**
     * Adds a CoatAction for a source, if the source is usable right now and
     * its kind of coating has not been offered yet.
     *
     * @param actions        the list the action is added to
     * @param offered        kinds of coating already offered
     * @param coatable       the thing that would be coated
     * @param source         the coating source being considered
     * @param sourceLocation where the source lies, or null if it is carried
     */
    private static void offer(ActionList actions, Set<CoatingKind> offered, Coatable coatable,
                              CoatingSource source, Location sourceLocation){
        // e.g. ground that is not charged at the moment
        if (!source.canProvideCoating()){
            return;
        }

        // two fires next to the farmer should only give one "coat with fire" option
        if (offered.add(source.getCoating().getKind())){
            actions.add(new CoatAction(coatable, source, sourceLocation));
        }
    }
}
