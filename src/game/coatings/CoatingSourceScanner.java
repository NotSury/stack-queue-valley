package game.coatings;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.CoatAction;
import game.interfaces.Coatable;
import game.interfaces.CoatingSource;

import java.util.HashSet;
import java.util.Set;

public class CoatingSourceScanner {

    private CoatingSourceScanner(){}

    public static ActionList coatActions(Coatable coatable, Actor actor, GameMap map){
        ActionList actions = new ActionList();
        Set<String> offered = new HashSet<>();

        for (CoatingSource source : actor.getInventory().getItemsAs(CoatingSource.class)){
            offer(actions, offered, coatable, source, null);
        }

        for (Exit exit : map.locationOf(actor).getExits()){
            Location nearby = exit.getDestination();

            for (CoatingSource source : nearby.getItemsAs(CoatingSource.class)){
                offer(actions, offered, coatable, source, nearby);
            }

            CoatingSource ground = nearby.getGroundAs(CoatingSource.class);
            if (ground != null){
                offer(actions, offered, coatable, ground, nearby);
            }
        }
        return actions;
    }

    private static void offer(ActionList actions, Set<String> offered, Coatable coatable,
                              CoatingSource source, Location sourceLocation){
        if (!source.canProvideCoating()){
            return;
        }
        String coatingName = source.getCoating().toString();
        if (offered.add(coatingName)){
            actions.add(new CoatAction(coatable, source, sourceLocation));
        }
    }
}
