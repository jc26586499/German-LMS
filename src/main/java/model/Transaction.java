package model;

import java.sql.Timestamp;

/**
 * Table: transactions
 * Columns:
 * - t_id (PK, AI)
 * - user_id (FK -> users.user_id)
 * - amount
 * - description
 * - t_date (timestamp, default current_timestamp)
 */
public class Transaction {

    private int tId;
    private Integer userId;        // DB 可為 NULL
    private Integer amount;        // DB 可為 NULL
    private String description;
    private Timestamp tDate;       // DB: t_date

    public Transaction() {}

    public Transaction(int tId, Integer userId, Integer amount, String description, Timestamp tDate) {
        this.tId = tId;
        this.userId = userId;
        this.amount = amount;
        this.description = description;
        this.tDate = tDate;
    }

    public int gettId() {
        return tId;
    }

    public void settId(int tId) {
        this.tId = tId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
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
        return "Transaction{" +
                "tId=" + tId +
                ", userId=" + userId +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", tDate=" + tDate +
                '}';
    }
}