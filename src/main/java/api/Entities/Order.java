package api.Entities;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Κλάση που αντιπροσωπεύει μια παραγελλία ενός χρήστη
 */
public class Order {
    private static int nextOrderCounter = 1;
    private int orderId;
    private String username;
    private Date orderDate;
    private Cart cart; // Τα τελικά προϊόντα είναι αυτά που έχει στο καλάθι του
    private BigDecimal totalCost;

    /**
     * Constructor που δημιουργεί μια νέα παραγγελία
     * @param username το username του χρήστη που κάνει μια παραγελλία
     * @param cart το καλάθι με τα τελικά προϊόντα
     */
    public Order(String username, Cart cart) {
        this.orderId = nextOrderCounter++;
        this.username = username;
        this.orderDate = new Date();
        this.cart = new Cart(cart);
        this.totalCost = calculateTotalCost();
    }

    /**
     * Υπολογίζει το τελικό κόστος με βάση τα προϊόντα που είχε στο καλάθι
     * @return το τελικό κόστος {@link BigDecimal}
     */
    private BigDecimal calculateTotalCost() {
        return cart.getTotalCost();
    }

    // Getters & Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "Order{" +
                "username='" + username + '\'' +
                ", orderDate=" + orderDate +
                ", cart=" + cart +
                ", totalCost=" + totalCost +
                '}';
    }
}
