package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actions.ConsumeAction;
import game.actions.FeedAction;
import game.interfaces.FeedItem;
import game.interfaces.Feedable;
import game.interfaces.FreshProduce;

/**
 * Tomato represents a harvested product that can be consumed,
 * fed to farm animals, and processed by processing facilities.
 *
 * <p>When consumed, a Tomato increases the actor's maximum health
 * by 2. When used as animal feed, it increases the Farmer's maximum
 * health by 2. As a ProcessableItem, it can also be repeatedly
 * processed into different processed products.</p>
 *
 * @author Lee Le Si 34599223
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.1.0
 */
public class Tomato extends ProcessableItem implements FeedItem, FreshProduce {
    /**
     * Constructor for the Tomato class.
     *
     * <p>Creates a Tomato with the display character 'o'.</p>
     */
    public Tomato(){
        super("Tomato",'o');
    }

    /**
     * Applies the consumption effect of a Tomato.
     *
     * <p>Consuming a Tomato increases the actor's maximum health by 2.
     * The actor's current health is updated according to the statistic
     * modification performed by the engine.</p>
     *
     * @param actor the actor consuming the Tomato
     * @return a description of the consumption result
     */
    @Override
    public String applyConsumptionEffect(Actor actor){
        actor.modifyStatisticMaximum(
                ActorStatistics.HEALTH,
                StatisticOperations.INCREASE,
                2
        );
        return actor + " consumes the Tomato.";
    }

    /**
     * Returns the actions that can be performed with this Tomato while
     * it is in an actor's inventory.
     *
     * @param owner the actor carrying the Tomato
     * @param map the current game map
     * @return a list containing the allowable actions for this Tomato
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map){
        ActionList actions = new ActionList();
        actions.add(new ConsumeAction(this));
        return actions;
    }

    /**
     * Applies the Farmer-related effect of using this Tomato as feed.
     *
     * <p>Feeding an animal with a Tomato increases the Farmer's
     * maximum health by 2.</p>
     *
     * @param farmer the Farmer using the Tomato as feed
     * @return a description of the feeding effect
     */
    @Override
    public String feedEffect(Actor farmer){
        farmer.modifyStatisticMaximum(
                ActorStatistics.HEALTH,
                StatisticOperations.INCREASE,
                2
        );
        return farmer + "'s maximum health increases by 2.";
    }

    /**
     * Removes this Tomato from the Farmer's inventory after it has
     * successfully been used to feed an animal.
     *
     * @param farmer the Farmer carrying the Tomato
     */
    @Override
    public void removeAfterFeeding(Actor farmer){
        farmer.getInventory().remove(this);
    }

    /**
     * Returns feeding actions available when this Tomato is carried
     * near another actor.
     *
     * <p>If the other actor supports the Feedable capability, a
     * FeedAction using this Tomato is added to the available actions.</p>
     *
     * @param otherActor the actor that may be fed
     * @param location the location of the other actor
     * @return the available actions involving this Tomato and the other actor
     */
    @Override
    public ActionList allowableActions(Actor otherActor, Location location){
        ActionList actions = new ActionList();

        otherActor.asCapability(Feedable.class).ifPresent(
                feedable -> actions.add(new FeedAction(feedable,this, location))
        );
        return actions;
    }

    /**
     * Checks if this Tomato is fresh.
     *
     * @return true because the Tomato cannot become rotten
     */
    @Override
    public boolean isFresh() {
        return true;
    }

    /**
     * Returns the product name of this Tomato.
     *
     * @return the name "Tomato"
     */
    @Override
    public String getProductName() {
        return "Tomato";
    }
}
