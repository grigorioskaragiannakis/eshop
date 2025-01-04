package api.Services;

import api.Entities.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Κλάση που διαχειρίζεται τις λειτουργίες που αφορούν τα προϊόντα μας.
 */
public class ProductService {

    // Λίστα που θα κρατάει όλα τα διαθέσιμα προϊόντα
    private final List<Product> products;
    private List<Category> categories;

    /**
     * Constructor για αρχικοποίηση της λίστας των προϊόντων
     */
    public ProductService() {
        this.products = new ArrayList<>();
    }

    /**
     * Προσθέτει ένα νέο προϊόν τύπου {@link Product} στα προϊόντα.
     * @param product το προϊόν που θα προστεθεί.
     */
    public void addProduct(Product product) {
        products.add(product);
    }

    /**
     * Συνάρτηση για προσθήκη προϊόντος με βάση τα χαρακτηριστικά (details)
     * @param productDetails τα χαρακτηριστικά του προϊόντος π.χ (Τιτλος: "xxxx", Περιγραφή: "xxxxx"...)
     */
    public void addProductByDetails(Map<String, String> productDetails) {
        // Παίρνουμε τα χαρακτηριστικά των προϊόντων από το productDetails
        try {
            String title = productDetails.get("Τίτλος");
            String description = productDetails.get("Περιγραφή");
            String categoryName = productDetails.get("Κατηγορία");
            BigDecimal price = new BigDecimal(productDetails.get("Τιμή").replace("€", "").replace(",", ".")); // απαραίτητες αντικαταστάσεις
            String quantityStr = productDetails.get("Ποσότητα");
            String subcategoryName = productDetails.get("Υποκατηγορία");

            Number quantity;
            Product product = null;
            List<SubCategory> subcategory = getSubCategoryByName(categoryName, subcategoryName);


            // Ελέγχουμε με τι τελειώνει η κατάληξη για να δούμε αν θα είναι WeightProduct ή PiecesProduct
            if (quantityStr.endsWith("kg")) {
                quantity = Double.parseDouble(quantityStr.replace("kg", "").trim());
                product = new WeightProduct(title, description, new Category(categoryName, subcategory), price, quantity);

            } else if (quantityStr.endsWith("τεμάχια")) {
                quantity = Integer.parseInt(quantityStr.replace("τεμάχια", "").replace("τμχ", "").trim());
                product = new PiecesProduct(title, description, new Category(categoryName, subcategory), price, quantity);
            }

            addProduct(product); // Προσθήκη προϊόντος στη λίστα με όλα τα προϊόντα
        } catch (Exception e) {
            System.err.println("Error reading product details.. " + productDetails);
            e.printStackTrace();
            throw new IllegalArgumentException("Illegal format! " + e);
        }
    }



    /**
     * Επιστρέφει το προϊόν με βάση τον τίτλο του.
     * @param title ο τίτλος του προϊόντος.
     * @return το προϊόν που βρέθηκε ή αν δεν βρεθεί τότε null.
     */
    public Product getProductByName(String title) {
        if (title == null || title.trim().isEmpty()) {
            return null;
        }

        return products.stream()
                .filter(Objects::nonNull)
                .filter(product -> product.getTitle() != null)
                .filter(product -> product.getTitle().toLowerCase().contains(title.toLowerCase()))
                .findFirst()
                .orElse(null);
    }


    /**
     * Επιστρέφει όλα τα προϊόντα που έχουμε.
     * @return μία λίστα με όλα τα προϊόντα.
     */
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    public List<Product> getProducts() {
        return products;
    }

    public List<Category> getCategories() {
        return categories;
    }


    /**
     * Επιστρέφει τη κατηγορία με βάση το όνομα
     * @param name το όνομα της κατηγορίας που θέλουμε
     * @return ένα αντικείμενο τύπου {@link Category}, δηλαδή η κατηγορία
     */
    public Category getCategoryByName(String name) {
        for (Category category : categories) {
            if (category.getName().equalsIgnoreCase(name)) {
                return category;
            }
        }
        return null;
    }


    /**
     * Επιστρέφει την υποκατηγορία με βάση το όνομα της κατηγορίας και το όνομα της υποκατηγορίας
     * @param categoryName το όνομα της κατηγορίας
     * @param subcategoryName το όνομα της υποκατηγορίας
     * @return λίστα με όλες τις υποκατηγορέις που βρέθηκαν τα δοθέντα στοιχεία
     */
    public List<SubCategory> getSubCategoryByName(String categoryName, String subcategoryName) {
        Category category = getCategoryByName(categoryName);
        if (category == null) {
            return Collections.emptyList();
        }

        List<SubCategory> matchingSubcategories = new ArrayList<>();

        for (SubCategory subcategory : category.getSubcategories()) {
            if (subcategory.getName().equalsIgnoreCase(subcategoryName)) {
                matchingSubcategories.add(subcategory);
            }
        }

        return matchingSubcategories;
    }


    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    /**
     * Επιστρέφει όλα τα προϊόντα που δεν είναι πλέον διαθέσιμα
     * @return Λίστα με όλα τα μη-διαθέσημα προϊόντα
     */
    public List<Product> getUnavailableProducts(){

        List<Product> unavailableProducts = new ArrayList<>();

        // Αν η ποσότητα είναι ίση με μηδέν τότε το προϊόν δεν είναι δθαέσιμο άρα το προσθέτουμε στη λίστα με όλα τα μη-διαθέσιμα προϊόντα
        // την οποία θα επιστρέψουμε
        for(Product product : products){
            if(product.getQuantity().doubleValue() == 0.0)
                unavailableProducts.add(product);
        }

        return unavailableProducts;
    }

    /**
     * Επιστρέφει όλα τα προϊόντα με βάση τον τίτλο και την κατηγορία
     * @param title ο τίτλος του προϊόντος
     * @param category η κατηγορία του προϊόντος
     * @return λίστα με όλα τα προϊόντα που βρέθηκαν
     */
    public List<Product> getProductsByTitleAndCategory(String title, String category) {
        return products.stream()
                .filter(product -> (title == null || title.isEmpty() || product.getTitle().toLowerCase().contains(title.toLowerCase())))
                .filter(product -> product.getCategory().getName().equalsIgnoreCase(category))  // Make sure to match category exactly
                .collect(Collectors.toList());
    }

    /**
     * Επιστρέφει όλα τα προϊόντα με βάση τον τίτλο και την υποκατηγορία
     * @param title ο τίτλος του προϊόντος
     * @param subcategory η υποκατηγορία του προϊόντος
     * @return λίστα με όλα τα προϊόντα που βρέθηκαν
     */
    public List<Product> getProductsByTitleAndSubcategory(String title, String subcategory) {
        return products.stream()
                .filter(product -> (title == null || title.isEmpty() || product.getTitle().toLowerCase().contains(title.toLowerCase()))) // Handle null or empty title
                .filter(product -> product.getCategory().getSubcategories().stream()
                        .anyMatch(sub -> sub.getName().equalsIgnoreCase(subcategory)))  // Match subcategory
                .collect(Collectors.toList());
    }

    /**
     * Επιστρέφει όλα τα προϊόντα με βάση τον τίτλο μόνο
     * @param title ο τίτλος του προϊόντος
     * @return λίστα με όλα τα προϊόντα που βρέθηκαν
     */
    public List<Product> getProductsByTitle(String title) {
        return products.stream()
                .filter(product -> (title == null || title.isEmpty() || product.getTitle().toLowerCase().contains(title.toLowerCase()))) // Handle null or empty title
                .collect(Collectors.toList());
    }


}
