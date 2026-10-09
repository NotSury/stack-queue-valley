package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.WebPlacement;
import game.navigation.GridDistance;

/**
 * Places one web on an adjacent eligible tile without moving the Spider.
 * @author Surya Muni Peragas 35651628
 * @version 1.0.0
 */
public class PlaceWebAction extends Action {

    private final Location destination;
    private final WebPlacement network;

    /**
     * Creates an action for the owning Spider's web network.
     *
     * @param destination the intended adjacent web site
     * @param network the acting Spider's web network
     */
    public PlaceWebAction(Location destination, WebPlacement network) {
        this.destination = destination;
        this.network = network;
    }

    /**
     * Rechecks adjacency and placement eligibility before adding the web.
     *
     * @param actor the Spider placing the web
     * @param map the Spider's current map
     * @return a description of the result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (!map.contains(actor)
                || destination.map() != map
                || GridDistance.between(
                map.locationOf(actor), destination) != 1
                || !network.placeAt(destination)) {
            return actor + " cannot weave a web there.";
        }

        return actor + " weaves a web nearby.";
    }

    /**
     * Describes this action without exposing a hidden web's coordinates.
     *
     * @param actor the Spider placing the web
     * @return the action description
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " weaves a web nearby";
    }
}
