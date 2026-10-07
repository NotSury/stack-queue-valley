package game.actions.traps;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import game.actions.WebBoundAction;
import game.capabilities.WebAbility;
import game.statuses.WebBoundStatus;

import java.util.List;

/**
 * Applies and consumes one-turn web bindings.
 * Immune actors cannot be bound, and additional webs cannot
 * stack more pending lost turns onto an already bound actor.
 */
public final class WebTrapHandler {

    private WebTrapHandler() {
    }

    /**
     * Applies one pending web binding to an eligible actor.
     *
     * @param actor the actor that the web attempts to catch
     * @return true if a new binding was applied
     */
    public static boolean tryBind(Actor actor) {
        if (!actor.isConscious()
                || actor.hasAbility(WebAbility.IMMUNE_TO_WEBS)
                || actor.hasStatus(WebBoundStatus.class)) {
            return false;
        }

        actor.addStatus(new WebBoundStatus());
        return true;
    }

    /**
     * Consumes a pending binding before normal action selection.
     * Removes all web binding statuses and produces only one
     * lost turn. Preserves a pending multi-turn continuation.
     * Immune or unconscious actors receive no escape action.
     *
     * @param actor the actor whose turn is beginning
     * @param lastAction the previous action, or null
     * @return a web escape action, or null if no turn is blocked
     */
    public static Action consumeBinding(Actor actor, Action lastAction) {
        List<WebBoundStatus> bindings = actor.statusesOf(WebBoundStatus.class);

        if (bindings.isEmpty()) {
            return null;
        }

        for (WebBoundStatus binding : bindings) {
            actor.removeStatus(binding);
        }

        if (!actor.isConscious()
                || actor.hasAbility(WebAbility.IMMUNE_TO_WEBS)) {
            return null;
        }

        Action continuation = lastAction == null
                ? null
                : lastAction.getNextAction();

        return new WebBoundAction(continuation);
    }
}
