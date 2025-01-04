import api.Entities.Category;
import api.Entities.Product;
import api.Entities.SubCategory;
import api.Entities.WeightProduct;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Κλάση που παρέχει δοκιμαστικές test συναρτήσεις για την {@link Product}
 */
public class ProductTest {

    /**
     * Συνάρτηση που ελέγχει εάν λειτουργεί σωστά ο έλεγχος για τα null πεδία κατα την αρχικοποίηση.
     */
    @Test
    void testNullFields() {

        SubCategory fruitSubCategory = new SubCategory("Fruit");
        Category foodCategory = new Category("Food", List.of(fruitSubCategory));

        // Έλεγχος για το εάν σκάει ο κώδικας όταν βάζουμε null τίτλο
        assertThrows(IllegalArgumentException.class, () -> {
            new WeightProduct(null, "Super fresh!",foodCategory,new BigDecimal("1.50"), 10);
        });
    }
}
