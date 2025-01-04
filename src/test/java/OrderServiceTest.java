import static org.junit.jupiter.api.Assertions.*;

import api.Entities.*;
import api.Services.CartService;
import api.Services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

/**
 * Κλάση που περιέχει δοκιμαστικές συναρτήσεις για την {@link OrderService}
 */
public class OrderServiceTest {

    private CartService cartService;
    private OrderService orderService;
    private WeightProduct p1;
    private WeightProduct p2;
    private WeightProduct p3;

    @BeforeEach
    public void setUp() {
        cartService = new CartService();
        orderService = new OrderService(cartService);

        SubCategory fruitSubCategory = new SubCategory("Fruit");
        Category foodCategory = new Category("Food", List.of(fruitSubCategory));

        p1 = new WeightProduct("Apple", "Super fresh!",foodCategory,new BigDecimal("1.50"), 20.5);
        p2 = new WeightProduct("Grapes", "Super fresh!",foodCategory,new BigDecimal("1.50"), 20.5);
        p3 = new WeightProduct("Lemons", "Super fresh!",foodCategory,new BigDecimal("1.50"), 20.5);

        cartService.createCartForUser("user1");

        cartService.addItemToCart("user1", p1, 10.5);
        cartService.addItemToCart("user1", p2, 5.5);
        cartService.addItemToCart("user1", p3, 1.5);

    }

    /**
     * Δοκιμαστική συνάρτηση που ελέγχει αν ολοκληρώνεται μία παραγγελία με επιτυχία
     */
    @Test
    public void testPlaceOrder() {
        String username = "user1";

        Order order = orderService.placeOrder(username); // Κάνε τη παραγγελία

        assertNotNull(order, "Ordewr not succesfully created its null..");
        assertTrue(orderService.getAllOrders().size() > 0, "All orders should be greated than 0");

        Cart cart = cartService.getCartForUser(username);

        assertTrue(cart.getItems().isEmpty(), "After order cart should be empty");
    }

    /**
     * Έλεγχος για το εάν επιστρέφονται σωστά όλες οι παραγγελίας του χρήστη
     */
    @Test
    public void testGetOrdersByUser() {
        String username = "user1";

        try {
            orderService.placeOrder(username);
            orderService.placeOrder("user2");
        }
        catch (Exception e)
        {
            System.out.println("Error here, probably user does not have an order");
        }

        List<Order> orders = orderService.getOrdersByUser(username);

        assertEquals(1, orders.size(), "User should only have 1 order.");
    }

    /**
     * Έλεγχος για το εάν επιστρέφεται η σωστή παραγγελία με βάση το id της παραγγελίας.
     */
    @Test
    public void testGetOrderById() {
        String username = "user1";

        Order order = orderService.placeOrder(username);

        Order retrievedOrder = orderService.getOrderById(username, order.getOrderId());

        assertEquals(order.getOrderId(), retrievedOrder.getOrderId(), "Wrong order Id..");
    }

}
