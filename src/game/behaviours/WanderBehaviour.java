package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * WanderBehaviour represents a behaviour that allows an actor to move
 * randomly to one of its accessible neighbouring locations.
 *
 * <p>The behaviour examines all exits from the actor's current location
 * and creates movement actions for destinations that the actor can enter.
 * One of the available movement actions is then selected randomly.</p>
 *
 * <p>If there are no accessible neighbouring locations, the behaviour
 * returns null and no movement action is performed.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class WanderBehaviour implements Behaviour<Actor,Action> {
    /**
     * Random number generator used to select one of the available
     * movement actions.
     */
    private final Random random = new Random();

    /**
     * Determines a random movement action for the actor.
     *
     * <p>All exits from the current location are checked and only destinations
     * that the actor can enter are considered. A movement action is created
     * for each valid destination and one is selected randomly.</p>
     *
     * @param actor the actor performing the wandering behaviour
     * @param location the actor's current location
     * @return a randomly selected movement action, or null if no valid
     *         movement is available
     */
    @Override
    public Action operate(Actor actor, Location location) {

    List<Action> actions = new ArrayList<>();

        for (Exit exit : location.getExits()) {
         if (exit.getDestination().canActorEnter(actor)) {
             actions.add(new MoveActorAction(
                     exit.getDestination(),
                     exit.getName()
             ));
         }
     }

     if (!actions.isEmpty()){
         return actions.get(random.nextInt(actions.size()));
     }

     return null;
    }
}
