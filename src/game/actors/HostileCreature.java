package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.behaviours.AttackBehaviour;
import game.behaviours.WanderBehaviour;
import game.interfaces.Hostile;
import game.weapons.CreatureWeapon;

import java.util.Map;
import java.util.TreeMap;

public abstract class HostileCreature extends Actor implements Hostile {

    private final Map<Integer, Behaviour<Actor, Action>>  behaviours = new TreeMap<>();

    protected HostileCreature(String name, char displayChar, int hitPoints, CreatureWeapon weapon){
        super(name, displayChar, hitPoints);
        setIntrinsicWeapon(weapon);
        behaviours.put(0, new AttackBehaviour());
        behaviours.put(1, new WanderBehaviour());
    }

    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (!isConscious()) {
            display.println(unconscious(map));
            return new DoNothingAction();
        }

        for (Behaviour<Actor, Action> behaviour : behaviours.values()) {
            Action action = behaviour.operate(this, map.locationOf(this));
            if (action != null) {
                return action;
            }
        }
        return new DoNothingAction();
        }

    @Override
    public String respondToAttack(Actor attacker, GameMap map) {
        return "";
    }
}
