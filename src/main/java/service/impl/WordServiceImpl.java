package service.impl;

import dao.WordDao;
import dao.impl.WordDaoImpl;
import model.Word;
import service.WordService;

import java.sql.SQLException;
import java.util.List;

public class WordServiceImpl implements WordService {

    private final WordDao wordDao = new WordDaoImpl();

    @Override
    public List<String> listCategories() throws SQLException {
        return wordDao.findAllCategories();
    }

    @Override
    public List<Word> listWordsByCategory(String category) throws SQLException {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("category cannot be empty");
        }
        return wordDao.findByCategory(category.trim());
    }

    @Override
    public List<Word> buildQuiz(String category, int questionCount) throws SQLException {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("category cannot be empty");
        }
        if (questionCount <= 0) questionCount = 10;
        return wordDao.findRandomByCategory(category.trim(), questionCount);
    }

    @Override
    public Word getWord(int wordId) throws SQLException {
        if (wordId <= 0) throw new IllegalArgumentException("wordId must be > 0");
        return wordDao.findById(wordId);
    }
}