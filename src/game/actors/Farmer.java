package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.displays.Menu;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actions.traps.WebTrapHandler;
import game.capabilities.ActorRole;
import game.capabilities.CombatStatus;
import game.capabilities.FarmerStatistic;
import game.items.Hoe;
import game.items.Pickaxe;
import game.items.WateringCan;
/**
 * Farmer represents the player-controlled actor in the game.
 *
 * <p>The Farmer begins with health, stamina, a Hoe, and a WateringCan.
 * At the start of each turn, the Farmer recovers 1 stamina point before
 * selecting an action from the available action menu.</p>
 *
 * <p>The Farmer also displays its current health, stamina, and inventory
 * at the beginning of each turn.</p>
 *
 * @author Lee Le Si 34599223
 * @author Khoo Lynn 34598901
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.1.0
 */
public class Farmer extends Actor {
    /**
     * Constructor for the Farmer class.
     *
     * <p>Creates a Farmer with 100 health points, a maximum stamina of 20,
     * and starting equipment consisting of a Hoe and a WateringCan.</p>
     * Farmer is marked as a target for hostile creatures.</p>
     *
     * <p>A Sword is also given for testing REQ 4 until it can be bought
     * from the merchant (REQ 3).</p>
     *
     * <p>A Pickaxe is added to the Farmer's starting inventory for REQ 2,
     * allowing the Farmer to perform breaking actions on breakable objects.</p>
     *
     * @param name the name of the Farmer
     */
    public Farmer(String name) {
        super("Farmer " + name, '@', 100);
        this.enableAbility(ActorRole.FARMER);
        this.enableAbility(CombatStatus.HOSTILE_TARGET);

        this.addNewStatistic(ActorStatistics.STAMINA, new BaseStatistic(20));
        this.addNewStatistic(FarmerStatistic.COINS, new BaseStatistic(10000));

        this.getInventory().add(new Hoe());
        this.getInventory().add(new WateringCan());
        this.getInventory().add(new Pickaxe());
    }
    /**
     * Determines the action performed by the Farmer during the current turn.
     *
     * <p>The Farmer first recovers 1 stamina point. If the Farmer is
     * unconscious, the unconscious behaviour is handled and no further
     * action is performed. Multi-turn actions are continued when required.</p>
     *
     * <p>Otherwise, the Farmer's health, stamina, and inventory are displayed
     * before the action menu is shown.</p>
     *
     * @param actions the actions currently available to the Farmer
     * @param lastAction the action performed during the previous turn
     * @param map the game map containing the Farmer
     * @param display the display used to show game information
     * @return the action selected or continued for this turn
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {

        this.modifyStatistic(
                ActorStatistics.STAMINA,
                StatisticOperations.INCREASE,
                1
        );

        if (!this.isConscious()) {
            this.unconscious(map);
            return new DoNothingAction();
        }

        Action webAction = WebTrapHandler.consumeBinding(this, lastAction);

        if (webAction != null) {
            return webAction;
        }

        // Handle multi-turn Actions
        if (lastAction.getNextAction() != null)
            return lastAction.getNextAction();

        display.println(this.toString());
        display.println("HP: " + this.getStatistic(ActorStatistics.HEALTH));
        display.println("SP: " + this.getStatistic(ActorStatistics.STAMINA));
        display.println("Inventory: " + this.getInventory().getItems());
        display.println("Coins: " + this.getStatistic(FarmerStatistic.COINS));
        display.println("");

        // return/print the console menu
        Menu menu = new Menu(actions);
        return menu.showMenu(this, display);
    }
}
