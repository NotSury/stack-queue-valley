package game.afflictions;

import edu.monash.fit2099.engine.actors.Actor;
import game.capabilities.NecromancyCapability;

import java.util.List;
import java.util.function.Supplier;

/**
 * The single place where curses are given to actors (REQ3), so the rules
 * "undead are immune" and "an actor never carries the same curse twice" are
 * written once instead of in every curse, ground and creature (DRY).
 *
 * @author Lee Le Si 34599223
 * @version 1.1.0
 */
public final class CurseApplier {

    /**
     * Private constructor: this is a utility class with only static methods.
     */
    private CurseApplier() {
    }

    /**
     * Gives a curse to an actor if the actor can receive it.
     *
     * @param target the actor to curse
     * @param curse  the curse to give (a fresh object that is not shared with another actor)
     * @return true if the curse was applied, false if the actor is undead or already has this kind of curse
     */
    public static boolean apply(Actor target, Affliction curse) {
        if (target.hasAbility(NecromancyCapability.UNDEAD) || hasCurse(target, curse.getType())) {
            return false;
        }
        target.addStatus(curse);
        return true;
    }

    /**
     * Gives a fresh copy of a curse to each actor in a group that can receive it.
     *
     * @param targets the actors to curse
     * @param curse   creates a new curse object for each actor
     * @return how many actors received the curse
     */
    public static int applyToAll(List<Actor> targets, Supplier<Affliction> curse) {
        int applied = 0;
        for (Actor target : targets) {
            if (apply(target, curse.get())) {
                applied++;
            }
        }
        return applied;
    }

    /**
     * Checks whether an actor already carries a curse of the given kind.
     *
     * @param target the actor to check
     * @param type   the kind of curse
     * @return true if the actor already has a curse of this kind
     */
    public static boolean hasCurse(Actor target, CurseType type) {
        return target.statusesOf(Affliction.class).stream()
                .anyMatch(curse -> curse.getType() == type);
    }
}