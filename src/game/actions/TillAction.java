package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.capabilities.Ability;
import game.capabilities.Tillable;

/**
 * Represents an action that allows an actor to till a tillable object.
 * The action succeeds only when the actor has a tilling tool
 * and sufficient stamina.
 *
 */
public class TillAction extends Action {

    private static final int STAMINA_COST = 2;

    private final Tillable tillable;
    private final Location location;

    /**
     * Constructs a new till action.
     *
     * @param tillable the object to be tilled
     * @param location the location of the object to be tilled
     */
    public TillAction(Tillable tillable, Location location) {
        this.tillable = tillable;
        this.location = location;
    }

    /**
     * Executes the tilling action.
     * If the actor has a tilling tool and at least two stamina points,
     * two stamina points are consumed and the target is tilled. Otherwise,
     * the action fails without consuming stamina or modifying the target.
     *
     * @param actor the actor performing the action
     * @param map the map containing the actor
     * @return a description of the result of the action
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (!actor.hasAbility(Ability.TILL)) {
            return "You don't have a Hoe to till dirt.";
        }

        if (actor.getStatistic(ActorStatistics.STAMINA) < STAMINA_COST) {
            return "You are too tired to till dirt.";
        }

        actor.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.DECREASE, STAMINA_COST);

        return tillable.tilledBy(actor, location);
    }

    /**
     * Returns the description of this action for the action menu.
     *
     * @param actor the actor performing the action
     * @return the menu description
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " tills " + tillable;
    }
}
