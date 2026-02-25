package dao;

import java.sql.SQLException;
import java.util.List;
import model.UserWordStats;
import vo.BossWordVO;

public interface UserWordStatsDao {

    /**
     * 若 user_word_stats 沒有該 user_id + word_id，先補一筆（避免 update 0 rows）
     */
    void ensureExists(int userId, int wordId) throws SQLException;

    /**
     * 正確次數 +1
     */
    void addCorrect(int userId, int wordId) throws SQLException;

    /**
     * 錯誤次數 +1
     */
    void addWrong(int userId, int wordId) throws SQLException;

    /**
     * 查某單字統計
     */
    UserWordStats findOne(int userId, int wordId) throws SQLException;

    /**
     * 題本：wrong_count >= threshold
     */
    List<BossWordVO> findBossWords(int userId, int threshold) throws SQLException;
}