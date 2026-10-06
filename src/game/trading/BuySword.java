package game.trading;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.capabilities.FarmerStatistic;
import game.interfaces.Buyable;
import game.items.Sword;

import java.util.Random;

public class BuySword implements Buyable
{
    private static final int PRICE = 3000;

    @Override
    public int getBuyPrice()
    {
        return PRICE;
    }

    @Override
    public String buy(Actor buyer, Actor merchant, GameMap map)
    {
        if (buyer.getStatistic(FarmerStatistic.COINS) < PRICE)
        {
            return buyer + " does not have enough coins to buy a Sword.";
        }

        buyer.modifyStatistic(FarmerStatistic.COINS, StatisticOperations.DECREASE, PRICE);

        Random random = new Random();

        if (random.nextBoolean())
        {
            buyer.getInventory().add(new Sword());
            return buyer + " bought a Sword for " + PRICE + " coins.";
        }

        return "Merchant took " + PRICE + " coins but gave no Sword.";
    }

    @Override
    public String toString()
    {
        return "Sword";
    }
}
