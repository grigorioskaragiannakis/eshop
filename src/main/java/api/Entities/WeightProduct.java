package api.Entities;

import api.Enums.ProductType;

import java.math.BigDecimal;

public class WeightProduct extends Product{

    /**
     * Constructor που αρχικοποιεί την WeightProduct με όλα τα απαραίτητα πεδία.
     *
     * @param title
     * @param description
     * @param category
     * @param price
     * @param quantity
     */
    public WeightProduct(String title, String description, Category category, BigDecimal price, Number quantity) {
        super(title, description, category, price, quantity);
    }

    @Override
    public ProductType getType() {
        return ProductType.WEIGHT;
    }
}
