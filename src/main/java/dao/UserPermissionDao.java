package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UserPermissionDao {

    /**
     * 檢查使用者是否已購買/解鎖某 category
     */
    boolean hasCategory(int userId, String category) throws SQLException;

    /**
     * 取得使用者已購買/解鎖的所有 category（用於 UI 顯示、快取）
     */
    List<String> findCategoriesByUser(int userId) throws SQLException;

    /**
     * 新增一筆購買紀錄（解鎖）
     * 注意：是否允許重複，建議 DB 加 UNIQUE(user_id, category_purchased)
     */
    boolean insert(int userId, String category) throws SQLException;
    
    boolean hasCategory(Connection conn, int userId, String category) throws SQLException;

    boolean insert(Connection conn, int userId, String category) throws SQLException;
}