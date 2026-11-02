package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.CookAction;
import game.cooking.Recipe;

import java.util.List;
import java.util.random.RandomGenerator;

/**
 * An impassable cooking station that offers its recipes
 * to adjacent actors.
 *
 * <p>Every recipe is offered regardless of the actor's inventory.
 * Ingredient availability is checked when cooking is attempted.</p>
 *
 * @author Surya Muni Peragas 35651628
 * @version 1.0.0
 */
public class CookingStation extends Ground {

    private final List<Recipe> recipes;
    private final RandomGenerator random;

    /**
     * Creates a cooking station with its available recipes.
     *
     * @param recipes the recipes offered by this station
     * @param random the generator used for cooking outcomes
     */
    public CookingStation(List<Recipe> recipes, RandomGenerator random) {
        super('K', "Cooking Station");
        this.recipes = List.copyOf(recipes);
        this.random = random;
    }

    /**
     * Provides one cooking action per recipe to an adjacent actor.
     *
     * @param actor the Actor interacting with the station
     * @param location the location of the cooking station
     * @param direction the direction of the station from the actor
     * @return the available cooking actions
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();

        if (!direction.isEmpty()) {
            for (Recipe recipe : recipes) {
                actions.add(new CookAction(recipe, location, random));
            }
        }

        return actions;
    }

    /**
     * Prevents actors from entering the cooking station's tile.
     *
     * @param actor the actor attempting to enter
     * @return false because the station is impassable
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }
}
