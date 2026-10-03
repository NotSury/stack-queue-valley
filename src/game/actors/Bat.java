package game.actors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Strawberry;
import game.weapons.CreatureWeapon;

import java.util.List;
import java.util.Random;

public class Bat extends HostileCreature{

    private final Random random = new Random();

    public Bat(){
        super("Bat",'b', 50, new CreatureWeapon(35, "bites", 90, "fangs"));
    }

    @Override
    public String respondToAttack(Actor attacker, GameMap map){
        List<Exit> exits = map.locationOf(this).getExits();
        if(exits.isEmpty()){
            return "";
        }

        Location dropSpot = exits.get(random.nextInt(exits.size())).getDestination();

        dropSpot.addItem(new Strawberry(false));
        return this + " drops a strawberry.";
    }
}
