package game.effects;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.SoilConvertible;
import game.grounds.StrawberrySeedling;
import game.grounds.CauliflowerSprout;
import game.grounds.MelonSprout;
import game.interfaces.PlantFactory;

import java.util.Random;
import java.util.List;
import java.util.Optional;

/**
 * Restores nearby Dirt tiles to Soil over three effect ticks.
 * Newly restored tiles after the first tick have 50% change
 * of regenerating a random plant.
 *
 * Existing crops and Soil remain unchanged.
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.1.0
 */
public class LivingSoilEffect extends TimedAreaEffect {

    public static final String NAME = "Living Soil";
    private static final int RADIUS = 1;
    private static final int DURATION = 3;
    private static final double REGENERATION_CHANCE = 0.5;
    private final Random random;
    private final List<PlantFactory> regrowth;
    private boolean firstTick = true;

    /**
     * Creates a Living Soil effect.
     *
     * @param map the current game map
     * @param centre the centre of the affected area
     * @param random random generator for regeneration
     * @param regrowth available plant factories
     */
    public LivingSoilEffect(GameMap map, Location centre,
                            Random random, List<PlantFactory> regrowth) {
        super(map, centre, RADIUS, DURATION);
        this.random = random;
        this.regrowth = regrowth;
    }

    /**
     * Returns the ability name.
     *
     * @return the name of Living Soil
     */
    @Override
    public String getAbilityName() {
        return NAME;
    }

    /**
     * Restores Dirt to Soil and regenerates plants on later ticks.
     *
     * @param area the affected locations
     */
    @Override
    protected void applyTick(List<Location> area)
    {
        for (Location location : area)
        {
            Optional<SoilConvertible> convertible =
                    location.getGround().asCapability(SoilConvertible.class);

            if (convertible.isEmpty()){
                continue;
            }

            location.setGround(convertible.get().convertToSoil());

            if (!firstTick) {
                    regenerate(location);
            }
        }
        firstTick = false;
    }

    /**
     * Gives a newly restored tile a 50% chance of growing a plant.
     *
     * @param location location where regeneration may occur
     */
    private void regenerate(Location location) {
        if (random.nextDouble() < REGENERATION_CHANCE) {
            int index = random.nextInt(regrowth.size());
            location.setGround(regrowth.get(index).create());
        }
    }
}
