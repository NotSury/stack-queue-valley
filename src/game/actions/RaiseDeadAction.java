package game.actions;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actors.Undead;
import game.capabilities.NecromancyCapability;
import game.interfaces.Cursed;

/**
 * The Farmer raises the remains of a cursed creature as an undead minion (REQ3).
 *
 * <p>Raising costs stamina, and an actor can only command one minion at a
 * time. The dead creature decides its own undead form through
 * {@link Cursed#rise}, so this action never needs to know which creature it
 * is raising.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class RaiseDeadAction extends Action {

    /** Stamina needed to raise the dead. */
    private static final int STAMINA_COST = 5;

    /** The creature whose remains are being raised. */
    private final Cursed deceased;

    /** The ground under the remains, restored once they are raised. */
    private final Ground previous;

    /** Where the remains lie. */
    private final Location remains;

    /** Where the remains are, for the menu (e.g. "North"). */
    private final String direction;

    /**
     * Creates the action.
     *
     * @param deceased  the creature whose remains are being raised
     * @param previous  the ground under the remains
     * @param remains   where the remains lie
     * @param direction where the remains are, for the menu
     */
    public RaiseDeadAction(Cursed deceased, Ground previous, Location remains, String direction) {
        this.deceased = deceased;
        this.previous = previous;
        this.remains = remains;
        this.direction = direction;
    }

    /**
     * Raises the minion on the remains, clears the remains and spends stamina.
     *
     * @param actor the actor raising the dead
     * @param map   the map the action happens on
     * @return a description of what happened
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (actor.hasAbility(NecromancyCapability.HAS_MINION)) {
            return actor + " already commands a minion.";
        }
        if (actor.getStatistic(ActorStatistics.STAMINA) < STAMINA_COST) {
            return actor + " is too tired to raise the dead.";
        }

        Undead minion = deceased.rise(actor);
        try {
            remains.addActor(minion);
        } catch (GameEngineException e) {
            return "Something is in the way of the remains.";
        }
        remains.setGround(previous);
        actor.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.DECREASE, STAMINA_COST);
        actor.enableAbility(NecromancyCapability.HAS_MINION);
        return actor + " raises a " + minion + " from the remains.";
    }

    /**
     * Text shown for this action in the menu.
     *
     * @param actor the actor who would raise the dead
     * @return e.g. "Farmer Bob raises the remains at North (5 stamina)"
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " raises the remains at " + direction + " (" + STAMINA_COST + " stamina)";
    }
}