package game.interfaces;

import game.grounds.Plant;

/**
 * Factory interface for creating new plants.
 *
 * @author Khoo Lynn 34598901
 * @version 1.0.0
 */
public interface PlantFactory {

    /**
     * Creates a new plant.
     *
     * @return a new plant instance
     */
    Plant create();
}
