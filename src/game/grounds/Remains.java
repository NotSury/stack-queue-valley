package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.RaiseDeadAction;
import game.capabilities.ActorRole;
import game.capabilities.NecromancyCapability;
import game.interfaces.Cursed;

/**
 * The remains of a dead cursed creature (REQ3). Remains stay on the map and
 * remember which creature died there and what ground was underneath, so the
 * Farmer can raise the creature as an undead minion and the original ground is
 * restored.
 *
 * <p>Remains count as cursed ground, so no other curse can cover them.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.1.0
 */
public class Remains extends Ground {

    private final Ground previous;
    private final Cursed deceased;

    /**
     * Creates remains on top of the existing ground.
     *
     * @param previous the ground underneath, restored after the remains are raised
     * @param deceased the cursed creature that died here
     */
    public Remains(Ground previous, Cursed deceased) {
        super('%', "Remains");
        this.previous = previous;
        this.deceased = deceased;
        this.enableAbility(NecromancyCapability.CURSED_GROUND);
    }

    /**
     * Lets the Farmer raise these remains when nobody is standing on them.
     *
     * @param actor     the actor next to the remains
     * @param location  the location of the remains
     * @param direction where the remains are from the actor
     * @return a raise action for the Farmer, or an empty list otherwise
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        if (actor.hasAbility(ActorRole.FARMER) && !location.containsAnActor()) {
            actions.add(new RaiseDeadAction(deceased, previous, location, direction));
        }
        return actions;
    }
}