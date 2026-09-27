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
 * Represents a harvested Parsnip that can be carried, consumed,
 * used for feeding, and processed by artisan facilities.
 */
public class Parsnip extends ProcessableProduct implements FeedItem {

    private static final int HEALTH_RESTORED = 1;
    private static final int MAX_STAMINA_INCREASE = 1;

    /**
     * Construct a portable Parsnip.
     */
    public Parsnip(){
        super("Parsnip", 'p');
    }

    /**
     * Applies the raw Parsnip consumption effect.
     * Consuming a Parsnip restores one health point without exceeding the actor's
     * maximum health.
     *
     * @param actor the actor consuming this Parsnip
     */
    @Override
    protected void applyConsumptionEffects(Actor actor) {
        actor.heal(HEALTH_RESTORED);
    }

    /**
     * Applies the Parsnip specific effect when used to feed an animal.
     * The actor's maximum stamina is increased and this Parsnip is removed
     * from the actor's inventory.
     *
     * @param actor the actor using this Parsnip for feeding
     */
    @Override
    public void usedForFeedingBy(Actor actor) {
        actor.modifyStatisticMaximum(ActorStatistics.STAMINA, StatisticOperations.INCREASE, MAX_STAMINA_INCREASE);
        actor.getInventory().remove(this);
    }

    /**
     * Returns actions this Parsnip allows its owner to perform on another actor.
     *
     * @param otherActor the actor that may be fed
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
