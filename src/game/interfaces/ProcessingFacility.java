package game.interfaces;

import game.items.ProcessableItem;
/**
 * ProcessingFacility represents a facility that can process a
 * ProcessableItem into another processable product.
 *
 * <p>Different facilities can implement this interface to provide
 * different processing methods without requiring ProcessAction to
 * depend on specific facility classes.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public interface ProcessingFacility {
    /**
     * Processes the given processable item using this facility's
     * processing method.
     *
     * @param product the processable item to be processed
     * @return the newly created processed item
     */
    ProcessableItem process(ProcessableItem product);
    /**
     * Returns the name of this processing facility.
     *
     * @return the name of the processing facility
     */
    String getFacilityName();
}
