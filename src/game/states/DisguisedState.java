package game.states;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.ItemAbility;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.CaveMimic;
import game.navigation.GridDistance;
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

    private final CaveMimic mimic;
    private final char disguisedDisplayChar;

    private int quietTurns;

    /**
     * Creates a disguised state for a Cave Mimic.
     *
     * @param mimic the Mimic that owns this state
     * @param scanner the scanner used to inspect the surroundings
     * @param disguisedDisplayChar the character representing the disguise
     */
    public DisguisedState(CaveMimic mimic, EnvironmentScanner scanner, char disguisedDisplayChar) {
        super(scanner);
        this.mimic = mimic;
        this.disguisedDisplayChar = disguisedDisplayChar;
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
        mimic.disguise(disguisedDisplayChar);
        mimic.scatterBait(location, MAXIMUM_BAIT_ITEMS);
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
        Actor farmer = getScanner().findFarmer(location, CaveMimic.DETECTION_RADIUS);

        if (farmer != null) {
            quietTurns = 0;

            Location farmerLocation = location.map().locationOf(farmer);

            if (GridDistance.between(location, farmerLocation) == 1 && farmer.hasAbility(ItemAbility.PORTABLE)) {
                return CreatureStateType.AMBUSH;
            }

            return CreatureStateType.DISGUISED;
        }

        quietTurns++;

        if (quietTurns >= REQUIRED_QUIET_TURNS) {
            Location loot = getScanner().findNearestPortableItemLocation(mimic, location, MINIMUM_LOOT_DISTANCE, CaveMimic.DETECTION_RADIUS);

            if (loot != null) {
                return CreatureStateType.LOOTING;
            }
        }

        return CreatureStateType.DISGUISED;
    }

}
