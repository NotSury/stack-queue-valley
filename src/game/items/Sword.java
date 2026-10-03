package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.Weapon;
import game.actions.AttackAction;
import game.coatings.Coating;
import game.coatings.CoatingSourceScanner;
import game.interfaces.Coatable;
import game.interfaces.Hostile;

import java.util.Random;

public class Sword extends Item implements Weapon, Coatable {
    private static final int DAMAGE = 20;
    private static final int HIT_RATE = 85;

    private final Random random = new Random();
    private Coating coating;

    public Sword(){
        super("Sword",'t');
    }

    @Override
    public String attack(Actor attacker, Actor target, GameMap map){
        String result;
        if (random.nextInt(100) < HIT_RATE){
            target.hurt(DAMAGE);
            result = attacker + " slashes " + target + " for " + DAMAGE + " damage!";
        }else{
            result = attacker + " misses " + target + ".";
        }
        return result;
    }

    @Override
    public void setCoating(Coating coating) {
        this.coating = coating;
    }

    @Override
    public ActionList allowableActions(Actor otherActor, Location location){
        ActionList actions = new ActionList();
        if (otherActor.asCapability(Hostile.class).isPresent()){
            actions.add(new AttackAction(otherActor, location.toString(),this));
        }
        return actions;
    }

    @Override
    public ActionList allowableActions(Actor owner, GameMap map){
        return CoatingSourceScanner.coatActions(this, owner, map);
    }

    @Override
    public String toString(){
        return coating == null ? "Sword" : "Sword (" + coating + "-coated)";
    }
}
