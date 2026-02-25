package dao.impl;

import dao.UserPermissionDao;
import util.Tool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserPermissionDaoImpl implements UserPermissionDao {

    private Connection requireConn() throws SQLException {
        Connection conn = Tool.getDb();
        if (conn == null) throw new SQLException("DB connection failed (Tool.getDb() returned null).");
        return conn;
    }

    @Override
    public boolean hasCategory(int userId, String category) throws SQLException {
        String sql =
                "SELECT 1 FROM user_permissions " +
                "WHERE user_id = ? AND category_purchased = ? " +
                "LIMIT 1";

        Connection conn = requireConn();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, category);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } finally {
            try { conn.close(); } catch (Exception ignore) {}
        }
    }

    @Override
    public List<String> findCategoriesByUser(int userId) throws SQLException {
        String sql =
                "SELECT category_purchased FROM user_permissions " +
                "WHERE user_id = ? " +
                "ORDER BY purchase_date DESC";

        Connection conn = requireConn();
        List<String> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String c = rs.getString("category_purchased");
                    if (c != null && !c.trim().isEmpty()) list.add(c.trim());
                }
            }
        } finally {
            try { conn.close(); } catch (Exception ignore) {}
        }

        return list;
    }

    @Override
    public boolean insert(int userId, String category) throws SQLException {
        String sql =
                "INSERT INTO user_permissions (user_id, category_purchased) " +
                "VALUES (?, ?)";

        Connection conn = requireConn();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, category);

            return ps.executeUpdate() == 1;

        } finally {
            try { conn.close(); } catch (Exception ignore) {}
        }
    }

    @Override
    public boolean hasCategory(Connection conn, int userId, String category) throws SQLException {
        String sql = "SELECT 1 FROM user_permissions WHERE user_id=? AND category_purchased=? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, category);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public boolean insert(Connection conn, int userId, String category) throws SQLException {
        String sql = "INSERT INTO user_permissions (user_id, category_purchased) VALUES (?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, category);
            return ps.executeUpdate() == 1;
        }
    }
}