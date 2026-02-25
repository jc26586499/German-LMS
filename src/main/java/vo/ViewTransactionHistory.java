package vo;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * MySQL VIEW: view_transaction_history
 */
public class ViewTransactionHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    private int tId;
    private String username;
    private Integer amount;
    private String description;
    private Timestamp tDate;

    public ViewTransactionHistory() {}

    public int getTId() { return tId; }
    public void setTId(int tId) { this.tId = tId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Timestamp getTDate() { return tDate; }
    public void setTDate(Timestamp tDate) { this.tDate = tDate; }

    @Override
    public String toString() {
        return "ViewTransactionHistory{" +
                "tId=" + tId +
                ", username='" + username + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", tDate=" + tDate +
                '}';
    }
}