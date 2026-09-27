package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.displays.Menu;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.items.Hoe;
import game.items.WateringCan;

/**
 * Represents the player-controlled Farmer in Stack-Queue Valley.
 */
public class Farmer extends Actor {
    /**
     * Constructs a Farmer with the given name.
     * The Farmer starts with 100 health points, 20 stamina points, a Hoe and a Watering Can.
     *
     * @param name the name of the Farmer
     */
    public Farmer(String name) {
        super("Farmer " + name, '@', 100);
        this.addNewStatistic(ActorStatistics.STAMINA, new BaseStatistic(20));

        this.getInventory().add(new Hoe());
        this.getInventory().add(new WateringCan());
    }

    /**
     * Adds ground actions from adjacent locations occupied by actors,
     * since the engine otherwise omits the underlying ground actions.
     *
     * @param actions available actions for the Farmer
     * @param map current game map
     */
    private void addOccupiedGroundActions(ActionList actions, GameMap map) {
        for (Exit exit : map.locationOf(this).getExits()) {
            Location destination = exit.getDestination();

            if (destination.containsAnActor()) {
                actions.add(destination.getGround().allowableActions(this, destination, exit.getName()));
            }
        }
    }

    /**
     * Selects the action to be performed by the Farmer for the current turn.
     *
     * @param actions collection of possible Actions for this farmer
     * @param lastAction the action performed during the previous turn
     * @param map the map containing the Farmer
     * @param display the display used for game output and menu interaction
     * @return the action selected for the current turn
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (!this.isConscious()) {
            this.unconscious(map);
            return new DoNothingAction();
        }

        this.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.INCREASE, 1);

        // Handle multi-turn Actions
        if (lastAction.getNextAction() != null)
            return lastAction.getNextAction();

        display.println(this.name);
        display.println("HP: " + this.getStatistic(ActorStatistics.HEALTH));
        display.println("SP: " + this.getStatistic(ActorStatistics.STAMINA));
        display.println("Inventory: " + this.getInventory().getItems());

        addOccupiedGroundActions(actions, map);

        // return/print the console menu
        Menu menu = new Menu(actions);
        return menu.showMenu(this, display);
    }
}
