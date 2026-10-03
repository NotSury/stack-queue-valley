package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.AttackAction;
import game.capabilities.CombatStatus;

public class AttackBehaviour implements Behaviour<Actor, Action> {

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
