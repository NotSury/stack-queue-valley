package game.effects;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.actors.Actor;
import game.interfaces.Repellable;
import game.support.AreaLocationHelper;

import java.util.Optional;
import java.util.List;

/**
 * A temporary sanctuary created by SanctuaryDeer.
 * Protects its area and supports the Farmer.
 *
 * @author <Vanessa Liau Jia Yee> <34599398>
 * @version 1.1.0
 */
public class NatureSanctuaryEffect extends TimedAreaEffect {
    public static final String NAME = "Nature's Sanctuary";

    private static final int RADIUS = 1;
    private static final int REACH = 2;
    private static final int DURATION = 1;
    private static final int MAX_RETREAT = 5;

    /**
     * Direction priority for displacement: up, right, down, left.
     */
    private static final int[][] DIRECTIONS = {
            {0, -1},
            {1, 0},
            {0, 1},
            {-1, 0}
    };

    /**
     * Creates Nature's Sanctuary centred on the Farmer.
     *
     * @param map    the current game map
     * @param centre the sanctuary's centre
     */
    public NatureSanctuaryEffect(GameMap map, Location centre) {
        super(map, centre, RADIUS, DURATION);
    }

    /**
     * Repels hostile creature inside the sanctuary and prevents
     * nearby hostile creature from entering its protected area.
     *
     * @param area the sanctuary's protected locations
     */
    private void repelHostiles(List<Location> area) {

        for (Location location : AreaLocationHelper.square(getMap(), getCentre(), REACH)) {

            if (!location.containsAnActor()) {
                continue;
            }
            Actor actor = location.getActor();

            Optional<Repellable> repellable =
                    actor.asCapability(Repellable.class);

            if (repellable.isEmpty()) {
                continue;
            }

            if (area.contains(location)) {
                Optional<Location> retreat =
                        findRetreat(actor, location, area);

                if (retreat.isPresent()) {
                    getMap().moveActor(actor, retreat.get());
                }
            }

            restrictCreature(repellable.get(), area);
        }
    }

    /**
     * Prevents a creature from entering the sanctuary.
     *
     * @param creature the affected creature
     * @param area the protected locations
     */
    private void restrictCreature(Repellable creature, List<Location> area) {
        creature.forbid(area);
    }

    /**
     * Finds an available retreat location outside the sanctuary.
     * Searches along the horizontal and vertical axes using a fixed
     * direction priority of up, right, down and left.
     *
     * @param actor the hostile creature being displaced
     * @param from the creature's current location
     * @param shield the locations protected by Nature's Sanctuary
     * @return an available retreat location, or an empty Optional if non exists
     */
    private Optional<Location> findRetreat(Actor actor, Location from, List<Location> shield) {

        for (int distance = 1; distance <= MAX_RETREAT; distance++) {
           for (int[] direction : DIRECTIONS) {

                int x = from.x() + direction[0] * distance;
                int y = from.y() + direction[1] * distance;

                if (!AreaLocationHelper.inBounds(getMap(), x, y)) {
                    continue;
                }

                Location candidate = getMap().at(x, y);

                if (!shield.contains(candidate)
                        && candidate.canActorEnter(actor)
                        && !candidate.containsAnActor()) {
                    return Optional.of(candidate);
            }
        }
    }
    return Optional.empty();
}

    /**
     * Returns the name of Nature's Sanctuary.
     *
     * @return the sanctuary ability name
     */
    @Override
    public String getAbilityName() {
        return NAME;
    }

    /**
     * Applies sanctuary protection by repelling nearby hostile creatures
     * and preventing them from entering the protected area.
     *
     * @param area the sanctuary's protected locations
     */
    @Override
    protected void applyTick(List<Location> area) {
        repelHostiles(area);
    }
}
