package service.impl;

import dao.UserPermissionDao;
import dao.impl.UserPermissionDaoImpl;
import service.PermissionService;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PermissionServiceImpl implements PermissionService {

    private final UserPermissionDao permissionDao = new UserPermissionDaoImpl();

    /**
     * 免費主題（Demo：水果免費）
     * 可以改成多個：new HashSet<>(Arrays.asList("水果","..."))
     */
    private final Set<String> freeCategories =
            new HashSet<>(Arrays.asList("水果"));

    @Override
    public boolean canAccessCategory(int userId, String category) throws SQLException {

        if (category == null || category.trim().isEmpty()) return false;
        String c = category.trim();

        // 1) 免費主題直接放行
        if (freeCategories.contains(c)) return true;

        // 2) 其它主題查 user_permissions
        return permissionDao.hasCategory(userId, c);
    }
}