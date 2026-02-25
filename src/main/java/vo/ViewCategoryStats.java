package vo;

import java.io.Serializable;

/**
 * MySQL VIEW: view_category_stats
 */
public class ViewCategoryStats implements Serializable {

    private static final long serialVersionUID = 1L;

    private String category;
    private int totalWords;
    private int totalPurchases;

    public ViewCategoryStats() {}

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getTotalWords() { return totalWords; }
    public void setTotalWords(int totalWords) { this.totalWords = totalWords; }

    public int getTotalPurchases() { return totalPurchases; }
    public void setTotalPurchases(int totalPurchases) { this.totalPurchases = totalPurchases; }

    @Override
    public String toString() {
        return "ViewCategoryStats{" +
                "category='" + category + '\'' +
                ", totalWords=" + totalWords +
                ", totalPurchases=" + totalPurchases +
                '}';
    }
}