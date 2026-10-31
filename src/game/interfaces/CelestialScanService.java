package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Defines celestial scanning operations available to an Observatory.
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public interface CelestialScanService {

    /**
     * Checks whether the Observatory can begin a new scan.
     *
     * @return true if no previous field is active
     */
    boolean canScan();

    /**
     * Attempts to activate a NASA-driven celestial scan.
     *
     * @param actor actor starting the scan
     * @param observatory location of the Observatory
     * @return result of the attempted scan
     */
    String scan(Actor actor, Location observatory);
}
