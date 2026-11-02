package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.cooking.CookingContext;
import game.cooking.Recipe;

import java.util.random.RandomGenerator;

/**
 * An action that lets an actor cook a selected recipe
 * at a cooking station.
 *
 * <p>The recipe handles ingredient validation, cooking effects
 * and adding the resulting dish to the inventory.</p>
 *
 * @author Surya Muni Peragas 35651628
 * @version 1.0.0
 */
public class CookAction extends Action {

    private final Recipe recipe;
    private final Location stationLocation;
    private final RandomGenerator random;

    /**
     * Creates an action for cooking one recipe.
     *
     * @param recipe the selected recipe
     * @param stationLocation the cooking station's location
     * @param random the generator used for cooking outcomes
     */
    public CookAction(Recipe recipe, Location stationLocation, RandomGenerator random) {
        this.recipe = recipe;
        this.stationLocation = stationLocation;
        this.random = random;
    }

    /**
     * Runs the selected recipe using the actor and station location.
     *
     * @param actor the actor attempting to cook
     * @param map the map where the action is performed
     * @return a description of the cooking result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        CookingContext context = new CookingContext(actor, stationLocation, random);

        return recipe.cook(context);
    }

    /**
     * Describes the selected recipe in the action menu.
     *
     * @param actor the actor attempting to cook
     * @return the menu description
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " cooks " + recipe.getName();
    }
}
