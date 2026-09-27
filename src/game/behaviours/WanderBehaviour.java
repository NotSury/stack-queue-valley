package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a behavior that moves an actor to a randomly selected
 * neighboring location when movement is possible.
 */
public class WanderBehaviour implements Behaviour<Actor, Action> {

    private final Random random = new Random();

    /**
     * Selects a random valid movement action for the actor.
     *
     * @param actor the actor performing the behavior
     * @param location the actor's current location
     * @return a movement action, or null if no movement is possible
     */
    @Override
    public Action operate(Actor actor, Location location) {
        List<Action> actions = new ArrayList<>();

        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();

            if (destination.canActorEnter(actor)) {
                actions.add(destination.getMoveAction(actor, "around", exit.getHotKey()));
            }
        }

        if (actions.isEmpty()) {
            return null;
        }

        return actions.get(random.nextInt(actions.size()));
    }
}
