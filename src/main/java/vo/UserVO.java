package vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 聚合用 UserVO
 */
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private int userId;
    private String username;
    private int balance;
    private String role;

    private final List<String> unlockedCategories = new ArrayList<>();
  

    public UserVO() {}

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getBalance() { return balance; }
    public void setBalance(int balance) { this.balance = balance; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public List<String> getUnlockedCategories() {
        return Collections.unmodifiableList(unlockedCategories);
    }

 
    
    public void addUnlockedCategory(String category) {
        if (category == null) return;
        String c = category.trim();
        if (c.isEmpty()) return;
        if (!unlockedCategories.contains(c)) {
            unlockedCategories.add(c);
        }
    }

    @Override
    public String toString() {
        return "UserVO{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", balance=" + balance +
                ", role='" + role + '\'' +
                ", unlockedCategories=" + unlockedCategories +
                '}';
    }
}