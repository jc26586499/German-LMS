package dao;

import java.sql.Connection;
import java.sql.SQLException;

public interface UserBalanceDao {

    /**
     * 取得目前餘額
     */
    int getBalance(Connection conn, int userId) throws SQLException;

    /**
     * 更新餘額（直接 set）
     */
    boolean updateBalance(Connection conn, int userId, int newBalance) throws SQLException;
}