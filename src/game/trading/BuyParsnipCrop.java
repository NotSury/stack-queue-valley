package game.trading;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.capabilities.FarmerStatistic;
import game.grounds.Crop;
import game.interfaces.Buyable;
import game.grounds.ParsnipCrop;
import game.items.Parsnip;
import game.interfaces.PlantableGround;
import game.interfaces.PlantableGround;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author <Vanessa Liau Jia Yee> <34599398>
 * @version 1.0.0
 */
public class BuyParsnipCrop implements Buyable {
    private static final int PRICE = 250;
    private static final int PARSNIP_AMOUNT = 3;
    private final Random random = new Random();

    @Override
    public int getBuyPrice() {
        return PRICE;
    }

    @Override
    public String buy(Actor buyer, Actor merchant, GameMap map) {
        if (buyer.getStatistic(FarmerStatistic.COINS) < PRICE) {
            return buyer + " does not have enough coins to buy a Parsnip Crop.";
        }

        buyer.modifyStatistic(FarmerStatistic.COINS, StatisticOperations.DECREASE, PRICE);

        Location merchantLocation = map.locationOf(merchant);

        List<Location> availableLocations = new ArrayList<>();

        for (Exit exit : merchantLocation.getExits()) {
            Location location = exit.getDestination();

            if (location.getGroundAs(PlantableGround.class) != null) {
                availableLocations.add(location);
            }
        }

        if (availableLocations.isEmpty()) {
            buyer.modifyStatistic(FarmerStatistic.COINS, StatisticOperations.INCREASE, PRICE);

            return "There is no available location for a Parsnip Crop. " + PRICE + " coins were refunded!";
        }

        Location cropLocation = availableLocations.get(random.nextInt(availableLocations.size()));

        cropLocation.setGround(new ParsnipCrop());

        List<Exit> cropExits = cropLocation.getExits();

        for (int i = 0; i < PARSNIP_AMOUNT; i++) {
            int randomIndex = random.nextInt(cropExits.size());

            Location dropLocation = cropExits.get(randomIndex).getDestination();

            dropLocation.addItem(new Parsnip());
        }
        return buyer +" bought a Parsnip Crop for "+ PRICE + " coins.";
    }

    @Override
    public String toString()
    {
        return "Parsnip Crop";
    }
}
