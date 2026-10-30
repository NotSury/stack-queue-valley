package game.capabilities;

/**
 * Capabilities used by the Necromancer's Curse feature (REQ3).
 *
 * <p>HAS_MINION marks an actor that currently controls a raised undead minion,
 * so only one minion can be active at a time. UNDEAD marks undead actors, which
 * are immune to every curse.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public enum NecromancyCapability {
    /** The actor currently has an active undead minion. */
    HAS_MINION,
    /** The actor is undead and cannot be cursed. */
    UNDEAD
}