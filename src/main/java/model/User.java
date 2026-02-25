package model;

/**
 * Table: users
 * Columns:
 * - user_id (PK, AI)
 * - username (UNIQUE, NOT NULL)
 * - password (NOT NULL)
 * - role (enum: student/admin, default student)
 * - balance (int, default 0)
 */
public class User {

    private int userId;
    private String username;
    private String password;
    private String role;   // 建議維持 String，避免 enum 映射麻煩
    private int balance;

    public User() {}

    public User(int userId, String username, String password, String role, int balance) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.balance = balance;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", balance=" + balance +
                '}';
    }
}