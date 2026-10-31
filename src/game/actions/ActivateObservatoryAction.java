package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import game.interfaces.CelestialScanService;

import java.util.Objects;

/**
 * Allows the Farmer to activate an Observatory.
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public final class ActivateObservatoryAction extends Action {

    private final CelestialScanService scans;
    private final Location observatory;

    /**
     * Creates an Observatory activation action.
     *
     * @param scans celestial scanning service
     * @param observatory location of the Observatory
     */
    public ActivateObservatoryAction(
            CelestialScanService scans,
            Location observatory) {

        this.scans = Objects.requireNonNull(scans, "scans");
        this.observatory = Objects.requireNonNull(
                observatory, "observatory");
    }

    /**
     * Executes a celestial scan.
     *
     * @param actor actor performing the action
     * @param map current game map
     * @return result of the scan
     */
    @Override
    public String execute(Actor actor, GameMap map) {

        if (map != observatory.map()) {
            return "The Observatory is on another map.";
        }

        return scans.scan(actor, observatory);
    }

    /**
     * Describes the action in the Farmer's menu.
     *
     * @param actor actor performing the action
     * @return menu description
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " scans the skies at the Observatory";
    }
}
