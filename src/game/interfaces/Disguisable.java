package game.interfaces;

/**
 * Provides operations for changing a creature's visible appearance.
 *
 * @author Surya Muni Peragas 35651628
 * @version 1.0.0
 */
public interface Disguisable {

    /**
     * Changes the creature's appearance to a disguise.
     *
     * @param displayChar the character representing the disguise
     */
    void disguise(char displayChar);

    /**
     * Restores the creature's normal appearance.
     */
    void reveal();
}
