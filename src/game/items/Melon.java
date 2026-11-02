package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.capabilities.IngredientCategory;
import game.coatings.Coating;
import game.coatings.MelonCoating;
import game.cooking.CookingContext;
import game.effects.Explosion;
import game.interfaces.CoatingSource;
import game.interfaces.Ingredient;

/**
 * Represents a Melon that can be consumed by an actor.
 *
 * <p>Consuming a Melon increases the actor's maximum health
 * by 10 points.</p>
 *
 * <p>A Melon can also be used as a coating source (REQ 4). Coating a
 * weapon with a Melon makes it explode on impact, and the Melon is
 * used up in the process.</p>
 *
 * @author <Chin Kai Xuan> <35730374>
 * @author <Lee Le Si> <34599223>
 * @author Surya Muni Peragas 35651628
 * @version 1.2.0
 */
public class Melon extends ConsumableItem implements CoatingSource, Ingredient {

    private static final double COOKING_BURST_CHANCE = 0.5;
    private static final int COOKING_BURST_DAMAGE = 15;

    /**
     * Amount by which maximum health is increased when consumed.
     */
    private static final int MAX_HEALTH_INCREASE = 10;

    /**
     * Constructor for Melon.
     */
    public Melon() {
        super("Melon", 'm');
    }

    /**
     * Applies the consumption effect of this Melon.
     *
     * <p>The actor's maximum health is increased by 10 points.</p>
     *
     * @param actor the actor consuming the Melon
     * @return a description of the consumption result
     */
    @Override
    protected String applyConsumptionEffect(Actor actor) {
        actor.modifyStatisticMaximum(
                ActorStatistics.HEALTH,
                StatisticOperations.INCREASE,
                MAX_HEALTH_INCREASE
        );

        return actor + " consumes the Melon and increases maximum health by 10.";
    }

    /**
     * A melon can coat a weapon so it explodes on impact (REQ 4).
     *
     * @return a new melon coating
     */
    @Override
    public Coating getCoating(){
        return new MelonCoating();
    }

    /**
     * Applying a melon as a coating uses it up.
     *
     * @param actor          the actor who applied the coating
     * @param sourceLocation where the melon lies, or null if it was carried
     */
    @Override
    public void onCoatingApplied(Actor actor, Location sourceLocation){
        if (sourceLocation == null){
            actor.getInventory().remove(this);
        }else{
            sourceLocation.removeItem(this);
        }
    }

    /**
     * Returns the category used when selecting this Melon for cooking.
     *
     * @return the Fruit category
     */
    @Override
    public IngredientCategory getCategory() {
        return IngredientCategory.FRUIT;
    }

    /**
     * Applies this Melon's cooking effect.
     *
     * <p>A successful burst damages every actor adjacent to the station.
     * Inventory removal is handled by IngredientEntry regardless of
     * whether the burst occurs.
     *
     * @param context the context for the cooking attempt
     * @return a burst description, or an empty string if no burst occurs
     */
    @Override
    public String applyCookingEffect(CookingContext context) {
        if (!context.chance(COOKING_BURST_CHANCE)) {
            return "";
        }

        Explosion explosion = new Explosion(COOKING_BURST_DAMAGE);
        explosion.explodeAround(context.getStationLocation());

        return "Melon bursts, dealing " + COOKING_BURST_DAMAGE + " damage to each actor adjacent to the cooking station.";
    }
}