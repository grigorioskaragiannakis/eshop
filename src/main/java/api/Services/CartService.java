package api.Services;

import api.Entities.Cart;
import api.Entities.Product;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Κλάση που διαχειρίζεται τις λειτουργίες του καλαθιού για κάθε χρήστη.
 */
public class CartService {

    // περιέχει το καλάθι κάθε χρήστη, το κλειδί είναι το username του χρήστη και value το καλάθι του
    private final Map<String, Cart> userCarts;

    /**
     * Constructor για αρχικοποίηση του map
     */
    public CartService() {
        this.userCarts = new HashMap<>();
    }

    /**
     * Αρχικοποιούμε το καλάθι του χρήστη, εαν δεν έχει καλάθι τότε φτιάχνουμε νέο (αρχικοποίηση)
     * @param username το username του χρήστη
     */
    public void createCartForUser(String username) {
        if (!userCarts.containsKey(username)) {
            userCarts.put(username, new Cart());
        }
    }

    /**
     * Επιστρέφει το καλάθι του χρήστη με το δοσμένο username
     * @param username το username του χρήστη που θέλουμε να πάρουμε το καλάθι του
     * @return το καλάθι του χρήστη
     */
    public Cart getCartForUser(String username) {
        return userCarts.get(username);
    }

    /**
     * Προσθήκη προϊόντος στο καλάθι του χρήστη
     * @param username το username του χρήστη που θέλουμε να προσθέσουμε προϊόν στο καλάθι
     * @param product το προϊόν
     * @param quantity η ποσότητα του προϊόντος
     * @return true αν προστέθηκε το προϊόν στο καλάθι με επιτυχία, διαφορετικά false
     */
    public boolean addItemToCart(String username, Product product, Number quantity) {
        // Τραβάμε το καλάθι του χρήστη για να προσθέσουμε το προϊόν
        Cart cart = userCarts.get(username);
        if (cart != null) { // Null check

            if (quantity.doubleValue() > product.getQuantity().doubleValue()) {
                return false;
            }
            cart.addProduct(product, quantity);

            return true; // επιτυχία
        }
        return false; // αποτυχία προσθήκης προϊόντος
    }

    /**
     * Αφαιρεί ποσότητα από ένα προϊόν στο καλάθι του χρήστη με βάση τον τίτλο του προϊόντος.
     *
     * @param username το username του χρήστη που θέλουμε να αφαιρέσουμε το προϊόν
     * @param title ο τίτλος του προϊόντος που θέλουμε να αφαιρέσουμε
     * @param quantity η ποσότητα που θέλουμε να αφαιρέσουμε
     * @return true αν αφαιρέθηκε το προϊόν, διαφορετικά false
     */
    public boolean removeItemFromCart(String username, String title, Number quantity) {
        Cart cart = userCarts.get(username); // Παίρνουμε το καλάθι του χρήστη
        if (cart != null) { // Έλεγχος αν έχει καλάθι

            // Ψάχνουμε το προϊόν με βάση τον τίτλο
            for (Map.Entry<Product, Number> item : cart.getItems().entrySet()) {

                Product product = item.getKey(); // Προϊόν
                Number currentQuantity = item.getValue(); // Ποσότητα προϊόντος στο καλάθι

                if (product.getTitle().equalsIgnoreCase(title)) {

                    if (quantity instanceof Integer) {
                        int current = currentQuantity.intValue();
                        int toRemove = quantity.intValue();

                        // Αν δώσει μεγαλύτερη ποσότητα αφαιρόυμε όλο το προϊόν διαφορετικά μειώνουμε τη ποσότητα
                        if (toRemove >= current) {
                            cart.getItems().remove(product);
                        } else {
                            cart.getItems().put(product, current - toRemove);
                        }

                        return true; // Επιτυχής αφαίρεση

                    } else if (quantity instanceof Double) {

                        double current = currentQuantity.doubleValue();
                        double toRemove = quantity.doubleValue();

                        // Αν δώσει μεγαλύτερη ποσότητα αφαιρόυμε όλο το προϊόν διαφορετικά μειώνουμε τη ποσότητα
                        if (toRemove >= current) {
                            cart.getItems().remove(product);
                        } else {
                            cart.getItems().put(product, current - toRemove);
                        }
                        return true; // Επιτυχής αφαίρεση
                    }

                    return false;
                }
            }
        }
        return false; // Δεν βρέθηκε το προϊόν
    }


    /**
     * Επιστρέφει το συνολοκό κόστος του καλαθιού για τον χρήστη
     * @param username το username του χρήστη που θέλουμε να πάρουμε το συνολικό κόστος του καλαθιού
     * @return το συνολικό κόστος σε {@link BigDecimal}
     */
    public BigDecimal getTotalCostForUser(String username) {
        Cart cart = userCarts.get(username);
        return cart != null ? cart.getTotalCost() : BigDecimal.ZERO;
    }
}
