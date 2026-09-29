package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.interfaces.FeedItem;
import game.interfaces.Feedable;
/**
 * FeedAction represents an action that allows an actor to feed a
 * Feedable target using a FeedItem.
 *
 * <p>The action requires 2 stamina points. When successful, the target's
 * feeding effect and the food item's feeding effect are both applied.
 * The food item is then removed from the actor's inventory.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class FeedAction extends Action {
    /**
     * The feedable actor targeted by this action.
     */
    private final Feedable target;
    /**
     * The food item used to feed the target.
     */
    private final FeedItem food;
    /**
     * The location of the feedable target.
     */
    private final Location targetLocation;

    /**
     * Constructor for the FeedAction class.
     *
     * @param target the feedable actor being fed
     * @param food the item used to feed the target
     * @param targetLocation the location of the feedable target
     */
    public FeedAction(Feedable target, FeedItem food, Location targetLocation) {
        this.target = target;
        this.food = food;
        this.targetLocation = targetLocation;
    }

    /**
     * Executes the feeding action.
     *
     * <p>If the actor has at least 2 stamina points, the required stamina
     * is deducted before applying both the animal-specific feeding effect
     * and the food-specific feeding effect. The food item is removed after
     * the feeding process is completed.</p>
     *
     * <p>If the actor does not have enough stamina, no feeding effects are
     * applied and the food remains in the actor's inventory.</p>
     *
     * @param actor the actor performing the feeding action
     * @param map the game map where the action is performed
     * @return a description of the result of the feeding action
     */
    @Override
    public String execute(Actor actor, GameMap map){

        if (actor.getStatistic(ActorStatistics.STAMINA)<2){
            return actor + " is too tired to feed the animal.";
        }

        actor.modifyStatistic(
                ActorStatistics.STAMINA,
                StatisticOperations.DECREASE,
                2
        );

        String animalResult = target.feed(actor, targetLocation);
        String foodResult = food.feedEffect(actor);

        food.removeAfterFeeding(actor);

        return animalResult + " " + foodResult;
    }

    /**
     * Returns the description of this action for display in the action menu.
     *
     * @param actor the actor performing the action
     * @return the menu description of the feeding action
     */
    @Override
    public String menuDescription(Actor actor){
        return actor + " feeds " + target + " with " + food;
    }
}

