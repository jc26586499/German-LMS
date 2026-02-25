package model;

import java.io.Serializable;

/**
 * 對應資料表：words
 * 欄位：word_id, vocabulary, meaning, category, image_path
 *
 * 圖片讀取建議（UI）：
 * 使用 "res/images/" + word.getImagePath()
 */
public class Word implements Serializable {

    private static final long serialVersionUID = 1L;

    private int wordId;
    private String vocabulary;
    private String meaning;
    private String category;
    private String imagePath;

    public Word() {}

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }

    public String getVocabulary() {
        return vocabulary;
    }

    public void setVocabulary(String vocabulary) {
        this.vocabulary = vocabulary;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "Word{" +
                "wordId=" + wordId +
                ", vocabulary='" + vocabulary + '\'' +
                ", meaning='" + meaning + '\'' +
                ", category='" + category + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}