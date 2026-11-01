package game.interfaces;

/**
 * Represents an actor that can be temporarily stunned
 * by magical effects.
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.0.0
 */
public interface Stunnable {

    /**
     * Applies a temporary stun to the actor.
     *
     * @param turns the number of turns to remain stunned
     */
    void stun(int turns);
}
