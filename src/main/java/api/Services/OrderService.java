package api.Services;

import api.Entities.Cart;
import api.Entities.Order;
import api.Entities.Product;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Κλάση που διαχειρίζεται τις παραγγελίες
 */
public class OrderService {

    // Κάνουμε depedent το cartService καθώς και μία λίστα που περιέχει ένα map με το username του χρήστή καθώς και τις παραγγελίες που έχει κάνει
    private final CartService cartService;
    private final Map<String, List<Order>> userOrders;

    /**
     * Constructor που αρχικοποιεί τις απαραίτητες μεταβλητές πο χρειαζόμαστε.
     * @param cartService
     */
    public OrderService(CartService cartService) {
        this.cartService = cartService;
        this.userOrders = new HashMap<>();
    }


    /**
     * Συνάρτηση η οποία δημιουργεί μία νέα παραγγελία με βάση το καλάθι του πελάτη
     * @param username το username του πελάτη
     * @return το αντικείμενο της παραγγελίας {@link Order}
     */
    public Order placeOrder(String username) {
        Cart cart = cartService.getCartForUser(username); // Παίρνουμε το καλάθι του χρήστη

        if(Objects.isNull(cart)){
            throw new IllegalArgumentException("No order for this user!");
        }
        // Δημιουργούμε μια νέα παραγγελία
        Order order = new Order(username, cartService.getCartForUser(username));

        // Προσθέτουμε τη παραγγελία στη λίστα με όλες τις παραγγελίες
        userOrders.computeIfAbsent(username, k -> new ArrayList<>()).add(order);

        return order; // επιστρέφουμε τη παραγγελία
    }

    /**
     * Επιστρέφουμε όλες τις παραγγελίες του χρήστη
     * @param username to username του χρήστη που θέλουμε να τραβήξουμε τις παραγγλίες.
     * @return τη λίστα με όλες τις παραγγελίες του χρήστη
     */
    public List<Order> getOrdersByUser(String username) {
        return userOrders.getOrDefault(username, new ArrayList<>());
    }


    /**
     * Επιστρέφει τη παραγγελία του χρήστη με βάση το id της παραγγελίας
     * @param username το username του πελάτη
     * @param orderId το Id της παραγγελίας
     * @return ένα αντικείμενο {@link Order}, δηλαδή τη παραγγελία του χρήστη
     */
    public Order getOrderById(String username, int orderId) {
        List<Order> orders = userOrders.get(username); // παίρνουμε όλες τις παραγγελίες του χρήστη

        // αν υπάρχει παραγγελία τότε επέστρεψέ την
        if (Objects.nonNull(orders)) {
            for (Order order : orders) {
                if (order.getOrderId() == orderId) {
                    return order;
                }
            }
        }
        return null;
    }

    /**
     * Επιστρέφει όλες τις παραγγελίας που υπάρχουν στο σύστημα
     * @return λίστα με όλες τις παραγγελίες
     */
    public List<Order> getAllOrders() {

        List<Order> allOrders = new ArrayList<>();

        for (List<Order> orders : userOrders.values()) {
            allOrders.addAll(orders); // πρόσθεσε κάθε παραγγελία σε μία λίστα
        }

        return allOrders; // επιστροφή όλων των παραγγελιών
    }

    /**
     * Επιστρέφει τα προϊόντα που υπάρχουν στις περισσότερες παραγγελίες.
     * @return Λίστα με τα προϊόντα
     */
    public List<Product> getMostOrderedProducts() {
        Map<Product, Integer> productFrequency = new HashMap<>();

        // Διατρέχουμε όλες τις παραγγελίας
        for (List<Order> orders : userOrders.values()) {
            for (Order order : orders) {
                Set<Product> uniqueProducts = order.getCart().getItems().keySet();
                for (Product product : uniqueProducts) {
                    productFrequency.put(product, productFrequency.getOrDefault(product, 0) + 1); // Μετράμε το frequency
                }
            }
        }

        // Επιστρέφουμε τα most frequent products
        return productFrequency.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }


    /**
     * Επιστρέφει το map των χρηστών και των παραγγελιών του κάθε χρήστη
     * @return {@link Map} χρηστών/ παραγγελιών.
     */
    public Map<String, List<Order>> getUserOrders() {
        return userOrders;
    }
}
