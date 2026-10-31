package game.effects;

import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import game.interfaces.FreshProduce;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Collects fresh produce within a fixed area for three turns.
 * A maximum of five produce items can be collected.
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.0.0
 */
public class HarvestTrailEffect extends TimedAreaEffect {

    public static final String NAME = "Magical Harvest Trail";
    private static final int RADIUS = 1;
    private static final int DURATION = 3;
    private static final int MAX_ITEMS = 5;

    private final Actor farmer;
    private int collectedCount = 0;

    /**
     * Creates a magical harvest trail.
     *
     * @param map    the current game map
     * @param centre the centre of the effect
     * @param farmer the Farmer receiving collected produce
     */
    public HarvestTrailEffect(GameMap map, Location centre, Actor farmer) {
        super(map, centre, RADIUS, DURATION);
        this.farmer = farmer;
    }

    /**
     * Returns the name of this magical ability.
     *
     * @return the ability name
     */
    @Override
    public String getAbilityName() {
        return NAME;
    }

    /**
     * Collects up to five fresh produce items from the area.
     *
     * @param area locations affected by the effect
     */
    @Override
    protected void applyTick(List<Location> area) {
        for (Location location : area) {
            for (Item item : new ArrayList<>(location.getItems())) {

                Optional<FreshProduce> produce =
                        item.asCapability(FreshProduce.class);

                if (produce.isPresent() && produce.get().isFresh()) {
                    location.removeItem(item);
                    farmer.getInventory().add(item);
                    collectedCount++;
                }

                if (collectedCount >= MAX_ITEMS) {
                    finishEarly();
                    return;
                }
            }
        }
    }
}

