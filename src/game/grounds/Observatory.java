package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

import game.actions.ActivateObservatoryAction;
import game.capabilities.ActorRole;
import game.interfaces.CelestialScanService;

import java.util.Objects;

/**
 * A protected Observatory used to scan NASA asteroid data.
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public final class Observatory extends Ground {

    private final CelestialScanService scans;

    /**
     * Creates an Observatory connected to a scanning service.
     *
     * @param scans celestial scanning service
     */
    public Observatory(CelestialScanService scans) {
        super('O', "Observatory");

        this.scans = Objects.requireNonNull(scans, "scans");
    }

    /**
     * Offers a scan to the Farmer when no field is active.
     *
     * @param actor actor interacting with the Observatory
     * @param location Observatory location
     * @param direction direction relative to the actor
     * @return available Observatory actions
     */
    @Override
    public ActionList allowableActions(
            Actor actor,
            Location location,
            String direction) {

        ActionList actions = new ActionList();

        if (actor.hasAbility(ActorRole.FARMER)
                && scans.canScan()) {

            actions.add(
                    new ActivateObservatoryAction(scans, location)
            );
        }

        return actions;
    }
}
