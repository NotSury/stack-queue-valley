package game.interfaces;

/**
 * Represents a plant whose lifecycle can be temporarily frozen.
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.0.0
 */
public interface Freezable
{
    /**
     * Temporarily freezes the plant's lifecycle.
     */
    void freeze();

    /**
     * Prevents magical growth while a freeze effect remains active.
     */
    void holdFreeze();

    /**
     * Releases protection when a freeze effect expires.
     */
    void releaseFreeze();
}
