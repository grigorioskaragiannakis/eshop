import api.Entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Κλάση που ελέγχει διάφορες λειτουργικότητες του καλαθιού του αγοραστή {@link CartTest}
 */
public class CartTest {

    private Cart cart; // Το καλάθι
    private PiecesProduct piecesProduct; // Ένα προϊον που μετριέται σε κομμάτια
    private WeightProduct weightProduct; // Ένα προϊόν που μετριέται σε βάρος

    @BeforeEach
    void setUp() {
        cart = new Cart();

        // Ορισμός κατηγοριών και υποκατηγοριών
        SubCategory fruitSubCategory = new SubCategory("Fruit");
        Category foodCategory = new Category("Food", List.of(fruitSubCategory));

        SubCategory bookSubCategory = new SubCategory("Thriller");
        Category bookCategory = new Category("Books", List.of(bookSubCategory));

        weightProduct = new WeightProduct("Apple", "Fresh red apple", foodCategory, new BigDecimal("1.50"), 15.4);
        piecesProduct = new PiecesProduct("Learning Java", "A Nice book to learn java", bookCategory, new BigDecimal("7.00"), 2);
    }

    /**
     * Συνάρτηση που ελέγχει αν η προσθήκη των προϊόντων γίνεται με επιτυχία
     */
    @Test
    void testAddProducts() {
        cart.addProduct(weightProduct, 5.4);
        cart.addProduct(piecesProduct, 30);

        // Αναμένουμε 2 αντικείμενα στο καλάθι του αγοαρστή
        assertEquals(2, cart.getItems().size());

        // Έλεγχος για το εάν υπάρχει το προϊόν στο καλάθι και με τι ποσότητα (βάρος)
        assertTrue(cart.getItems().containsKey(weightProduct));
        assertEquals(5.4, cart.getItems().get(weightProduct));

        // Έλεγχος για το εάν υπάρχει το προϊόν στο καλάθι και με τι ποσότητα (κομμάτια)
        assertTrue(cart.getItems().containsKey(piecesProduct));
        assertEquals(30, cart.getItems().get(piecesProduct));
    }

    /**
     * Συνάρτηση που ελέγχει την αφαίρεση ενός προϊόντος απο το καλάθι.
     */
    @Test
    void testRemoveProduct() {
        cart.addProduct(weightProduct, 5.4);
        cart.addProduct(piecesProduct, 30);

        // Αφαίρεση ενός προϊόντος
        cart.removeProduct(weightProduct);

        // Έλεγχος για τα προϊόντα που έχουν μείνει στο καλάθι
        assertFalse(cart.getItems().containsKey(weightProduct));
        assertTrue(cart.getItems().containsKey(piecesProduct));
    }

    /**
     * Συνάρτηση που κάνει Update τη ποσότητα ενός προϊόντος στο καλάθι
     */
    @Test
    void testUpdateQuantity() {
        cart.addProduct(weightProduct, 5.4);

        // Ανανέωση ποσότητας από 5.4 σε 10
        cart.updateQuantity(weightProduct, 10.0);

        // Έλεγχος για το εάν η νέα ποσότητα είναι 10
        assertEquals(10.0, cart.getItems().get(weightProduct));
    }

    /**
     * Συνάρτηση που υπολογίζει το τελικό κόστος του καλαθιού
     */
    @Test
    void testGetTotalCost() {
        cart.addProduct(weightProduct, 5.4);
        cart.addProduct(piecesProduct, 30);

        // Έλεγχος για το εάν το κόστος που υπολογίζουμε εδώ είναι το αναμενόμενο
        BigDecimal expectedCost = weightProduct.getPrice().multiply(new BigDecimal("5.4"))
                .add(piecesProduct.getPrice().multiply(new BigDecimal("30")));


        assertEquals(expectedCost, cart.getTotalCost());
    }



}
