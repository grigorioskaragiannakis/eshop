
import api.Entities.*;
import api.Services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Κλάση που περιέχει δοκιμαστικές test συναρτήσεις που αφορούν το {@link ProductService}
 */
class ProductServiceTest {

    private ProductService productService;
    private WeightProduct p1;
    private WeightProduct p2;
    private WeightProduct p3;

    @BeforeEach
    void setUp() {
        SubCategory fruitSubCategory = new SubCategory("Fruit");
        Category foodCategory = new Category("Food", List.of(fruitSubCategory));

        p1 = new WeightProduct("Apple", "Super fresh!",foodCategory,new BigDecimal("1.50"), 20.5);
        p2 = new WeightProduct("Grapes", "Super fresh!",foodCategory,new BigDecimal("1.50"), 20.5);
        p3 = new WeightProduct("Lemons", "Super fresh!",foodCategory,new BigDecimal("1.50"), 20.5);

        productService = new ProductService();
    }

    /**
     * Δοκιμαστική συνάρτηση που ελέγχει αν προστίθονται ορθά τα προϊόντα
     */
    @Test
    void testAddProduct() {
        productService.addProduct(p1);
        productService.addProduct(p2);
        productService.addProduct(p3);

        List<Product> products = productService.getAllProducts();
        assertEquals(3, products.size());
        assertEquals(p1, products.get(0));
    }

    /**
     * Δοκιμαστική συνάρτηση που ελέγχει αν επιστρέφεται σωστά το προϊόν με βάση το τίτλο που δίνουμε.
     */
    @Test
    void testGetProductByName() {
        productService.addProduct(p1);
        productService.addProduct(p2);
        productService.addProduct(p3);

        Product product = productService.getProductByName("Apple");
        assertNotNull(product);
        assertEquals("Apple", product.getTitle());
    }

    /**
     * Συνάρτηση που ελέγχει αν επιστρέφονται ορθά το σύνολο όλων των προϊόντων.
     */
    @Test
    void testGetAllProducts() {
        productService.addProduct(p1);
        productService.addProduct(p2);
        productService.addProduct(p3);

        List<Product> products = productService.getAllProducts();
        assertEquals(3, products.size());
        assertTrue(products.contains(p1));
        assertTrue(products.contains(p2));
        assertTrue(products.contains(p3));
    }

}
