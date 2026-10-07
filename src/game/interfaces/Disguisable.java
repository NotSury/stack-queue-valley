package game.interfaces;

/**
 * Provides operations for changing a creature's visible appearance.
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
