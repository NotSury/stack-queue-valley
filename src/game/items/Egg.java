package game.items;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actors.Chicken;
import game.capabilities.FarmerStatistic;
import game.capabilities.IngredientCategory;
import game.cooking.CookingContext;
import game.interfaces.Ingredient;
import game.interfaces.Sellable;

import java.util.Optional;
import java.util.Random;

import java.util.ArrayList;
import java.util.List;

/**
 * Egg represents an item that can be produced by a Chicken after feeding.
 *
 * <p>An Egg is a portable item that can be carried in an actor's
 * inventory or dropped onto the game map.</p>
 *
 * @author Lee Le Si <34599223>
 * @author Vanessa Liau Jia Yee <34599398>
 * @author Surya Muni Peragas 35651628
 * @version 1.3.0
 */
public class Egg extends Item implements Sellable, Ingredient
{
    private static final int SELL_PRICE = 40;
    private static final double CHICKEN_CHANCE = 0.5;
    private static final double COOKING_HATCH_CHANCE = 0.25;

    /**
     * Constructor for the Egg class.
     *
     * <p>Creates a portable Egg with the display character '0'.</p>
     */
    public Egg() {
        super("Egg",'0');
        this.makePortable();
    }

    /**
     * Returns the selling price of Egg.
     *
     * @return the selling price in coins
     */
    @Override
    public int getSellPrice()
    {
        return SELL_PRICE;
    }

    /**
     * Sells the Egg to the Merchant.
     *
     * <p>The Egg is removed from the seller and the seller gains 40 coins.
     * Then, with a 50% chance, a Chicken appears on a random tile next to the
     * Merchant. Only tiles the Chicken is allowed to enter are used (no actor
     * on it, and not a blocking ground such as a Rock or a Wall). If there is
     * no such tile, no Chicken appears.</p>
     *
     * @param seller the actor selling the Egg
     * @param merchant the Merchant buying the Egg
     * @param map the current game map
     * @return a message describing the result of the sale
     */
    @Override
    public String sell(Actor seller, Actor merchant, GameMap map) {
        seller.getInventory().remove(this);

        seller.modifyStatistic(FarmerStatistic.COINS,
                StatisticOperations.INCREASE, SELL_PRICE);

        if (Math.random() < CHICKEN_CHANCE) {
            Chicken chicken = new Chicken();
            Location merchantLocation = map.locationOf(merchant);

            List<Location> availableLocations = new ArrayList<>();

            for (Exit exit : merchantLocation.getExits()) {
                Location location = exit.getDestination();

                if (location.canActorEnter(chicken)) {
                    availableLocations.add(location);
                }
            }

            if (!availableLocations.isEmpty()) {
                Random locationRandom = new Random();

                int randomIndex =
                        locationRandom.nextInt(availableLocations.size());

                Location chickenLocation = availableLocations.get(randomIndex);

                try {
                    chickenLocation.addActor(chicken);

                    return seller + " sold Egg for " + SELL_PRICE +
                            " coins and a Chicken appeared.";
                } catch (GameEngineException e)
                {
                    return seller + " sold Egg for " + SELL_PRICE +
                            " coins.";
                }
            }
        }
        return seller + " sold Egg for " + SELL_PRICE + " coins.";
    }

    /**
     * Returns the category used when selecting this Egg for cooking.
     *
     * @return the Animal Product category
     */
    @Override
    public IngredientCategory getCategory() {
        return IngredientCategory.ANIMAL_PRODUCT;
    }

    /**
     * Attempts to hatch a Chicken beside the cooking station.
     *
     * <p>The selected tile must be enterable by the Chicken. IngredientEntry
     * removes the Egg regardless of whether hatching succeeds.
     *
     * @param context the context for the cooking attempt
     * @return a hatching description, a placement-failure message,
     *     or an empty string if no hatching occurs
     */
    @Override
    public String applyCookingEffect(CookingContext context) {
        if (!context.chance(COOKING_HATCH_CHANCE)) {
            return "";
        }

        Chicken chicken = new Chicken();

        Optional<Location> destination = context.randomAdjacent(
                location -> location.canActorEnter(chicken)
        );

        if (destination.isEmpty()) {
            return "";
        }

        Location hatchLocation = destination.get();

        try {
            hatchLocation.addActor(chicken);
        } catch (GameEngineException exception) {
            return "The Egg could not hatch a Chicken";
        }

        return "A Chicken hatches at " + hatchLocation + ".";
    }
}