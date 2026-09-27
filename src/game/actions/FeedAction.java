package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.capabilities.FeedItem;
import game.capabilities.Feedable;

/**
 * Represents an action that allows an actor to feed a feedable target
 * using a selected feeding item.
 */
public class FeedAction extends Action {

    private static final int FEED_STAMINA_COST = 2;

    private final Feedable feedable;
    private final FeedItem feedItem;

    /**
     * Constructs a feeding action.
     *
     * @param feedable the target being fed
     * @param feedItem the item used for feeding
     */
    public FeedAction(Feedable feedable, FeedItem feedItem) {
        this.feedable = feedable;
        this.feedItem = feedItem;
    }

    /**
     * Executes the feeding action.
     * The actor must have sufficient stamina before the target and food
     * effects are applied.
     *
     * @param actor the actor performing the feeding
     * @param map the map containing the actor
     * @return a description of the feeding result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (actor.getStatistic(ActorStatistics.STAMINA) < FEED_STAMINA_COST) {
            return "You are too tired to feed this animal.";
        }

        actor.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.DECREASE, FEED_STAMINA_COST);

        Location actorLocation = map.locationOf(actor);

        feedable.fedBy(actor, actorLocation);
        feedItem.usedForFeedingBy(actor);

        return actor + " feeds " + feedable + " with " + feedItem + ".";
    }

    /**
     * Describes the feeding action in the action menu.
     *
     * @param actor the actor performing the feeding
     * @return the menu description
     */
    @Override
    public String menuDescription(Actor actor){
        return actor + " feeds " + feedable + " with " + feedItem;
    }
}
