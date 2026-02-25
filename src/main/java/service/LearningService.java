package service;

import java.sql.SQLException;
import java.util.List;
import vo.BossWordVO;

public interface LearningService {

    /**
     * 提交答題結果：會更新 user_word_stats
     */
    void submitAnswer(int userId, int wordId, boolean correct) throws SQLException;

    /**
     * 取得題本：wrong_count >= threshold（例如 3）
     */
    List<BossWordVO> getBossBook(int userId, int threshold) throws SQLException;
}