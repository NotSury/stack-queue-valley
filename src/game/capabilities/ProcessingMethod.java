package game.capabilities;

import game.items.ProcessableProduct;

/**
 * Represents a method that can apply one processing layer to a product.
 */
public interface ProcessingMethod {

    /**
     * Creates a new processed product that wraps the given product.
     *
     * @param product the complete product before processing
     * @return the newly processed product
     */
    ProcessableProduct process(ProcessableProduct product);
}
