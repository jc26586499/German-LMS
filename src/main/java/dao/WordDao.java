package dao;

import java.sql.SQLException;
import java.util.List;
import model.Word;

public interface WordDao {

    /**
     * 查所有主題（category）
     */
    List<String> findAllCategories() throws SQLException;

    /**
     * 查某主題全部單字
     */
    List<Word> findByCategory(String category) throws SQLException;

    /**
     * 隨機抽題（測驗用），limit 建議 5~20
     */
    List<Word> findRandomByCategory(String category, int limit) throws SQLException;

    /**
     * 用 word_id 查單字
     */
    Word findById(int wordId) throws SQLException;
}