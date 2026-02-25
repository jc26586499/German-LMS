package service;

public interface HintService {

    /**
     * 回傳圖片檔案的相對路徑（供 UI 讀取）
     * 例如：res/images/fruit_01.jpg
     */
    String getHintImagePath(int wordId) throws Exception;
}