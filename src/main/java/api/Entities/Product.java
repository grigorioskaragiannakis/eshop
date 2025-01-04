package api.Entities;

import api.Enums.ProductType;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Abstract κλάση η οποία ορίζει τα βασικά χαρακτηριστικά ενός προϊόντος όπως τίτλο, περιγραφή, κατηγορία, τιμή και ποσότητα.
 * Οι κλάσεις που θα κληρωνομίσουν αυτή την κλάση θα καθορίζουν το τύπο του προϊόντος.
 */
public abstract class Product {

    protected String title;
    protected String description;
    protected Category category;
    protected BigDecimal price;
    protected Number quantity;

    /**
     * Constructor που αρχικοποεί ένα Product με όλα τα απαραίτητα πεδία.
     * @param title
     * @param description
     * @param category
     * @param price
     * @param quantity
     */
    public Product(String title, String description, Category category, BigDecimal price, Number quantity) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.price = price;
        this.quantity = quantity;


        // Ελέγχει αν έχουν αρχικοποιηθεί όλα τα απαραίτητα πεδία
        if(!checkNullFields())
            throw new IllegalArgumentException("Υπάρχουν κενά πεδία τα οποία είναι υποχρεωτικά!");
    }

    /**
     * Abstract συνάρτηση που επιστρέφει το τύπο του προϊόντος.
     * @return {@link ProductType}
     */
    public abstract ProductType getType();

    // Getters & Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Number getQuantity() {
        return quantity;
    }

    public void setQuantity(Number quantity) {
        this.quantity = quantity;
    }

    /**
     * Ελέγχει αν τα απαραίτητα πεδία έχουν τιμές. Επίσης ελέγχει και τα αριθμητικά πεδία όπως η τιμή και ποσότητα να έχουν ορθές τιμές.
     * @return true αν όλα τα απαραίτητα πεδία δεν είναι null, αλλιώς false
     */
    public boolean checkNullFields() {
        return Objects.nonNull(title)
                && !title.isEmpty()
                && Objects.nonNull(description) && !description.isEmpty()
                && Objects.nonNull(category)
                && Objects.nonNull(price) && price.compareTo(BigDecimal.ZERO) > 0
                && Objects.nonNull(quantity) && quantity.doubleValue() >= 0;
    }


}
