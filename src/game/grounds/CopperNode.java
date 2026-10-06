package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.Chargeable;
import game.items.CopperOre;

/**
 * A breakable Copper Node found in the mines.
 *
 * <p>Breaking a Copper Node drops Copper Ore, replaces the node with Dirt
 * and charges adjacent grounds that support the Chargeable capability.</p>
 *
 * @author Khoo Lynn 34598901
 * @version 1.0.0
 */
public class CopperNode extends BreakableGround{

    /**
     * Creates a CopperNode.
     */
    public CopperNode(){
        super('N', "Copper Node");
    }

    /**
     * Charges all adjacent grounds that support the Chargeable capability.
     *
     * @param location the location containing the Copper Node.
     */
    private void chargeAdjacentGrounds(Location location){
        for (Exit exit : location.getExits()){
            Location destination = exit.getDestination();
            Chargeable chargeable = destination.getGroundAs(Chargeable.class);

            if (chargeable != null){
                chargeable.charge();
            }
        }
    }

    /**
     * Breaks the Copper Node, drops one Copper Ore, charges adjacent chargeable
     * grounds and replaces the Copper Node with Dirt.
     *
     * @param actor actor breaking the object.
     * @param location location of the object being broken.
     * @return a description of the breaking result.
     */
    @Override
    public String breakBy(Actor actor, Location location){
        location.addItem(new CopperOre());
        chargeAdjacentGrounds(location);
        location.setGround(new Dirt());
        return actor + " breaks the Copper Node.";
    }
}
