package service;

import java.sql.SQLException;
import java.util.List;
import model.Word;

public interface WordService {

    List<String> listCategories() throws SQLException;

    List<Word> listWordsByCategory(String category) throws SQLException;

    List<Word> buildQuiz(String category, int questionCount) throws SQLException;

    Word getWord(int wordId) throws SQLException;
}