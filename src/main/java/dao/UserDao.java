package dao;

import model.User;
import vo.UserVO;

import java.sql.SQLException;

public interface UserDao {

    // 登入：查 users 表
    User login(String username, String password) throws SQLException;

    // 註冊：檢查重複 username
    boolean existsByUsername(String username) throws SQLException;

    // 註冊：新增使用者（預設 balance=200, role=student）
    // 回傳新增後的 user_id（失敗回傳 -1）
    int insertUserReturnId(String username, String password, int balance, String role) throws SQLException;

    // 查 view_user_status（聚合 unlocked_category）
    UserVO findUserStatusByUsername(String username) throws SQLException;
}