package api.Entities;


import api.Enums.ProductType;

import java.math.BigDecimal;

public class PiecesProduct extends Product {
    /**
     * Constructor που αρχικοποιεί ένα PiecesProduct με όλα τα απαραίτητα στοιχεία.
     *
     * @param title
     * @param description
     * @param category
     * @param price
     * @param quantity
     */
    public PiecesProduct(String title, String description, Category category, BigDecimal price, Number quantity) {
        super(title, description, category, price, quantity);
    }

    @Override
    public ProductType getType() {
        return ProductType.PIECES;
    }
}
