package game.interfaces;

import edu.monash.fit2099.engine.items.Item;

/**
 * Factory interface for creating produce items.
 *
 * @author Khoo Lynn 34598901
 * @version 1.0.0
 */
public interface ProduceFactory {

    /**
     * Creates a new produce item.
     *
     * @return a new produce item.
     */
    Item create();
}
