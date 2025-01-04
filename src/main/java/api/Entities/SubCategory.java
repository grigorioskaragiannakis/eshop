package api.Entities;

/**
 * Κλάση που περιέχει πληροφορίες σχετικά με την υποκατηγορία ενός προϊόντος.
 */
public class SubCategory {
    private String name;

    /**
     * Constructor για αρχικοποίηση μιας υποκατηγορίας.
     * @param name
     */
    public SubCategory(String name) {
        this.name = name;
    }

    // Getter & Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}