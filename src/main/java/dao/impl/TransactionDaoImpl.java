package dao.impl;

import dao.TransactionDao;
import util.Tool;
import vo.TransactionVO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDaoImpl implements TransactionDao {

    @Override
    public boolean insert(Connection conn, int userId, int amount, String description) throws SQLException {
        String sql = "INSERT INTO transactions (user_id, amount, description) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, amount);
            ps.setString(3, description);
            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public List<TransactionVO> findHistoryByUsername(String username) throws SQLException {
        // view_transaction_history: t_id, username, amount, description, t_date
        String sql = "SELECT t_id, username, amount, description, t_date " +
                     "FROM view_transaction_history WHERE username=? ORDER BY t_date DESC";

        Connection conn = Tool.getDb();
        if (conn == null) throw new SQLException("DB connection failed.");

        List<TransactionVO> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TransactionVO vo = new TransactionVO();
                    vo.settId(rs.getInt("t_id"));
                    vo.setUsername(rs.getString("username"));
                    vo.setAmount(rs.getInt("amount"));
                    vo.setDescription(rs.getString("description"));
                    vo.settDate(rs.getTimestamp("t_date"));
                    list.add(vo);
                }
            }
        } finally {
            try { conn.close(); } catch (Exception ignore) {}
        }

        return list;
    }

    @Override
    public List<TransactionVO> findHistoryByUserId(int userId) throws SQLException {
        String sql = "SELECT t_id, user_id, amount, description, t_date " +
                     "FROM transactions WHERE user_id=? ORDER BY t_date DESC";

        Connection conn = Tool.getDb();
        if (conn == null) throw new SQLException("DB connection failed.");

        List<TransactionVO> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TransactionVO vo = new TransactionVO();
                    vo.settId(rs.getInt("t_id"));
                    vo.setUserId(rs.getInt("user_id"));
                    vo.setAmount(rs.getInt("amount"));
                    vo.setDescription(rs.getString("description"));
                    vo.settDate(rs.getTimestamp("t_date"));
                    list.add(vo);
                }
            }
        } finally {
            try { conn.close(); } catch (Exception ignore) {}
        }

        return list;
    }
}