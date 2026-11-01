package game.effects;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Dirt;
import game.grounds.Soil;
import game.interfaces.SoilConvertible;
import game.support.AreaLocationHelper;

import java.util.List;
import java.util.Optional;

/**
 * Transform nearby Dirt tiles into Soil.
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.0.0
 */
public class LivingSoilEffect {

    private static final int RADIUS = 1;

    /**
     * Transform Dirt into Soil around the given centre.
     *
     * @param map the current game map
     * @param centre the centre of the transformation
     */
    public void apply(GameMap map, Location centre)
    {
        List<Location> area =
                AreaLocationHelper.square(map, centre, RADIUS);

        for (Location location : area)
        {
            Optional<SoilConvertible> convertible =
                    location.getGround().asCapability(SoilConvertible.class);

            if (convertible.isPresent()){
                location.setGround(convertible.get().convertToSoil());
            }
        }
    }
}
