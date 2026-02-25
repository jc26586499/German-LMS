package dao.impl;

import dao.UserDao;
import model.User;
import util.Tool;
import vo.UserVO;

import java.sql.*;

public class UserDaoImpl implements UserDao {

    @Override
    public User login(String username, String password) throws SQLException {

        String sql = "SELECT user_id, username, password, balance, role " +
                     "FROM users WHERE username=? AND password=?";

        Connection conn = Tool.getDb();
        if (conn == null) throw new SQLException("DB connection failed.");

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                u.setBalance(rs.getInt("balance"));
                u.setRole(rs.getString("role"));
                return u;
            }

        } finally {
            try { conn.close(); } catch (Exception ignore) {}
        }
    }

    @Override
    public boolean existsByUsername(String username) throws SQLException {

        String sql = "SELECT 1 FROM users WHERE username=? LIMIT 1";

        Connection conn = Tool.getDb();
        if (conn == null) throw new SQLException("DB connection failed.");

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } finally {
            try { conn.close(); } catch (Exception ignore) {}
        }
    }

    @Override
    public int insertUserReturnId(String username, String password, int balance, String role) throws SQLException {

        String sql = "INSERT INTO users (username, password, balance, role) VALUES (?,?,?,?)";

        Connection conn = Tool.getDb();
        if (conn == null) throw new SQLException("DB connection failed.");

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setInt(3, balance);
            ps.setString(4, role);

            int rows = ps.executeUpdate();
            if (rows != 1) return -1;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
            return -1;

        } finally {
            try { conn.close(); } catch (Exception ignore) {}
        }
    }

    @Override
    public UserVO findUserStatusByUsername(String username) throws SQLException {

        String sql = "SELECT user_id, username, balance, role, unlocked_category " +
                     "FROM view_user_status WHERE username=?";

        Connection conn = Tool.getDb();
        if (conn == null) throw new SQLException("DB connection failed.");

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {

                UserVO user = null;

                while (rs.next()) {
                    if (user == null) {
                        user = new UserVO();
                        user.setUserId(rs.getInt("user_id"));
                        user.setUsername(rs.getString("username"));
                        user.setBalance(rs.getInt("balance"));
                        user.setRole(rs.getString("role"));
                    }

                    // ✅ 重點：不要去改 getUnlockedCategories() 回傳的 list
                    // 直接用 UserVO.addUnlockedCategory()
                    String unlocked = rs.getString("unlocked_category");
                    if (unlocked != null) {
                        user.addUnlockedCategory(unlocked);
                    }
                }

                return user;
            }

        } finally {
            try { conn.close(); } catch (Exception ignore) {}
        }
    }
}