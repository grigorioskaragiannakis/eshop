import api.Entities.Category;
import api.Entities.SubCategory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

/**
 * Κλάση που παρέχει δοκιμαστικές συναρτήσεις για την κλάση {@link Category}
 */
public class CategoryTest {

    /**
     * Συνάρτηση για έλεγχο σωστής δημιουργίας {@link Category} και {@link SubCategory}.
     */
    @Test
    void testCategoryCreation() {
        SubCategory subCategory1 = new SubCategory("Fruit");
        SubCategory subCategory2 = new SubCategory("Vegetables");

        Category category = new Category("Food", Arrays.asList(subCategory1, subCategory2));

        // Έλεγχος για το εάν έχουν αρχικοποοιηθεί σωστά η κατηγορία και οι υποκατηγορίες αυτής
        assertEquals("Food", category.getName());
        assertEquals(2, category.getSubcategories().size());
        assertTrue(category.getSubcategories().contains(subCategory1));
        assertTrue(category.getSubcategories().contains(subCategory2));
    }
}
