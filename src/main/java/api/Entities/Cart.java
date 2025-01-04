package api.Entities;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Η κλάση Cart αναπαριστά το καλάθι του αγοραστή και περιέχει τα προϊόντα καθώς και τη ποσότητα αυτών.
 * Επιτρέπει προσθήκη, αφαίρεση και ενημέρωση της ποσότητας ενος προϊόντος.
 */
public class Cart {

    // Map όπου για κάθε προϊόν περιέχετε η ποσότητα αυτού
    private Map<Product, Number> products;

    /**
     * Constructor για αρχικοποίηση του Map.
     */
    public Cart() {
        this.products = new HashMap<>();
    }

    /**
     * Copy constructor για αντιγραφή του cart
     * @param other το cart που θέλουμε να αντιγράψουμε.
     */
    public Cart(Cart other) {
        this.products = new HashMap<>(other.products); // Clone the list of items
    }

    /**
     * Προσθέτει ένα προϊόν στο καλάθι αγορών.
     * @param product Το προϊόν που θέλουμε να προσθέσουμε
     * @param quantity η ποσότητα, την οποία την δηλώνουμε {@link Number} καθώς το προϊόν μπορεί να είναι είτε {@link PiecesProduct} είτε {@link WeightProduct}
     */
    public void addProduct(Product product, Number quantity) {

        // Αν το Map περιέχει ήδη το product, απλά αυξάνουμε τη ποσότητα κατα quantity.
        if (products.containsKey(product)) {
            Number quantityInCard = products.get(product);

            if (product instanceof WeightProduct) {
                products.put(product, ((BigDecimal) quantityInCard).add((BigDecimal) quantity));
            } else {
                products.put(product, quantityInCard.intValue() + quantity.intValue());
            }
        } else { // Αν δεν υπάρχει τότε το προσθέτουμε στο Map
            products.put(product, quantity);
        }
    }

    /**
     * Αφαιρεί ένα προϊόν απο το καλάθι αγορών.
     * @param product
     */
    public void removeProduct(Product product) {
        products.remove(product);
    }

    /**
     * Ανανενώνει την ποσότητα ενός προϊόντος που βρίσκεται στο καλάθι του αγοραστή.
     * @param product
     * @param quantity
     */
    public void updateQuantity(Product product, Number quantity) {
        if (quantity.doubleValue() <= 0) {
            removeProduct(product);
        } else {
            products.put(product, quantity);
        }
    }

    /**
     * Υπλογίζει το τελικό κόστος του καλαθιού με βάση τα προϊόντα που υπάρχουν μέσα σε αυτό.
     * @return Το τελικό ποσό
     */
    public BigDecimal getTotalCost() {
        return products.entrySet().stream()
                .map(entry -> entry.getKey().getPrice().multiply(new BigDecimal(entry.getValue().toString())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    /**
     * Επιστρέφει το map με όλα τα προϊόντα που υπάρχουν στο καλάθι καθώς και τη ποσότητα αυτών.
     * @return {@link Map} όπου τα κλειδιά είναι τα προϊόντα και οι ποσότητες τα values.
     */
    public Map<Product, Number> getItems() {
        return products;
    }
}
