package service.impl;

import model.Word;
import service.HintService;
import service.WordService;
import service.impl.WordServiceImpl;

public class HintServiceImpl implements HintService {

    private final WordService wordService = new WordServiceImpl();

    @Override
    public String getHintImagePath(int wordId) throws Exception {
        Word w = wordService.getWord(wordId);
        if (w == null) throw new Exception("Word not found: wordId=" + wordId);

        // 路徑格式：res/images/ + image_path
        // UI 讀取圖片時應使用：
        // "res/images/" + word.getImagePath()
        return "res/images/" + w.getImagePath();
    }
}