package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.interfaces.Tillable;
import game.capabilities.ToolAbility;
/**
 * TillingAction represents an action that allows an actor to till
 * a ground that supports the Tillable behaviour.
 *
 * <p>The action requires the actor to have the TILLING ability and at
 * least 2 stamina points. When successful, the stamina cost is deducted
 * before the target's tilling behaviour is performed.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class TillingAction extends Action {
    /**
     * The tillable target affected by this action.
     */
    private final Tillable target;
    /**
     * The location of the ground being tilled.
     */
    private final Location location;
    /**
     * Constructor for the TillingAction class.
     *
     * @param target the tillable target affected by this action
     * @param location the location of the ground being tilled
     */
    public TillingAction(Tillable target, Location location) {
        this.target = target;
        this.location = location;
    }


    /**
     * Executes the tilling action.
     *
     * <p>The action succeeds only if the actor has the TILLING ability
     * and at least 2 stamina points. When successful, 2 stamina points are
     * deducted and the target's tilling behaviour is applied.</p>
     *
     * <p>If either requirement is not satisfied, the target and the actor's
     * stamina remain unchanged.</p>
     *
     * @param actor the actor performing the tilling action
     * @param map the game map where the action is performed
     * @return a description of the result of the tilling action
     */
    @Override
    public String execute(Actor actor, GameMap map) {

        if (!actor.hasAbility(ToolAbility.TILLING) || actor.getStatistic(ActorStatistics.STAMINA) < 2) {
            return "You are either too tired to till dirt or don't have a Hoe to till dirt.";
        }

        actor.modifyStatistic(
                ActorStatistics.STAMINA,
                StatisticOperations.DECREASE,
                2
        );

        target.till(location);
        return actor + " tills the dirt into soil.";
    }

    /**
     * Returns the description of this action for display in the action menu.
     *
     * @param actor the actor performing the action
     * @return the menu description of the tilling action
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " tills Dirt ";
    }
}
