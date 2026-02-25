package service;

import vo.UserVO;

public interface UserService {

    // 登入：成功回傳 UserVO（含 unlockedCategories）
    UserVO login(String username, String password) throws Exception;

    // 註冊：預設 balance=200、role=student；重複帳號要丟 Exception 讓 UI 跳視窗
    UserVO register(String username, String password, String confirmPassword) throws Exception;

    // 權限檢查（水果免費，其它要在 unlockedCategories）
    boolean hasPermission(UserVO user, String category);
}