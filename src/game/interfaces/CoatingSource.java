package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.coatings.Coating;

/**
 * Anything that can coat a {@link Coatable}, e.g. fire, a melon, or charged ground.
 *
 * <p>A coating can be applied when the actor is carrying the source or is standing
 * next to it. Any item or ground can become a coating source by implementing this
 * interface; no other class needs to change.</p>
 *
 * @author <Lee Le Si> <34599223>
 * @version 1.0.0
 */
public interface CoatingSource {
    /**
     * Gives the coating this source applies to a weapon.
     *
     * @return the coating this source gives
     */
    Coating getCoating();
    /**
     * Whether this source can be used right now (e.g. ground only while charged).
     *
     * <p>By default a source is always usable.</p>
     *
     * @return true if a coating can be taken from this source
     */
    default boolean canProvideCoating() {
        return true;
    }
    /**
     * Called after a coating has been taken from this source. Sources that are used
     * up (e.g. a melon) remove themselves here. Does nothing by default.
     *
     * @param actor          the actor who applied the coating
     * @param sourceLocation where the source lies, or null if the actor is carrying it
     */
    default void onCoatingApplied(Actor actor, Location sourceLocation) {
        // most sources are not used up, so nothing happens by default

    }

}
