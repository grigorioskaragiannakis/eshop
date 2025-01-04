
import api.Entities.*;
import api.Services.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Δοκιμάστική κλάση που περιέχει test συναρτήσεις για την {@link CartService}
 */
class CartServiceTest {

    private WeightProduct p1;
    private WeightProduct p2;
    private WeightProduct p3;
    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartService = new CartService();

        SubCategory fruitSubCategory = new SubCategory("Fruit");
        Category foodCategory = new Category("Food", List.of(fruitSubCategory));

        p1 = new WeightProduct("Apple", "Super fresh!",foodCategory,new BigDecimal("1.50"), 20.5);
        p2 = new WeightProduct("Grapes", "Super fresh!",foodCategory,new BigDecimal("1.50"), 20.5);
        p3 = new WeightProduct("Lemons", "Super fresh!",foodCategory,new BigDecimal("1.50"), 20.5);
    }

    /**
     * Δοκιμαστική συνάρτηση για την δημιουργία καλαθιού για τον χρήστη
     */
    @Test
    void testCreateCartForUser() {
        String username = "user1";
        cartService.createCartForUser(username);

        // Έλεγχος αν έχει δημιουργηθεί σωστά το καλάθι του χρήστη
        Cart cart = cartService.getCartForUser(username);
        assertNotNull(cart, "Cart is null..");
    }

    /**
     * Δοκιμαστική συνάρτηση για τη προσθήκη ενός προϊόντος στο καλάθι
     */
    @Test
    void testAddItemToCart() {
        String username = "user1";
        cartService.createCartForUser(username);

        boolean result = cartService.addItemToCart(username, p1, 2);

        // Έλεγχος αν προστέθηκε το προϊόν με επιτυχία
        assertTrue(result, "Product not added succesfully!");
        assertEquals(1, cartService.getCartForUser(username).getItems().size(), "Cart does not contain only 1 product.");
        assertEquals(2, cartService.getCartForUser(username).getItems().get(p1), "Wrong quantity.");
    }

    /**
     * ΔΟκιμαστική συνάρτηση για αφαίρεση ενός προϊόντος απο το καλάθι του χρήστη
     */
    @Test
    void testRemoveItemFromCart() {
        String username = "user1";
        cartService.createCartForUser(username);

        cartService.addItemToCart(username, p1, 5);

        boolean result = cartService.removeItemFromCart(username, "Apple", 3);

        // Έλεγχος αν αφαιρέθηκε με επιτυχια το προϊόν
        assertTrue(result, "Removing item has failed..");
        assertEquals(2, cartService.getCartForUser(username).getItems().get(p1), "Quantity should now be equal to 2, since initially 5");
    }


    /**
     * Δοκιμαστική συνάρτηση που επιστρέφει το συνολικό κόστος για το καλάθι του χρήστη
     */
    @Test
    void testGetTotalCostForUser() {
        String username = "user1";
        cartService.createCartForUser(username);

        cartService.addItemToCart(username, p1, 2); // 2 * 1.5 = 3
        cartService.addItemToCart(username, p2, 1); // 1 * 1.5 = 1.5

        // Το τελικό κόστος του καλαθιού θα πρέπει να είναι 4.5

        BigDecimal totalCost = cartService.getTotalCostForUser(username);

        // Έλεγχος αν επιστρέφει σωστό συνολικό κόστος
        assertEquals(0, BigDecimal.valueOf(4.5).compareTo(totalCost), "Total cost not calculated correctlty");
    }

}
