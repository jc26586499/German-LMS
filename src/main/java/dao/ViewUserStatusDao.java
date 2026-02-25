package dao;

import java.sql.SQLException;
import vo.UserVO;

public interface ViewUserStatusDao {

    /**
     * 查詢使用者完整狀態（含解鎖清單）
     */
    UserVO findByUserId(int userId) throws SQLException;
}