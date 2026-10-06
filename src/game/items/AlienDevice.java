package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TeleportAction;
import game.interfaces.Teleportable;
import game.teleportation.TeleportDestination;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * <p>A portable alien device that allows an actor to teleport to one of several
 * fixed destinations.
 *
 * The device has a 50% chance of malfunctioning. When this occurs, the actor is
 * moved to a random valid location on their current map instead of the selected
 * destination.</p>
 *
 * @author Khoo Lynn 34598901
 * @version 1.0.0
 */
public class AlienDevice extends Item implements Teleportable {
    private static final double MALFUNCTION_CHANCE = 0.5;
    private final List<TeleportDestination> destinations;
    private final Random random = new Random();

    /**
     * Creates an Alien Device with a collection of teleport destinations.
     *
     * @param destinations destinations available through this device.
     */
    public AlienDevice(List<TeleportDestination> destinations){
        super("Alien Device", 'A');
        this.makePortable();
        this.destinations = new ArrayList<>(destinations);
    }

    private Location getRandomLocation(Actor actor, GameMap map){
        List<Location> validLocations = new ArrayList<>();

        for (int x : map.getXRange()){
            for (int y : map.getYRange()){
                Location location = map.at(x, y);

                if (location.canActorEnter(actor)){
                    validLocations.add(location);
                }
            }
        }
        return validLocations.get(random.nextInt(validLocations.size()));
    }

    /**
     * <p>Provides a teleport action for each destination while the device is carried
     * by an actor.</p>
     *
     * @param owner the actor that owns the item
     * @param map the map where the actor is performing the action on
     * @return available teleport actions.
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map){
        ActionList actions = new ActionList();

        for (TeleportDestination destination : destinations){
            actions.add(new TeleportAction(this, destination));
        }

        return actions;
    }

    /**
     * Attempts to teleport the actor to the selected destination.
     *
     * <p>There is a 50% chance that the device malfunction and moves the actor
     * to a random valid location on their current map instead.</p>
     *
     * @param actor actor being teleported.
     * @param map current map of the actor.
     * @param destination selected teleport destination.
     * @return a description of the teleportation result.
     */
    @Override
    public String teleport(Actor actor, GameMap map, TeleportDestination destination){
        if (random.nextDouble() < MALFUNCTION_CHANCE){
            Location randomLocation = getRandomLocation(actor, map);
            map.moveActor(actor, randomLocation);

            return "The Alien Device malfunctions and sends " + actor + " to a random location.";
        }

        Location destinationLocation = destination.getLocation();
        destinationLocation.map().moveActor(actor, destinationLocation);

        return actor + " uses the Alien Device to travel to " + destination + ".";
    }
}
