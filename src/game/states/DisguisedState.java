package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.HealAction;
import game.interfaces.BaitCarrier;
import game.interfaces.Disguisable;
import game.perception.EnvironmentScanner;

/**
 * A stationary state in which a Cave Mimic disguises itself
 * and scatters portable items as bait.
 * Transitions to Ambush when an adjacent Farmer carries a portable
 * item, or to Looting after a quiet period when external loot
 * is reachable.
 */
public class DisguisedState extends EnvironmentAwareState {

    private static final int REQUIRED_QUIET_TURNS = 5;
    private static final int MINIMUM_LOOT_DISTANCE = 2;
    private static final int MAXIMUM_BAIT_ITEMS = 3;
    private static final int HEALING_PER_TURN = 3;

    private final Disguisable disguisable;
    private final BaitCarrier baitCarrier;
    private final char disguisedDisplayChar;
    private final int detectionRadius;

    private int quietTurns;

    /**
     * Creates a disguised state with appearance and bait operations.
     * The supplied role interfaces must operate on the owning creature.
     *
     * @param creature the actor that owns this state
     * @param scanner the scanner used to inspect the surroundings
     * @param disguisable the owning creature's appearance operations
     * @param baitCarrier the owning creature's bait operations
     * @param disguisedDisplayChar the character representing the disguise
     * @param detectionRadius the maximum distance used for environmental scans
     */
    public DisguisedState(Actor creature, EnvironmentScanner scanner, Disguisable disguisable, BaitCarrier baitCarrier, char disguisedDisplayChar, int detectionRadius) {
        super(creature, scanner);
        this.disguisable = disguisable;
        this.baitCarrier = baitCarrier;
        this.disguisedDisplayChar = disguisedDisplayChar;
        this.detectionRadius = detectionRadius;
    }

    /**
     * Returns the identifier for this state.
     *
     * @return the Disguised state identifier
     */
    @Override
    public CreatureStateType getType() {
        return CreatureStateType.DISGUISED;
    }

    /**
     * Applies the disguise, scatters adjacent bait and resets
     * the quiet-turn counter.
     *
     * @param location the Mimic's current location
     */
    @Override
    public void onEnter(Location location) {
        disguisable.disguise(disguisedDisplayChar);
        baitCarrier.scatterBait(location, MAXIMUM_BAIT_ITEMS);
        quietTurns = 0;
    }

    /**
     * Evaluates the next state and updates the quiet-turn counter.
     * The creature framework calls this method once per turn.
     * An adjacent Farmer carrying a portable item takes priority
     * over item seeking.
     *
     * @param location the creature's current location
     * @return Ambush, Looting or Disguised according to the surroundings
     */
    @Override
    public CreatureStateType determineNextState(Location location) {
        Actor farmer = getScanner().findFarmer(location, detectionRadius);

        if (farmer != null) {
            quietTurns = 0;

            if (canStealFrom(farmer, location)) {
                return CreatureStateType.AMBUSH;
            }

            return CreatureStateType.DISGUISED;
        }

        quietTurns++;

        if (quietTurns >= REQUIRED_QUIET_TURNS) {
            Location loot = getScanner().findNearestPortableItemLocation(getCreature(), location, MINIMUM_LOOT_DISTANCE, detectionRadius);

            if (loot != null) {
                return CreatureStateType.LOOTING;
            }
        }

        return CreatureStateType.DISGUISED;
    }

    /**
     * Selects health recovery while the Mimic remains disguised.
     * Remains idle when its health is already full.
     *
     * @param location the Mimic's current location
     * @return a healing action, or an idle action at maximum health
     */
    @Override
    public Action getAction(Location location) {
        if (getCreature().getStatistic(ActorStatistics.HEALTH) >= getCreature().getMaximumStatistic(ActorStatistics.HEALTH)) {
            return super.getAction(location);
        }

        return new HealAction(HEALING_PER_TURN);
    }

}
