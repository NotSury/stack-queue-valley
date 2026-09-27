package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;

/**
 * Represents an item that can be used to feed a feedable actor.
 */
public interface FeedItem {

    /**
     * Applies this item's specific feeding effect to the actor using it.
     *
     * @param actor the actor using this item for feeding
     */
    void usedForFeedingBy(Actor actor);
}
