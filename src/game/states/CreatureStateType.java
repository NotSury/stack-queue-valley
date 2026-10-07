package game.states;

/**
 * Identifies the states available to stateful creatures.
 *
 * <p>These identifiers are used to register and select state objects.
 * Each concrete state class defines its own transition conditions,
 * entry effects and ordinary-turn behaviour.</p>
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
    ESCAPE
}
