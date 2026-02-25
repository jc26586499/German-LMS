package dao.impl;

import dao.ViewUserStatusDao;
import util.Tool;
import vo.UserVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewUserStatusDaoImpl implements ViewUserStatusDao {

    @Override
    public UserVO findByUserId(int userId) throws SQLException {

        String sql =
                "SELECT user_id, username, balance, role, unlocked_category " +
                "FROM view_user_status " +
                "WHERE user_id=?";

        Connection conn = Tool.getDb();
        if (conn == null) {
            throw new SQLException("DB connection failed.");
        }

        UserVO userVO = null;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    if (userVO == null) {
                        userVO = new UserVO();
                        userVO.setUserId(rs.getInt("user_id"));  
                        userVO.setUsername(rs.getString("username"));
                        userVO.setBalance(rs.getInt("balance"));
                        userVO.setRole(rs.getString("role"));
                    }

                    String category = rs.getString("unlocked_category");
                    userVO.addUnlockedCategory(category);
                }
            }

        } finally {
            try { conn.close(); } catch (Exception ignore) {}
        }

        return userVO;
    }
}