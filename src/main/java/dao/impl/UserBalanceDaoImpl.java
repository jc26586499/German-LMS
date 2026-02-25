package dao.impl;

import dao.UserBalanceDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserBalanceDaoImpl implements UserBalanceDao {

    @Override
    public int getBalance(Connection conn, int userId) throws SQLException {
        String sql = "SELECT balance FROM users WHERE user_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("balance");
                throw new SQLException("User not found: user_id=" + userId);
            }
        }
    }

    @Override
    public boolean updateBalance(Connection conn, int userId, int newBalance) throws SQLException {
        String sql = "UPDATE users SET balance=? WHERE user_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newBalance);
            ps.setInt(2, userId);
            return ps.executeUpdate() == 1;
        }
    }
}