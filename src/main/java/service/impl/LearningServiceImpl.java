package service.impl;

import dao.UserWordStatsDao;
import dao.impl.UserWordStatsDaoImpl;
import service.LearningService;
import vo.BossWordVO;

import java.sql.SQLException;
import java.util.List;

public class LearningServiceImpl implements LearningService {

    private final UserWordStatsDao statsDao = new UserWordStatsDaoImpl();

    @Override
    public void submitAnswer(int userId, int wordId, boolean correct) throws SQLException {
        statsDao.ensureExists(userId, wordId);

        if (correct) {
            statsDao.addCorrect(userId, wordId);
        } else {
            statsDao.addWrong(userId, wordId);
        }
    }

    @Override
    public List<BossWordVO> getBossBook(int userId, int threshold) throws SQLException {
        return statsDao.findBossWords(userId, threshold);
    }
}