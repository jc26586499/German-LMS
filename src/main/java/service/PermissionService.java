package service;

import java.sql.SQLException;

public interface PermissionService {

    /**
     * 是否允許存取某 category（含免費名單）
     */
    boolean canAccessCategory(int userId, String category) throws SQLException;
}