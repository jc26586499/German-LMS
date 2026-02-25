package dao.impl;

import dao.UserWordStatsDao;
import model.UserWordStats;
import util.Tool;
import vo.BossWordVO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserWordStatsDaoImpl implements UserWordStatsDao {

    private Connection requireConn() throws SQLException {
        Connection conn = Tool.getDb();
        if (conn == null) throw new SQLException("DB connection failed (Tool.getDb() returned null).");
        return conn;
    }

    @Override
    public void ensureExists(int userId, int wordId) throws SQLException {
        String sql =
                "INSERT INTO user_word_stats (user_id, word_id, correct_count, wrong_count, last_practiced) " +
                "VALUES (?, ?, 0, 0, NOW()) " +
                "ON DUPLICATE KEY UPDATE last_practiced = last_practiced";

        Connection conn = requireConn();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, wordId);
            ps.executeUpdate();
        } finally {
            try { conn.close(); } catch (Exception ignore) {}
        }
    }

    @Override
    public void addCorrect(int userId, int wordId) throws SQLException {
        String sql =
                "UPDATE user_word_stats " +
                "SET correct_count = correct_count + 1, last_practiced = NOW() " +
                "WHERE user_id = ? AND word_id = ?";

        Connection conn = requireConn();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, wordId);

            int updated = ps.executeUpdate();
            if (updated == 0) {
                // 保險：先補再更
                conn.close();
                ensureExists(userId, wordId);
                addCorrect(userId, wordId);
            }
        } finally {
            try { conn.close(); } catch (Exception ignore) {}
        }
    }

    @Override
    public void addWrong(int userId, int wordId) throws SQLException {
        String sql =
                "UPDATE user_word_stats " +
                "SET wrong_count = wrong_count + 1, last_practiced = NOW() " +
                "WHERE user_id = ? AND word_id = ?";

        Connection conn = requireConn();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, wordId);

            int updated = ps.executeUpdate();
            if (updated == 0) {
                conn.close();
                ensureExists(userId, wordId);
                addWrong(userId, wordId);
            }
        } finally {
            try { conn.close(); } catch (Exception ignore) {}
        }
    }

    @Override
    public UserWordStats findOne(int userId, int wordId) throws SQLException {
        String sql =
                "SELECT stat_id, user_id, word_id, correct_count, wrong_count, last_practiced " +
                "FROM user_word_stats WHERE user_id=? AND word_id=?";

        Connection conn = requireConn();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, wordId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserWordStats s = new UserWordStats();
                    s.setStatId(rs.getInt("stat_id"));
                    s.setUserId(rs.getInt("user_id"));
                    s.setWordId(rs.getInt("word_id"));
                    s.setCorrectCount(rs.getInt("correct_count"));
                    s.setWrongCount(rs.getInt("wrong_count"));
                    s.setLastPracticed(rs.getTimestamp("last_practiced"));
                    return s;
                }
                return null;
            }
        } finally {
            try { conn.close(); } catch (Exception ignore) {}
        }
    }

    @Override
    public List<BossWordVO> findBossWords(int userId, int threshold) throws SQLException {
        String sql =
                "SELECT w.word_id, w.vocabulary, w.meaning, w.category, w.image_path, " +
                "       s.wrong_count, s.correct_count, s.last_practiced " +
                "FROM user_word_stats s " +
                "JOIN words w ON s.word_id = w.word_id " +
                "WHERE s.user_id = ? AND s.wrong_count >= ? " +
                "ORDER BY s.wrong_count DESC, s.last_practiced DESC";

        Connection conn = requireConn();
        List<BossWordVO> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, threshold);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BossWordVO vo = new BossWordVO();
                    vo.setWordId(rs.getInt("word_id"));
                    vo.setVocabulary(rs.getString("vocabulary"));
                    vo.setMeaning(rs.getString("meaning"));
                    vo.setCategory(rs.getString("category"));
                    vo.setImagePath(rs.getString("image_path"));
                    vo.setWrongCount(rs.getInt("wrong_count"));
                    vo.setCorrectCount(rs.getInt("correct_count"));
                    vo.setLastPracticed(rs.getTimestamp("last_practiced"));
                    list.add(vo);
                }
            }
        } finally {
            try { conn.close(); } catch (Exception ignore) {}
        }

        return list;
    }
}