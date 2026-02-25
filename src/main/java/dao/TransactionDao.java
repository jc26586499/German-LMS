package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import vo.TransactionVO;

public interface TransactionDao {

    /**
     * 新增交易紀錄（amount 可正可負）
     */
    boolean insert(Connection conn, int userId, int amount, String description) throws SQLException;

    /**
     * 查某使用者交易紀錄（用 view_transaction_history）
     */
    List<TransactionVO> findHistoryByUsername(String username) throws SQLException;

    /**
     * 查某使用者交易紀錄（直接用 transactions）
     */
    List<TransactionVO> findHistoryByUserId(int userId) throws SQLException;
}