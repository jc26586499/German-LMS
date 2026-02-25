package service;

import java.util.List;
import vo.TransactionVO;

public interface EconomyService {

    /**
     * 模擬加值（例如 +500）
     */
    int topUp(int userId, int amount) throws Exception;

    /**
     * 購買解鎖 category（會扣點、寫 transactions、寫 user_permissions）
     */
    int purchaseCategory(int userId, String category, int price) throws Exception;

    /**
     * 查交易紀錄（for JTable）
     */
    List<TransactionVO> getTransactionHistoryByUserId(int userId) throws Exception;
}