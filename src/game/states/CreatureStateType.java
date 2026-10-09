package game.states;

/**
 * Identifies the states available to stateful creatures.
 * These identifiers are used to register and select state objects.
 * Each concrete state class defines its own transition conditions,
 * entry effects and ordinary-turn behaviour.
 *
 * @author Surya Muni Peragas 35651628
 * @version 1.0.0
 */
public enum CreatureStateType {

    /**
     * The Cave Mimic remains stationary, disguised as a Geode,
     * with portable items scattered nearby as bait.
     */
    DISGUISED,

    /**
     * The Cave Mimic searches for and collects portable ground items.
     */
    LOOTING,

    /**
     * The Cave Mimic reveals itself and pursues or attacks the Farmer.
     */
    AMBUSH,

    /**
     * The Cave Mimic creates a temporary decoy on entry
     * and attempts to flee from the Farmer.
     */
    ESCAPE,

    /**
     * The Spider returns home and guards its nest.
     */
    NESTING,

    /**
     * The Spider builds traps around its nest.
     */
    WEAVING,

    /**
     * The Spider pursues or attacks an intruding Farmer.
     */
    HUNTING,

    /**
     * The Spider investigates an owned-web alert using tactical
     * two-tile jumps and safe walking.
     */
    POUNCING
}
