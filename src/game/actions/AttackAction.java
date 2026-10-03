package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.weapons.Weapon;
import game.interfaces.Hostile;

public class AttackAction extends Action {

    private final Actor target;
    private final String direction;
    private final Weapon weapon;

    public AttackAction(Actor target, String direction, Weapon weapon){
        this.target = target;
        this.direction = direction;
        this.weapon = weapon;
    }

    @Override
    public String execute(Actor actor, GameMap map){
        StringBuilder result = new StringBuilder(weapon.attack(actor, target, map));

        target.asCapability(Hostile.class).ifPresent(hostile -> {
            String reaction = hostile.respondToAttack(actor, map);
            if (!reaction.isEmpty()){
                result.append(System.lineSeparator()).append(reaction);
            }
        });

        if (!target.isConscious()){
            result.append(System.lineSeparator()).append(target.unconscious(actor, map));
        }
        return result.toString();
    }

    @Override
    public String menuDescription(Actor actor){
        return actor + " attacks " + target + " at " + direction + " with " + weapon;
    }
}
