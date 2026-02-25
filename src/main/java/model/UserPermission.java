package model;

import java.sql.Timestamp;

/**
 * Table: user_permissions
 * Columns:
 * - permission_id (PK, AI)
 * - user_id (FK -> users.user_id)
 * - category_purchased
 * - purchase_date (timestamp, default current_timestamp)
 *
 * UNIQUE(user_id, category_purchased)
 */
public class UserPermission {

    private int permissionId;
    private Integer userId;              // DB 可為 NULL，所以用 Integer 更安全
    private String categoryPurchased;    // DB: category_purchased
    private Timestamp purchaseDate;      // DB: purchase_date

    public UserPermission() {}

    public UserPermission(int permissionId, Integer userId, String categoryPurchased, Timestamp purchaseDate) {
        this.permissionId = permissionId;
        this.userId = userId;
        this.categoryPurchased = categoryPurchased;
        this.purchaseDate = purchaseDate;
    }

    public int getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(int permissionId) {
        this.permissionId = permissionId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getCategoryPurchased() {
        return categoryPurchased;
    }

    public void setCategoryPurchased(String categoryPurchased) {
        this.categoryPurchased = categoryPurchased;
    }

    public Timestamp getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Timestamp purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    @Override
    public String toString() {
        return "UserPermission{" +
                "permissionId=" + permissionId +
                ", userId=" + userId +
                ", categoryPurchased='" + categoryPurchased + '\'' +
                ", purchaseDate=" + purchaseDate +
                '}';
    }
}