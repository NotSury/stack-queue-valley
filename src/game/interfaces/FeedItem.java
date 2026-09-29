package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
/**
 * FeedItem represents an item that can be used to feed a feedable actor.
 *
 * <p>Each feed item defines an effect that is applied to the Farmer
 * when the item is used for feeding. The item can also define how it
 * is removed after a successful feeding action.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public interface FeedItem {
    /**
     * Applies the effect of using this item as feed.
     *
     * @param farmer the actor using this item to feed an animal
     * @return a description of the effect applied to the Farmer
     */
    String feedEffect(Actor farmer);
    /**
     * Removes this item after it has been successfully used for feeding.
     *
     * @param farmer the actor carrying the feed item
     */
    void removeAfterFeeding(Actor farmer);
}
