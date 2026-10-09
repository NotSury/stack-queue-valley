package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Stone;

import java.util.ArrayList;
import java.util.List;

/**
 * A breakable Rock found in the mines.
 *
 * <p>Breaking a Rock drops a Stone and replaces the Rock with Dirt.
 * There is also a 50 percent chance that a shockwave moves the actor to
 * a random valid location adjacent to the Rock.</p>
 *
 * @author Khoo Lynn 34598901
 * @version 1.0.0
 */
public class Rock extends BreakableGround{

    /**
     * Creates a Rock using its display character and name.
     */
    public Rock(){
        super('#', "Rock");
    }

    /**
     * <p>Breaks the Rock, drops a Stone and replaces the Rock with Dirt. There is
     * a 50% chance that the actor is moved to a random valid adjacent location by
     * a shockwave.</p>
     *
     * @param actor actor breaking the object.
     * @param location location of the object being broken.
     * @return a message describing the result of breaking the Rock.
     */
    @Override
    public String breakBy(Actor actor, Location location){
        location.addItem(new Stone());
        location.setGround(new Dirt());

        if (Math.random() < 0.5){
            Location destination = getRandomShockwaveDestination(actor, location);

            if (destination != null){
                location.map().moveActor(actor, destination);
                return actor + " breaks the Rock and is thrown by a shockwave";
            }
        }

        return actor + " breaks the Rock";
    }

    private Location getRandomShockwaveDestination(Actor actor, Location rockLocation){
        List<Location> validLocations = new ArrayList<>();

        for (Exit exit : rockLocation.getExits()){
            Location destination = exit.getDestination();

            if (destination.canActorEnter(actor)){
                validLocations.add(destination);
            }
        }

        if (validLocations.isEmpty()){
            return null;
        }

        int randomIndex = (int) (Math.random() * validLocations.size());
        return validLocations.get(randomIndex);
    }
}
