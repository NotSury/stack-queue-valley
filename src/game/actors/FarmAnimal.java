package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.behaviours.WanderBehaviour;
import game.interfaces.Feedable;

/**
 * FarmAnimal is an abstract base class for animals that can be fed
 * and can wander around the game map.
 *
 * <p>Common animal behaviour such as wandering and health restoration
 * from feeding is centralised in this class. Concrete animals only need
 * to define their own result after being fed.</p>
 *
 * @author Lee Le Si 34599223
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.1.0
 */
public abstract class FarmAnimal extends Actor implements Feedable {

    /**
     * The behaviour used by this farm animal to determine its movement.
     */
    private final Behaviour<Actor, Action> behaviour = new WanderBehaviour();

    /**
     * The amount of health restored when this animal is fed.
     */
    private final int feedingHealthRestore;

    /**
     * Constructor for the FarmAnimal class.
     *
     * @param name the name of the farm animal
     * @param displayChar the character used to represent the animal
     * @param hitPoints the initial and maximum health of the animal
     * @param feedingHealthRestore the amount of health restored when fed
     */
    public FarmAnimal(
            String name,
            char displayChar,
            int hitPoints,
            int feedingHealthRestore) {

        super(name, displayChar, hitPoints);
        this.feedingHealthRestore = feedingHealthRestore;
    }

    /**
     * Applies the common feeding behaviour of a farm animal.
     *
     * <p>The animal restores its health before performing its
     * animal-specific result of being fed.</p>
     *
     * @param farmer the actor feeding the animal
     * @param location the current location of the animal
     * @return a description of the feeding result
     */
    @Override
    public final String feed(Actor farmer, Location location) {

        this.modifyStatistic(
                ActorStatistics.HEALTH,
                StatisticOperations.INCREASE,
                feedingHealthRestore
        );

        produceAfterFeeding(location.map().locationOf(farmer));

        return farmer + " feeds the " + name + ".";
    }

    /**
     * Performs the animal-specific result after the animal is fed.
     *
     * @param location the current location of the animal
     */
    protected abstract void produceAfterFeeding(Location location);

    /**
     * Determines the action performed by this farm animal during its turn.
     *
     * <p>The animal delegates its movement decision to its behaviour.
     * If the behaviour does not return an action, the animal performs
     * a DoNothingAction instead.</p>
     *
     * @param actions the actions currently available to the animal
     * @param lastAction the action performed during the previous turn
     * @param map the game map containing the animal
     * @param display the display used by the game
     * @return the action performed by the animal for this turn
     */
    @Override
    public Action playTurn(
            ActionList actions,
            Action lastAction,
            GameMap map,
            Display display) {

        if (!isConscious()){
            display.println(unconscious(map));
            return new DoNothingAction();
        }

        Action action = behaviour.operate(this, map.locationOf(this));

        if (action != null) {
            return action;
        }

        return new DoNothingAction();
    }
}