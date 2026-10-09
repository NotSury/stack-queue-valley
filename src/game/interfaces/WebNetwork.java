package game.interfaces;

import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.actors.Actor;
/**
 * Manages the webs belonging to one creature.
 *
 * <p>Also exposes the latest web-trigger alert through
 * {@link WebAlertSource}.</p>
 *
 * @author Surya Muni Peragas 35651628
 * @author Lee Le Si 34599223
 * @version 1.1.0
 */
public interface WebNetwork extends WebPlacement, WebAlertSource {

    /**
     * Counts active owned webs.
     *
     * @return the number of active owned webs
     */
    int countActiveWebs();

    /**
     * Places an entry web at an eligible site.
     * At capacity, replaces the shortest-lived eligible owned web.
     * Guard webs are protected from replacement.
     *
     * @param location the proposed placement location
     * @return true when a web was placed
     */
    boolean placeEntryWeb(Location location);

    /**
     * Checks whether at least one adjacent entry web can be placed.
     * Includes replacement eligibility without changing any webs.
     *
     * @param origin the centre of the proposed entry effect
     * @return true when an adjacent entry placement can succeed
     */
    boolean canPlaceEntryWebs(Location origin);

    /**
     * Reinforces existing guards and places missing guards where possible.
     * Existing guards are preserved even when their count exceeds the target.
     *
     * @param guardWebs the desired minimum number of guard webs
     * @return the number of guards reinforced or placed
     */
    int maintainGuardWebs(int guardWebs);

    /**
     * Reinforces nearby owned webs up to their maximum lifetime.
     *
     * @param origin the centre of the reinforcement area
     * @param radius the maximum reinforcement distance
     * @param turns the additional lifetime ticks
     * @return the number of webs whose lifetime increased
     */
    int reinforceNearby(Location origin, int radius, int turns);

    /**
     * Temporarily reveals and reinforces owned webs.
     * When no webs remain, attempts defensive placement, followed
     * by the existing non-stacking binding fallback if placement fails.
     *
     * @param target the engaged Farmer
     * @param turns the additional lifetime ticks
     * @return a description of the effects
     */
    String revealAndReinforceAll(Actor target, int turns);

    /**
     * Places adjacent entry webs in deterministic order.
     * A focus prefers sites behind the direction towards that location.
     * Without a focus, sites use row then column ordering.
     *
     * @param origin the placement centre
     * @param count the maximum number of webs to place
     * @param focus the alert destination, or null
     * @return the number of webs placed
     */
    int placeEntryWebs(Location origin, int count, Location focus);

    /**
     * Returns the latest uncleared alert location.
     *
     * @return the alert location, or null
     */
    Location getAlertLocation();

    /**
     * Returns the latest uncleared alert version.
     *
     * @return the event version, or zero when no alert exists
     */
    long getAlertVersion();
}
