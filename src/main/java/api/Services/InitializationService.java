package api.Services;

import api.Entities.*;
import api.Enums.ProductType;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Κλάση που περιέχει μεθόδους για διάβασμα των απαραίτητων αρχείων καθώς και αποθήκευση των πληροφοριών σε αρχεία.
 */
public class InitializationService {

    /**
     * Αρχικοποίηση όλων των κατηγοριών και υποκατηγοριών από αρχείο
     * @param productService το service που αφορά τη διαχείριση όλων των προϊόντων
     * @param fileName το όνομα του αρχείου
     */
    public static void initializeCategoriesFromFile(ProductService productService, String fileName){
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {

            // Δημιουργούμε μία λίστα που θα περιέχει όλες τις κατηγορίες που θα διαβάσουμε από το αρχείο
            List<Category>  categories = new ArrayList<>();
            String line;

            // Όσο υπάρχουν γραμμές
            while ((line =  reader.readLine()) != null) {
                // Χωρίζουμε τα κομμάτια τη γραμμήες
                String[] parts = line.split("\\(");

                String categoryName = parts[0].trim();

                // Αντικατάσταση του ( με κενό
                String subCategoryString = parts[1].replace(")", "").trim();

                // Διαβάζουμε τις υποκατηγορίες
                String[] subCategories = subCategoryString.split("@");

                // Τις προσθέτουμε σε μία λίστα με υποκατηγορίες
                List<SubCategory> subCategoryList = Arrays.stream(subCategories)
                        .map(SubCategory::new)
                        .collect(Collectors.toList());

                // Φτιάχνουμε τη κατηγορία με τις αντίστοιχες υποκατηγορίες
                Category category = new Category(categoryName, subCategoryList);
                categories.add(category);
            }

            productService.setCategories(categories);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Αρχικοποίηση προϊόντων από αρχείο.
     * @param productService το service που αφορά τη διαχείριση όλων των προϊόντων
     * @param fileName το όνομα του αρχείου
     */
    public static void initializeProductsFromFile(ProductService productService, String fileName) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8))) {
            String line;
            Map<String, String> productDetails = new HashMap<>();

            // Διάβασμα του αρχείου που περιέχει τα προϊόντα
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    // Αν δεν είναι κενή η γραμμή και έχει productDetails τότε προσθέτουμε το προϊόν που διαβάστηκε στη λίστα με τα προϊόντα
                    if (!productDetails.isEmpty()) {
                        System.out.println("Προσθήκη προϊόντος στο αρχείο: " + productDetails);

                        productService.addProductByDetails(productDetails);

                        productDetails.clear(); // Αδειάζουμε το productDetails για το επόμενο προϊόν
                    }
                } else {
                    String[] parts = line.split(":", 2);
                    if (parts.length == 2) {
                        productDetails.put(parts[0].trim(), parts[1].trim());
                    } else {
                        System.err.println("Λάθος format σε γραμμή: " + line);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Αποθηκεύευι όλα τα προϊοντα που περιέχονται στη λίστα productList στο αρχείο με τα προϊόντα
     * @param productList λίστα με όλα τα προϊόντα τύπου {@link Product}
     * @param filePath το όνομα του αρχείου που θα αποθηκεύσουμε τα προϊόντα
     */
    public static void saveProductsToFile(List<Product> productList, String filePath) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Διατρέχουμε όλα τα προϊόντα
            for (Product product : productList) {
                // Και γράφουμε στο αρχείο στο ίδιο format που μας δίνεται στην άσκηση
                writer.write("Τίτλος: " + product.getTitle());
                writer.newLine();
                writer.write("Περιγραφή: " + product.getDescription());
                writer.newLine();
                writer.write("Κατηγορία: " + product.getCategory());
                writer.newLine();
                // Αντικαθιστούμε τα [ και ] που επιστρέφονται απο το toString του αντκειμένου subCategory με κενό
                writer.write("Υποκατηγορία: " + product.getCategory().getSubcategories().toString().replace("[", "").replace("]", ""));
                writer.newLine();
                writer.write("Τιμή: " + product.getPrice() + "€");
                writer.newLine();

                // Ανάλογα τι τύπου θα είναι το προϊόν προσθέτουμε κατάληξη kg ή τεμάχια.
                String quantityStr;

                // Αν το προϊόν είναι μετρήσιμο σε βάρος
                if (product.getType() == ProductType.WEIGHT) {

                    // Παίρνουμε το double από το Number
                    double quantity = product.getQuantity().doubleValue();

                    // Αφαιρούμε το δεκαδικό αν είναι .0
                    if (quantity == (int) quantity) {
                        quantityStr = "Ποσότητα: " + (int) quantity + "kg";
                    } else {
                        quantityStr = "Ποσότητα: " + quantity + "kg";
                    }
                } else {
                    // Αν το προϊόν είναι PiecesPproduct
                    int quantity = product.getQuantity().intValue();  // Κάνουμε cast τη ποσότητα του Number σε int
                    quantityStr = "Ποσότητα: " + quantity + " τεμάχια";
                }

                writer.write(quantityStr);
                writer.newLine();
                writer.write("\n"); // Αλλάζουμε γραμμή
            }

        } catch (IOException e) {
            System.err.println("Λάθος κατα την αποθήκευση προϊόντος στο αρχείο!" + e.getMessage());
        }
    }

    /**
     * Αποθηκεύει όλες τις παραγγελίες που περιέχονται στη λίστα orders στο αρχείο με τις παραγγελίες
     * @param orderList λίστα με όλες τις παραγγελίες τύπου {@link Order}
     * @param filePath το όνομα του αρχείου που θα αποθηκεύσουμε τις παραγγελίες
     */
    public static void saveOrdersToFile(List<Order> orderList, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Διατρέχουμε όλες τις παραγγελίες
            for (Order order : orderList) {
                // Γράφουμε το ID της παραγγελίας
                writer.write("Αριθμός Παραγγελίας: " + order.getOrderId());
                writer.newLine();
                // Γράφουμε το όνομα του πελάτη
                writer.write("Όνομα Πελάτη: " + order.getUsername());
                writer.newLine();
                // Γράφουμε την ημερομηνία της παραγγελίας
                writer.write("Ημερομηνία Παραγγελίας: " + order.getOrderDate());
                writer.newLine();

                // Γράφουμε τα προϊόντα του καλαθιού
                writer.write("Προϊόντα:");
                writer.newLine();

                // Διατρέχουμε τα προϊόντα του καλαθιού
                for (Map.Entry<Product, Number> entry : order.getCart().getItems().entrySet()) {
                    Product product = entry.getKey();
                    Number quantity = entry.getValue();
                    writer.write(" - Τίτλος: " + product.getTitle());
                    writer.newLine();
                    writer.write("   Περιγραφή: " + product.getDescription());
                    writer.newLine();
                    writer.write("   Κατηγορία: " + product.getCategory());
                    writer.newLine();
                    writer.write("   Τιμή: " + product.getPrice() + "€");
                    writer.newLine();
                    writer.write("   Ποσότητα: " + quantity + (product.getType() == ProductType.WEIGHT ? "kg" : " τεμάχια"));
                    writer.newLine();
                    String type = product instanceof PiecesProduct ? "PiecesProduct" : "WeightProduct"; // Για να ξέρουμε για το διάβασμα τη τύπου προϊόν θα δημιουργήσουμε
                    writer.write("   Τύπος Προϊόντος: " + type);
                    writer.newLine();
                }

                // Γράφουμε το συνολικό κόστος της παραγγελίας
                writer.write("Συνολικό Κόστος: " + order.getTotalCost() + "€");
                writer.newLine();

                // Αλλάζουμε γραμμή πριν την επόμενη παραγγελία
                writer.write("\n");
            }

        } catch (IOException e) {
            System.err.println("Λάος κατά την αποθήκευση παραγγελίας στο αρχείο!" + e.getMessage());
        }
    }

    /**
     * Διαβάζει παραγγελίες από αρχείο και τις αρχικοποιεί στο σύστημα
     * @param fileName το όνομα του αρχείου
     */
    public static void initializeOrdersFromFile(OrderService orderService, String fileName) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8))) {
            String line;
            String username = null;
            Cart cart = null;
            int orderId = -1;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("Αριθμός Παραγγελίας:")) {
                    if (cart != null && username != null) {
                        Order order = new Order(username, cart);
                        order.setOrderId(orderId);
                        orderService.getUserOrders().computeIfAbsent(username, k -> new ArrayList<>()).add(order);
                    }

                    orderId = Integer.parseInt(line.split(":")[1].trim());
                    cart = new Cart();
                } else if (line.startsWith("Όνομα Πελάτη:")) {
                    username = line.split(":")[1].trim();
                } else if (line.startsWith("- Τίτλος:")) {
                    String title = line.split(":")[1].trim();
                    String description = reader.readLine().split(":")[1].trim();
                    String category = reader.readLine().split(":")[1].trim();
                    BigDecimal price = new BigDecimal(reader.readLine().split(":")[1].trim().replace("€", ""));
                    Number quantity = Double.parseDouble(reader.readLine().split(":")[1].trim().split(" ")[0]);
                    String productType = reader.readLine().split(":")[1].trim();

                    Product product;
                    if (productType.equals("PiecesProduct")) {
                        product = new PiecesProduct(title, description, new Category(category, null), price, quantity);
                    } else if (productType.equals("WeightProduct")) {
                        product = new WeightProduct(title, description, new Category(category, null), price, quantity);
                    } else {
                        throw new IllegalArgumentException("Άγνοστος τύπος!: " + productType);
                    }

                    cart.addProduct(product, quantity);
                }
            }

            // Πρόσθεσε τελευταία παραγγελία
            if (cart != null && username != null) {
                Order order = new Order(username, cart);
                order.setOrderId(orderId);
                orderService.getUserOrders().computeIfAbsent(username, k -> new ArrayList<>()).add(order);
            }

        } catch (Exception e) {
            System.err.println("Λάθος! " + e.getMessage());
        }
    }

}
