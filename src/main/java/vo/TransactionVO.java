package vo;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 對應：
 * 1) transactions 表
 * 2) view_transaction_history
 */
public class TransactionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private int tId;            // t_id
    private int userId;         // 直接查 transactions 時使用
    private String username;    // 查 view_transaction_history 時使用
    private int amount;         // 正數=收入, 負數=支出
    private String description; // 說明
    private Timestamp tDate;    // 交易時間

    public TransactionVO() {}

    public int gettId() {
        return tId;
    }

    public void settId(int tId) {
        this.tId = tId;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp gettDate() {
        return tDate;
    }

    public void settDate(Timestamp tDate) {
        this.tDate = tDate;
    }

    @Override
    public String toString() {
        return "TransactionVO{" +
                "tId=" + tId +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", tDate=" + tDate +
                '}';
    }
}