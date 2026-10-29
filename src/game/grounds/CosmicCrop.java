package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.GroundAbility;
import game.items.ConsumableItem;
import game.items.CosmicCauliflower;
import game.items.CosmicMelon;
import game.items.CosmicParsnip;
import game.items.CosmicStrawberry;
import game.items.CosmicTomato;

import java.util.Objects;

/**
 * A mutated, harvestable crop that produces a cosmic consumable item.
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.1.0
 */
public final class CosmicCrop extends Crop {

    /**
     * Identifies the original crop and its harvesting cost.
     */
    public enum Variety {
        PARSNIP("Parsnip", 'p', 1),
        TOMATO("Tomato", 't', 2),
        CAULIFLOWER("Cauliflower", 'f', 3),
        MELON("Melon", 'w', 5),
        STRAWBERRY("Strawberry", 's', 1);

        private final String label;
        private final char symbol;
        private final int harvestCost;

        Variety(String label, char symbol, int harvestCost) {
            this.label = label;
            this.symbol = symbol;
            this.harvestCost = harvestCost;
        }
    }

    private final Variety variety;

    /**
     * Constructs a harvestable cosmic crop.
     *
     * @param variety original variety of the crop
     */
    public CosmicCrop(Variety variety) {
        super(
                Objects.requireNonNull(variety, "variety").symbol,
                "Cosmic " + variety.label + " Crop"
        );

        this.variety = variety;

        enableAbility(GroundAbility.COSMIC_CROP);
        disableAbility(GroundAbility.METEOR_IMPACTABLE);
    }

    /**
     * Returns the original crop variety.
     *
     * @return original crop variety
     */
    public Variety getVariety() {
        return variety;
    }

    /**
     * Returns the stamina required to harvest the cosmic crop.
     *
     * @return harvesting stamina cost
     */
    @Override
    public int getHarvestCost() {
        return variety.harvestCost;
    }

    /**
     * Harvests the crop and creates its matching cosmic item.
     *
     * @param actor actor harvesting the crop
     * @param location location containing the crop
     * @return description of the harvest result
     */
    @Override
    public String harvest(Actor actor, Location location) {

        ConsumableItem item = switch (variety) {
            case PARSNIP -> new CosmicParsnip();
            case TOMATO -> new CosmicTomato();
            case CAULIFLOWER -> new CosmicCauliflower();
            case MELON -> new CosmicMelon();
            case STRAWBERRY -> new CosmicStrawberry();
        };

        actor.getInventory().add(item);
        removeCrop(location);

        return actor + " harvests a Cosmic "
                + variety.label
                + " Crop and receives " + item + ".";
    }
}
