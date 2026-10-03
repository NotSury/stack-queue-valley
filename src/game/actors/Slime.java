package game.actors;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.weapons.CreatureWeapon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Slime extends HostileCreature{

    private static final int SPLIT_CHANCE = 50;
    private final Random random = new Random();

    public Slime(){
        super("Slime",'S',150,new CreatureWeapon(15,"slams", 50,"body"));

    }

    @Override
    public String respondToAttack(Actor attacker, GameMap map){
        if (random.nextInt(100) >= SPLIT_CHANCE){
            return "";
        }

        Slime newSlime = new Slime();

        List<Location> freeSpots = new ArrayList<>();
        for (Exit exit : map.locationOf(this).getExits()){
            if (exit.getDestination().canActorEnter(newSlime)){
                freeSpots.add(exit.getDestination());
            }
        }
        if (freeSpots.isEmpty()){
            return "";
        }

        try{
            freeSpots.get(random.nextInt(freeSpots.size())).addActor(newSlime);
        }catch (GameEngineException e){
            return "";
        }
        return this + " splits into another slime";
    }
}
