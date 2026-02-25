package vo;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 題本用 VO
 * JOIN：user_word_stats + words
 */
public class BossWordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private int wordId;
    private String vocabulary;
    private String meaning;
    private String category;
    private String imagePath;

    private int wrongCount;
    private int correctCount;
    private Timestamp lastPracticed;

    public BossWordVO() {}

    public int getWordId() { return wordId; }
    public void setWordId(int wordId) { this.wordId = wordId; }

    public String getVocabulary() { return vocabulary; }
    public void setVocabulary(String vocabulary) { this.vocabulary = vocabulary; }

    public String getMeaning() { return meaning; }
    public void setMeaning(String meaning) { this.meaning = meaning; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public int getWrongCount() { return wrongCount; }
    public void setWrongCount(int wrongCount) { this.wrongCount = wrongCount; }

    public int getCorrectCount() { return correctCount; }
    public void setCorrectCount(int correctCount) { this.correctCount = correctCount; }

    public Timestamp getLastPracticed() { return lastPracticed; }
    public void setLastPracticed(Timestamp lastPracticed) { this.lastPracticed = lastPracticed; }

    @Override
    public String toString() {
        return "BossWordVO{" +
                "wordId=" + wordId +
                ", vocabulary='" + vocabulary + '\'' +
                ", meaning='" + meaning + '\'' +
                ", category='" + category + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", wrongCount=" + wrongCount +
                ", correctCount=" + correctCount +
                ", lastPracticed=" + lastPracticed +
                '}';
    }
}