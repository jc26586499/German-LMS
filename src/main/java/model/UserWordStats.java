package model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 對應資料表：user_word_stats
 */
public class UserWordStats implements Serializable {

    private static final long serialVersionUID = 1L;

    private int statId;
    private int userId;
    private int wordId;
    private int correctCount;
    private int wrongCount;
    private Timestamp lastPracticed;

    public UserWordStats() {}

    public int getStatId() {
        return statId;
    }

    public void setStatId(int statId) {
        this.statId = statId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
    }

    public int getWrongCount() {
        return wrongCount;
    }

    public void setWrongCount(int wrongCount) {
        this.wrongCount = wrongCount;
    }

    public Timestamp getLastPracticed() {
        return lastPracticed;
    }

    public void setLastPracticed(Timestamp lastPracticed) {
        this.lastPracticed = lastPracticed;
    }

    @Override
    public String toString() {
        return "UserWordStats{" +
                "statId=" + statId +
                ", userId=" + userId +
                ", wordId=" + wordId +
                ", correctCount=" + correctCount +
                ", wrongCount=" + wrongCount +
                ", lastPracticed=" + lastPracticed +
                '}';
    }
}