package game.interfaces;

import game.coatings.Coating;

/**
 * Something whose effect can be changed by applying a coating to it,
 * e.g. the sword.
 *
 * <p>Holds at most one coating at a time: applying a new coating replaces the
 * old one, and a coating stays until it is replaced.</p>
 *
 * @author <Lee Le Si> <34599223>
 * @version 1.0.0
 */
public interface Coatable {
    /**
     * Replaces the current coating with a new one.
     *
     * @param coating the new coating
     */
    void setCoating(Coating coating);
}
