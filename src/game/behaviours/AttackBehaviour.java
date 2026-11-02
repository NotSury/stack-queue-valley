package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.AttackAction;
import game.capabilities.CombatStatus;

/**
 * Attacks an adjacent actor that hostile creatures go after (the farmer),
 * using the attacker's intrinsic weapon.
 *
 * @author <Lee Le Si> <34599223>
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.2.0
 */
public class AttackBehaviour implements Behaviour<Actor, Action> {
    /**
     * Looks at every adjacent tile for an actor with the HOSTILE_TARGET ability
     * and attacks the first one found.
     *
     * @param actor    the creature deciding what to do
     * @param location the creature's current location
     * @return an AttackAction against an adjacent target, or null if there is none
     *         (so the creature's next behaviour, e.g. wandering, is tried instead)
     */
    @Override
    public Action operate(Actor actor, Location location){
        for (Exit exit : location.getExits()){
            Location destination = exit.getDestination();
            if (destination.containsAnActor()
            && destination.getActor().hasAbility(CombatStatus.HOSTILE_TARGET)){
                return new AttackAction(destination.getActor(), exit.getName(),
                        actor.getIntrinsicWeapon());
            }
        }
        return null;
    }

}
