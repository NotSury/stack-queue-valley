package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actions.ConsumeAction;
import game.actions.FeedAction;
import game.capabilities.IngredientCategory;
import game.cooking.CookingContext;
import game.grounds.ParsnipCrop;
import game.interfaces.FeedItem;
import game.interfaces.Feedable;
import game.interfaces.Ingredient;
import game.interfaces.PlantableGround;

import java.util.Optional;

/**
 * Parsnip represents a harvested product that can be consumed,
 * fed to farm animals, and processed by processing facilities.
 *
 * <p>When consumed, a Parsnip restores 1 health point. When used
 * to feed an animal, it increases the Farmer's maximum stamina by 1.
 * As a ProcessableItem, it can also be repeatedly processed into
 * different processed products.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class Parsnip extends ProcessableItem implements FeedItem, Ingredient {
    /**
     * Constructor for the Parsnip class.
     *
     * <p>Creates a Parsnip with the display character 'p'.</p>
     */
    public Parsnip(){
        super("Parsnip",'p');
    }

    /**
     * Applies the consumption effect of a Parsnip.
     *
     * <p>Consuming a Parsnip restores 1 health point to the actor,
     * up to the actor's maximum health.</p>
     *
     * @param actor the actor consuming the Parsnip
     * @return a description of the consumption result
     */
    @Override
    public String applyConsumptionEffect(Actor actor){
        actor.modifyStatistic(
                ActorStatistics.HEALTH,
                StatisticOperations.INCREASE,
                1
        );


        return actor + " consumes the Parsnip ";
    }

    /**
     * Returns the actions that can be performed with this Parsnip
     * while it is in an actor's inventory.
     *
     * @param owner the actor carrying the Parsnip
     * @param map the current game map
     * @return a list containing the allowable actions for this Parsnip
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map){
        ActionList actions = new ActionList();
        actions.add(new ConsumeAction(this));
        return actions;
    }

    /**
     * Applies the Farmer-related effect of using this Parsnip as feed.
     *
     * <p>Feeding an animal with a Parsnip increases the Farmer's
     * maximum stamina by 1.</p>
     *
     * @param farmer the Farmer using the Parsnip as feed
     * @return a description of the feeding effect
     */
    @Override
    public String feedEffect(Actor farmer){
        farmer.modifyStatisticMaximum(
                ActorStatistics.STAMINA,
                StatisticOperations.INCREASE,
                1
        );
        return farmer + "'s maximum stamina increases by 1.";
    }

    /**
     * Removes this Parsnip from the Farmer's inventory after it has
     * successfully been used to feed an animal.
     *
     * @param farmer the Farmer carrying the Parsnip
     */
    @Override
    public void removeAfterFeeding(Actor farmer){
        farmer.getInventory().remove(this);
    }

    /**
     * Returns feeding actions available when this Parsnip is carried
     * near another actor.
     *
     * <p>If the other actor supports the Feedable capability, a
     * FeedAction using this Parsnip is added to the available actions.</p>
     *
     * @param otherActor the actor that may be fed
     * @param location the location of the other actor
     * @return the available actions involving this Parsnip and the other actor
     */
    @Override
    public ActionList allowableActions(Actor otherActor, Location location){

        ActionList actions = new ActionList();

        otherActor.asCapability(Feedable.class).ifPresent(
                feedable -> actions.add(new FeedAction(feedable, this, location))
                );

        return actions;

    }

    /**
     * Returns the product name of this Parsnip.
     *
     * @return the name "Parsnip"
     */
    @Override
    public String getProductName() {
        return "Parsnip";
    }

    /**
     * Returns the category used when selecting this Parsnip for cooking.
     *
     * @return the Vegetable category
     */
    @Override
    public IngredientCategory getCategory() {
        return IngredientCategory.VEGETABLE;
    }

    /**
     * Plants a Parsnip Crop on one random adjacent planting ground.
     *
     * <p>If no eligible tile exists, no crop is planted. IngredientEntry
     * still removes this Parsnip as part of the cooking workflow.
     *
     * @param context the context for the cooking attempt
     * @return a planting description, or an empty string if no tile qualifies
     */
    @Override
    public String applyCookingEffect(CookingContext context) {
        Optional<Location> destination = context.randomAdjacent(
                location -> location.getGroundAs(PlantableGround.class) != null
        );

        if (destination.isEmpty()) {
            return "";
        }

        Location plantingLocation = destination.get();
        plantingLocation.setGround(new ParsnipCrop());

        return "A Parsnip Crop grows at " + plantingLocation + ".";
    }
}
