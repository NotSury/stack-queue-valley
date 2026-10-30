package game.capabilities;

/**
 * Capabilities used by the Necromancer's Curse feature (REQ3).
 *
 * <p>HAS_MINION marks an actor that currently controls a raised undead minion,
 * so only one minion can be active at a time. UNDEAD marks undead actors, which
 * are immune to every curse. CURSED_GROUND marks ground created by the curses
 * (and the remains of cursed creatures), so it is never covered by more cursed
 * ground.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.1.0
 */
public enum NecromancyCapability {
    /** The actor currently has an active undead minion. */
    HAS_MINION,
    /** The actor is undead and cannot be cursed. */
    UNDEAD,
    /** The ground is cursed ground or cursed remains and must not be replaced. */
    CURSED_GROUND
}