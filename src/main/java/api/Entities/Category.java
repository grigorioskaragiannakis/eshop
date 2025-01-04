package api.Entities;

import java.util.List;

/**
 * Κλάση η οποία κρατάει πληροφορίες για τη κατηγορία του προϊόντος καθώς και τις υποκατηγορίες.
 */
public class Category {
    private String name;
    private List<SubCategory> subcategories;

    /**
     * Constructor που αρχικοποιεί τη κατηγορία με το όνομα και τις υποκατηγορίες.
     * @param name
     * @param subcategories
     */
    public Category(String name, List<SubCategory> subcategories) {
        this.name = name;
        this.subcategories = subcategories;
    }

    // Getters & Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SubCategory> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(List<SubCategory> subcategories) {
        this.subcategories = subcategories;
    }

    @Override
    public String toString() {
        return name;
    }
}
