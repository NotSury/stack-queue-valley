package game.grounds;

import game.items.PreservedProduct;
import game.items.ProcessableProduct;

/**
 * Represents a Preserves Jar that applies a preserved layer to products.
 */
public class PreservesJar extends ProcessingFacility{

    /**
     * Constructs a Preserves Jar.
     */
    public PreservesJar() {
        super('J', "Preserves Jar");
    }

    /**
     * Creates a new preserved layer around the given product.
     *
     * @param product the complete product before processing
     * @return a newly created preserved product
     */
    @Override
    public ProcessableProduct process(ProcessableProduct product) {
        return new PreservedProduct(product);
    }
}
