package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actions.FeedAction;
import game.capabilities.Ability;
import game.capabilities.FeedItem;
import game.capabilities.Feedable;

/**
 * Represents a harvested Tomato that can be carried, consumed,
 * used for feeding, and processed by artisan facilities.
 */
public class Tomato extends ProcessableProduct implements FeedItem {

    private static final int MAX_HEALTH_INCREASE = 2;
    private static final int MAX_HEALTH_INCREASE_WHEN_FEEDING = 2;

    /**
     * Constructs a portable Tomato.
     */
    public Tomato() {
        super("Tomato", 'o');
    }

    /**
     * Applies the raw Tomato consumption effect.
     * Consuming a Tomato increases the actor's maximum health.
     *
     * @param actor the actor consuming this Tomato
     */
    @Override
    protected void applyConsumptionEffects(Actor actor) {
        actor.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.INCREASE, MAX_HEALTH_INCREASE);
    }

    /**
     * Applies the Tomato specific effect when used for feeding.
     *
     * @param actor the actor using this Tomato for feeding
     */
    @Override
    public void usedForFeedingBy(Actor actor) {
        actor.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.INCREASE, MAX_HEALTH_INCREASE_WHEN_FEEDING);
        actor.getInventory().remove(this);
    }

    /**
     * Returns actions this Tomato allows its owner to perform on another actor
     *
     * @param otherActor the other actor that may be fed
     * @param location the location of the other actor
     * @return the available actions
     */
    @Override
    public ActionList allowableActions(Actor otherActor, Location location) {
        ActionList actions = new ActionList();

        if (otherActor.hasAbility(Ability.FEEDABLE)) {
            actions.add(new FeedAction((Feedable) otherActor, this));
        }

        return actions;
    }
}
