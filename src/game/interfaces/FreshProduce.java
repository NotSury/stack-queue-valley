package game.interfaces;

/**
 * Represents produce that can be collected automatically
 * by magical harvesting effects.
 *
 * @author Vanessa Liau Jia Yee <34599398>
 * @version 1.0.0
 */
public interface FreshProduce {

    /**
     * Checks if the produce is suitable for collection.
     *
     * @return true if the produce is fresh and can be collected
     */
    boolean isFresh();
}
