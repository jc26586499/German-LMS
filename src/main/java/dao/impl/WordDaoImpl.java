package dao.impl;

import dao.WordDao;
import model.Word;
import util.Tool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WordDaoImpl implements WordDao {

    private Connection requireConn() throws SQLException {
        Connection conn = Tool.getDb();
        if (conn == null) throw new SQLException("DB connection failed (Tool.getDb() returned null).");
        return conn;
    }

    private Word map(ResultSet rs) throws SQLException {
        Word w = new Word();
        w.setWordId(rs.getInt("word_id"));
        w.setVocabulary(rs.getString("vocabulary"));
        w.setMeaning(rs.getString("meaning"));
        w.setCategory(rs.getString("category"));
        w.setImagePath(rs.getString("image_path"));
        return w;
    }

    @Override
    public List<String> findAllCategories() throws SQLException {
        String sql = "SELECT DISTINCT category FROM words ORDER BY category";

        Connection conn = requireConn();
        List<String> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String c = rs.getString("category");
                if (c != null && !c.trim().isEmpty()) list.add(c.trim());
            }

        } finally {
            try { conn.close(); } catch (Exception ignore) {}
        }

        return list;
    }

    @Override
    public List<Word> findByCategory(String category) throws SQLException {
        String sql =
                "SELECT word_id, vocabulary, meaning, category, image_path " +
                "FROM words WHERE category=? " +
                "ORDER BY word_id";

        Connection conn = requireConn();
        List<Word> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } finally {
            try { conn.close(); } catch (Exception ignore) {}
        }

        return list;
    }

    @Override
    public List<Word> findRandomByCategory(String category, int limit) throws SQLException {
        if (limit <= 0) limit = 10;

        String sql =
                "SELECT word_id, vocabulary, meaning, category, image_path " +
                "FROM words WHERE category=? " +
                "ORDER BY RAND() LIMIT ?";

        Connection conn = requireConn();
        List<Word> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category);
            ps.setInt(2, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } finally {
            try { conn.close(); } catch (Exception ignore) {}
        }

        return list;
    }

    @Override
    public Word findById(int wordId) throws SQLException {
        String sql =
                "SELECT word_id, vocabulary, meaning, category, image_path " +
                "FROM words WHERE word_id=?";

        Connection conn = requireConn();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, wordId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }
        } finally {
            try { conn.close(); } catch (Exception ignore) {}
        }
    }
}