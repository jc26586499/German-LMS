package service.impl;

import dao.UserDao;
import dao.impl.UserDaoImpl;
import model.User;
import service.UserService;
import vo.UserVO;

public class UserServiceImpl implements UserService {

    private final UserDao userDao = new UserDaoImpl();

    @Override
    public UserVO login(String username, String password) throws Exception {

        if (username == null || username.trim().isEmpty())
            throw new Exception("Username cannot be empty.");

        if (password == null || password.isEmpty())
            throw new Exception("Password cannot be empty.");

        User user = userDao.login(username.trim(), password);
        if (user == null) throw new Exception("Invalid username or password.");

        // 查 view_user_status（含解鎖清單）
        UserVO vo = userDao.findUserStatusByUsername(user.getUsername());
        if (vo == null) throw new Exception("Login success but user status not found.");

        return vo;
    }

    @Override
    public UserVO register(String username, String password, String confirmPassword) throws Exception {

        if (username == null || username.trim().isEmpty())
            throw new Exception("Username cannot be empty.");

        if (password == null || password.isEmpty())
            throw new Exception("Password cannot be empty.");

        if (!password.equals(confirmPassword))
            throw new Exception("Passwords do not match.");

        String u = username.trim();

        // 1) 重複帳號
        if (userDao.existsByUsername(u)) {
            throw new Exception("Username already exists.");
        }

        // 2) 新增（預設 200 點、student）
        int newId = userDao.insertUserReturnId(u, password, 200, "student");
        if (newId <= 0) throw new Exception("Create account failed.");

        // 3) 回查完整狀態（含 unlockedCategories）
        UserVO vo = userDao.findUserStatusByUsername(u);
        if (vo == null) throw new Exception("Account created but user status not found.");

        return vo;
    }

    @Override
    public boolean hasPermission(UserVO user, String category) {
        if (user == null) return false;

        // 你現在規則：水果免費
        if ("水果".equals(category)) return true;

        return user.getUnlockedCategories() != null && user.getUnlockedCategories().contains(category);
    }
}