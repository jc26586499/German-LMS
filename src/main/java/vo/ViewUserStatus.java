package vo;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * MySQL VIEW: view_user_status
 */
public class ViewUserStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    private int userId;
    private String username;
    private int balance;
    private String role;

    private String unlockedCategory;
    private Timestamp purchaseDate;

    public ViewUserStatus() {}

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getBalance() { return balance; }
    public void setBalance(int balance) { this.balance = balance; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getUnlockedCategory() { return unlockedCategory; }
    public void setUnlockedCategory(String unlockedCategory) { this.unlockedCategory = unlockedCategory; }

    public Timestamp getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(Timestamp purchaseDate) { this.purchaseDate = purchaseDate; }

    @Override
    public String toString() {
        return "ViewUserStatus{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", balance=" + balance +
                ", role='" + role + '\'' +
                ", unlockedCategory='" + unlockedCategory + '\'' +
                ", purchaseDate=" + purchaseDate +
                '}';
    }
}