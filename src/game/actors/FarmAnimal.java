package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.WanderBehaviour;
import game.capabilities.Ability;
import game.capabilities.Feedable;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * Represents a farm animal that can be fed and can perform autonomous
 * behaviors such as wandering.
 */
public abstract class FarmAnimal extends Actor implements Feedable {

    private final Map<Integer, Behaviour<Actor, Action>> behaviours = new TreeMap<>();

    private final Random random = new Random();
    private static final int WANDER_BEHAVIOUR_PRIORITY = 999;

    /**
     * Constructs a farm animal.
     *
     * @param name the name of the animal
     * @param displayChar the character used to represent the animal
     * @param hitPoints the animal's starting health
     */
    protected FarmAnimal(String name, char displayChar, int hitPoints) {
        super(name, displayChar, hitPoints);
        this.enableAbility(Ability.FEEDABLE);
        behaviours.put(WANDER_BEHAVIOUR_PRIORITY, new WanderBehaviour());
    }

    /**
     * Selects an action for this animal to perform during its turn.
     *
     * @param actions collection of possible Actions for this animal
     * @param lastAction the action performed during the previous turn
     * @param map the game map containing this animal
     * @param display the game display
     * @return the selected action
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        for (Behaviour<Actor, Action> behaviour : behaviours.values()) {
            Action action = behaviour.operate(this, map.locationOf(this));

            if (action != null) {
                return action;
            }
        }

        return new DoNothingAction();
    }

    /**
     * Places an item on a randomly selected location adjacent to the given
     * actor location.
     *
     * @param actorLocation the location of the actor performing the feeding
     * @param item the item to be placed
     */
    protected void dropItemNearby(Location actorLocation, Item item) {
        List<Location> adjacentLocations = actorLocation.getNearbyLocations(1);

        if (!adjacentLocations.isEmpty()) {
            int randomIndex = random.nextInt(adjacentLocations.size());
            Location selectedLocation = adjacentLocations.get(randomIndex);
            selectedLocation.addItem(item);
        }
    }
}
