package game.afflictions;

/**
 * Identifies each kind of curse, so curses of the same kind can be recognised
 * without type checks (for example, to avoid giving an actor the same curse twice).
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public enum CurseType {
    /** Spreads to adjacent actors and blights the ground. */
    CONTAGION,
    /** Turns the surrounding tiles into impassable swamp. */
    MIRE,
    /** Links the host to another actor and shares its curses. */
    SOUL_TETHER
}