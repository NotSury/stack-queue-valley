package game.grounds;

import game.items.DriedProduct;
import game.items.ProcessableProduct;

/**
 * Represents a Drying Rack that applies a dried layer to products.
 */
public class DryingRack extends ProcessingFacility {

    /**
     * Constructs a Drying Rack.
     */
    public DryingRack() {
        super('R', "Drying Rack");
    }

    /**
     * Creates one dried layer around the given product.
     *
     * @param product the complete product before processing
     * @return a new dried product
     */
    @Override
    public ProcessableProduct process(ProcessableProduct product) {
        return new DriedProduct(product);
    }

}
