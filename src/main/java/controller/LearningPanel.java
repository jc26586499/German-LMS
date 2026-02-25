package controller;

import util.Tool;
import vo.UserVO;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.sql.*;

public class LearningPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final UserVO user;
    private final MainFrame mainFrame;

    private String currentCategory = "水果";
    private boolean wrongBookMode = false;

    private static final String CATEGORY_ALL_UNLOCKED = "__ALL_UNLOCKED__";

    private int currentWordId = -1;
    private String currentVocab = "";
    private String currentMeaning = "";
    private String currentImagePath = "";

    private JLabel lblTitle;
    private JLabel lblWord;

    private JComboBox<CategoryItem> cboCategory;
    private boolean ignoreCategoryEvent = false;

    private JTextArea txtAnswer;
    private JScrollPane answerScroll;

    private JButton btnHintText;
    private JButton btnShowImage;
    private JButton btnCheck;
    private JButton btnToggleWrongBook;

    // ✅ 需要控制背景色的容器
    private JPanel topPanel;
    private JPanel centerPanel;

    // ✅ 顏色
    private final Color BG_LEARNING = new Color(245, 247, 250);     // 原本淡灰
    private final Color BG_WRONGBOOK = new Color(255, 238, 238);    // 淡紅

    private final Color PURPLE = new Color(156, 136, 255);          // 淡紫主色
    private final Color PURPLE_DARK = new Color(130, 112, 230);     // hover/按下可用
    private final Color BTN_NEUTRAL = new Color(240, 242, 245);     // 淡灰白

    public LearningPanel(UserVO user, MainFrame mainFrame) {
        this.user = user;
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setBackground(BG_LEARNING);

        initUI();
        bindEvents();

        reloadCategoryDropdown();
        applyModeStyle();      // ✅ 一開始套用 Learning 模式樣式
        loadNextQuestion();
    }

    private void initUI() {
        // =========================
        // TOP
        // =========================
        topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(18, 18, 10, 18));
        topPanel.setOpaque(true);

        JPanel topLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        topLeft.setOpaque(false);

        lblTitle = new JLabel("Learning Mode");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitle.setForeground(new Color(45, 52, 54));

        cboCategory = new JComboBox<>();
        cboCategory.setPreferredSize(new Dimension(260, 34));

        topLeft.add(lblTitle);
        topLeft.add(cboCategory);

        topPanel.add(topLeft, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // =========================
        // CENTER
        // =========================
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(new EmptyBorder(40, 80, 40, 80));
        centerPanel.setOpaque(true);

        lblWord = new JLabel("...");
        lblWord.setFont(new Font("SansSerif", Font.BOLD, 54));
        lblWord.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtAnswer = new JTextArea();
        txtAnswer.setLineWrap(true);
        txtAnswer.setWrapStyleWord(true);
        txtAnswer.setFont(new Font("SansSerif", Font.PLAIN, 50));

        answerScroll = new JScrollPane(txtAnswer);
        answerScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        answerScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        int w = 760;
        answerScroll.setPreferredSize(new Dimension(w, 260));
        answerScroll.setMaximumSize(new Dimension(w, 320));
        answerScroll.setMinimumSize(new Dimension(w, 200));

        // 三顆按鈕
        JPanel btnRow = new JPanel(new GridLayout(1, 3, 14, 0));
        btnRow.setOpaque(false);
        btnRow.setPreferredSize(new Dimension(w, 54));
        btnRow.setMaximumSize(new Dimension(w, 54));

        btnHintText = new JButton("Hint");
        btnShowImage = new JButton("Show Image");
        btnCheck = new JButton("Check");

        styleNormalButton(btnHintText);
        styleNormalButton(btnShowImage);
        styleNormalButton(btnCheck);

        btnRow.add(btnHintText);
        btnRow.add(btnShowImage);
        btnRow.add(btnCheck);

        // ✅ Wrong Book 按鈕置中（在下方）
        JPanel wrongBookRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrongBookRow.setOpaque(false);

        btnToggleWrongBook = new JButton("Switch to Wrong Book");
        btnToggleWrongBook.setPreferredSize(new Dimension(260, 46));
        btnToggleWrongBook.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnToggleWrongBook.setFocusPainted(false);

        wrongBookRow.add(btnToggleWrongBook);

        // 組裝
        centerPanel.add(lblWord);
        centerPanel.add(Box.createVerticalStrut(26));
        centerPanel.add(answerScroll);
        centerPanel.add(Box.createVerticalStrut(18));
        centerPanel.add(btnRow);

        centerPanel.add(Box.createVerticalStrut(40)); // 你要對齊 sidebar logout 的視覺高度
        centerPanel.add(wrongBookRow);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void bindEvents() {
        cboCategory.addActionListener(e -> {
            if (ignoreCategoryEvent) return;

            // ✅ Wrong Book 模式禁止切 category
            if (wrongBookMode) return;

            CategoryItem item = (CategoryItem) cboCategory.getSelectedItem();
            if (item == null) return;

            setCategoryInternal(item.value);
        });

        btnHintText.addActionListener(e -> showTextHint());
        btnShowImage.addActionListener(e -> showHintImage());
        btnCheck.addActionListener(e -> checkAnswer());

        btnToggleWrongBook.addActionListener(e -> toggleWrongBook());

        // Ctrl+Enter = Check
        txtAnswer.getInputMap().put(KeyStroke.getKeyStroke("ctrl ENTER"), "CHECK");
        txtAnswer.getActionMap().put("CHECK", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                checkAnswer();
            }
        });
    }

    // ========= 外部給 Shop Enter 用 =========
    public void setCategory(String category) {
        if (wrongBookMode) return; // Wrong Book 模式不接受外部切換

        // ✅ 關鍵：先刷新下拉，讓新買的分類出現
        reloadCategoryDropdown();

        // category 若為 null/空 -> 預設 All Unlocked
        if (category == null || category.trim().isEmpty()) {
            setCategoryInternal(CATEGORY_ALL_UNLOCKED);
            selectDropdownCategory(CATEGORY_ALL_UNLOCKED);
            return;
        }

        setCategoryInternal(category.trim());
        selectDropdownCategory(category.trim());
    }

    private void setCategoryInternal(String categoryValue) {
        this.currentCategory = categoryValue;
        lblTitle.setText("Learning Mode");
        loadNextQuestion();
        txtAnswer.requestFocusInWindow();
    }

    private void toggleWrongBook() {
        wrongBookMode = !wrongBookMode;

        if (wrongBookMode) {
            lblTitle.setText("Wrong Book - (wrong_count >= 3)");
            btnToggleWrongBook.setText("Back to Learning");

            cboCategory.setEnabled(false);
            cboCategory.setToolTipText("Wrong Book mode: category switch disabled");
        } else {
            lblTitle.setText("Learning Mode");
            btnToggleWrongBook.setText("Switch to Wrong Book");

            cboCategory.setEnabled(true);
            cboCategory.setToolTipText(null);
        }

        applyModeStyle();   // ✅ 套用背景/按鈕色
        loadNextQuestion();
        txtAnswer.requestFocusInWindow();
    }

    // ✅ 這裡統一控制：背景變淡紅 + wrongbook 按鈕變淡紫
    private void applyModeStyle() {
        if (wrongBookMode) {
            setBackground(BG_WRONGBOOK);
            topPanel.setBackground(BG_WRONGBOOK);
            centerPanel.setBackground(BG_WRONGBOOK);

            stylePurpleButton(btnToggleWrongBook);
        } else {
            setBackground(BG_LEARNING);
            topPanel.setBackground(BG_LEARNING);
            centerPanel.setBackground(BG_LEARNING);

            styleNeutralButton(btnToggleWrongBook);
        }

        revalidate();
        repaint();
    }

    // ========= Dropdown =========

    private void reloadCategoryDropdown() {
        ignoreCategoryEvent = true;
        try {
            cboCategory.removeAllItems();

            cboCategory.addItem(new CategoryItem("All Unlocked", CATEGORY_ALL_UNLOCKED));
            cboCategory.addItem(new CategoryItem("水果", "水果"));

            try (Connection conn = Tool.getDb()) {
                if (conn != null) {
                    String sql = "SELECT DISTINCT TRIM(category_purchased) AS c FROM user_permissions WHERE user_id=? ORDER BY c";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setInt(1, user.getUserId());
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                String c = rs.getString("c");
                                if (c == null) continue;
                                c = c.trim();
                                if (c.isEmpty()) continue;
                                if ("水果".equals(c)) continue;
                                cboCategory.addItem(new CategoryItem(c, c));
                            }
                        }
                    }
                }
            }

            selectDropdownCategory(currentCategory);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            ignoreCategoryEvent = false;
        }
    }

    private void selectDropdownCategory(String categoryValue) {
        ignoreCategoryEvent = true;
        try {
            ComboBoxModel<CategoryItem> m = cboCategory.getModel();
            for (int i = 0; i < m.getSize(); i++) {
                CategoryItem item = m.getElementAt(i);
                if (item != null && item.value.equals(categoryValue)) {
                    cboCategory.setSelectedIndex(i);
                    return;
                }
            }
            cboCategory.setSelectedIndex(0);
        } finally {
            ignoreCategoryEvent = false;
        }
    }

    // ========= 問題載入 =========

    private void loadNextQuestion() {
        txtAnswer.setText("");

        try (Connection conn = Tool.getDb()) {
            if (conn == null) throw new Exception("DB connection failed.");

            WordRow row;
            if (!wrongBookMode) row = pickRandomWordLearningMode(conn);
            else row = pickRandomWrongBookWord(conn, user.getUserId());

            if (row == null) {
                setNoQuestion(wrongBookMode ? "No wrong words (>=3) yet" : "No words");
                return;
            }

            currentWordId = row.wordId;
            currentVocab = row.vocab;
            currentMeaning = row.meaning;
            currentImagePath = row.imagePath == null ? "" : row.imagePath.trim();

            lblWord.setText(currentVocab);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage() == null ? ex.toString() : ex.getMessage(),
                    "Load Question Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private WordRow pickRandomWordLearningMode(Connection conn) throws Exception {
        if (CATEGORY_ALL_UNLOCKED.equals(currentCategory)) {
            String sql =
                    "SELECT w.word_id, w.vocabulary, w.meaning, w.image_path " +
                    "FROM words w " +
                    "LEFT JOIN user_permissions p ON (p.user_id=? AND TRIM(p.category_purchased)=TRIM(w.category)) " +
                    "WHERE (TRIM(w.category)='水果' OR p.permission_id IS NOT NULL) " +
                    "ORDER BY RAND() LIMIT 1";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, user.getUserId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return null;
                    return new WordRow(
                            rs.getInt("word_id"),
                            rs.getString("vocabulary"),
                            rs.getString("meaning"),
                            rs.getString("image_path")
                    );
                }
            }
        } else {
            if (!isCategoryUnlocked(conn, user.getUserId(), currentCategory)) {
                setNoQuestion("LOCKED");
                return null;
            }
            return pickRandomWordByCategory(conn, currentCategory);
        }
    }

    private void setNoQuestion(String msg) {
        currentWordId = -1;
        currentVocab = "";
        currentMeaning = "";
        currentImagePath = "";
        lblWord.setText(msg);
    }

    // ========= Hint / Image / Check =========

    private void showTextHint() {
        if (currentWordId == -1) return;
        String hint = buildTextHint(currentMeaning);
        JOptionPane.showMessageDialog(this, "提示：" + hint, "Hint", JOptionPane.INFORMATION_MESSAGE);
    }

    private String buildTextHint(String meaning) {
        if (meaning == null) return "___";
        String m = meaning.trim();
        if (m.isEmpty()) return "___";
        return m.substring(0, 1) + "___";
    }

    private void showHintImage() {
        if (currentWordId == -1) return;

        if (currentImagePath == null || currentImagePath.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No image for this word.");
            return;
        }

        ImageIcon icon = loadImageIconFromResources(currentImagePath);
        if (icon == null) {
            JOptionPane.showMessageDialog(this,
                    "Image not found: " + currentImagePath + "\n請確認放在 src/main/resources/images/",
                    "Show Image",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Image scaled = icon.getImage().getScaledInstance(260, 260, Image.SCALE_SMOOTH);
        JLabel imgLabel = new JLabel(new ImageIcon(scaled));
        imgLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JOptionPane.showMessageDialog(this, imgLabel, "Image - " + currentVocab, JOptionPane.PLAIN_MESSAGE);
    }

    private ImageIcon loadImageIconFromResources(String fileName) {
        // 1. 定義可能的路徑清單 (對應你 7-Zip 看到的結構)
        String[] paths = {
            "/resources/images/" + fileName.trim(),
            "/images/" + fileName.trim()
        };

        // 2. 迴圈嘗試搜尋路徑
        for (String path : paths) {
            try (InputStream is = getClass().getResourceAsStream(path)) {
                if (is != null) {
                    BufferedImage bi = ImageIO.read(is);
                    if (bi != null) {
                        System.out.println("成功載入圖片：" + path); // 測試用，可刪除
                        return new ImageIcon(bi);
                    }
                }
            } catch (Exception e) {
                // 繼續嘗試下一個路徑
            }
        }

        // 3. 全部找不到才回傳 null
        return null;
    }

    private void checkAnswer() {
        if (currentWordId == -1) return;

        String ans = txtAnswer.getText().trim();
        boolean correct = ans.equalsIgnoreCase(currentMeaning);

        try (Connection conn = Tool.getDb()) {
            if (conn == null) throw new Exception("DB connection failed.");
            upsertStats(conn, user.getUserId(), currentWordId, correct);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage() == null ? ex.toString() : ex.getMessage(),
                    "Update Stats Failed",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (correct) JOptionPane.showMessageDialog(this, "Correct ✅");
        else JOptionPane.showMessageDialog(this, "Wrong ❌  正解：" + currentMeaning);

        loadNextQuestion();
        txtAnswer.requestFocusInWindow();
    }

    private void upsertStats(Connection conn, int userId, int wordId, boolean correct) throws Exception {
        String q = "SELECT stat_id, correct_count, wrong_count FROM user_word_stats WHERE user_id=? AND word_id=? LIMIT 1";
        Integer statId = null;
        int c = 0, w = 0;

        try (PreparedStatement ps = conn.prepareStatement(q)) {
            ps.setInt(1, userId);
            ps.setInt(2, wordId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    statId = rs.getInt("stat_id");
                    c = rs.getInt("correct_count");
                    w = rs.getInt("wrong_count");
                }
            }
        }

        if (statId == null) {
            String ins = "INSERT INTO user_word_stats (user_id, word_id, correct_count, wrong_count, last_practiced) VALUES (?,?,?,?,NOW())";
            try (PreparedStatement ps = conn.prepareStatement(ins)) {
                ps.setInt(1, userId);
                ps.setInt(2, wordId);
                ps.setInt(3, correct ? 1 : 0);
                ps.setInt(4, correct ? 0 : 1);
                ps.executeUpdate();
            }
        } else {
            int newC = c + (correct ? 1 : 0);
            int newW = w + (correct ? 0 : 1);

            String upd = "UPDATE user_word_stats SET correct_count=?, wrong_count=?, last_practiced=NOW() WHERE stat_id=?";
            try (PreparedStatement ps = conn.prepareStatement(upd)) {
                ps.setInt(1, newC);
                ps.setInt(2, newW);
                ps.setInt(3, statId);
                ps.executeUpdate();
            }
        }
    }

    // ========= DB helpers =========

    private boolean isCategoryUnlocked(Connection conn, int userId, String category) throws Exception {
        if ("水果".equals(category)) return true;
        String sql = "SELECT 1 FROM user_permissions WHERE user_id=? AND TRIM(category_purchased)=? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, category.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private WordRow pickRandomWordByCategory(Connection conn, String category) throws Exception {
        String sql = "SELECT word_id, vocabulary, meaning, image_path FROM words WHERE category=? ORDER BY RAND() LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new WordRow(
                        rs.getInt("word_id"),
                        rs.getString("vocabulary"),
                        rs.getString("meaning"),
                        rs.getString("image_path")
                );
            }
        }
    }

    private WordRow pickRandomWrongBookWord(Connection conn, int userId) throws Exception {
        String sql =
                "SELECT w.word_id, w.vocabulary, w.meaning, w.image_path " +
                "FROM user_word_stats s " +
                "JOIN words w ON s.word_id = w.word_id " +
                "LEFT JOIN user_permissions p ON (p.user_id = s.user_id AND TRIM(p.category_purchased)=TRIM(w.category)) " +
                "WHERE s.user_id=? AND s.wrong_count>=3 " +
                "AND (TRIM(w.category)='水果' OR p.permission_id IS NOT NULL) " +
                "ORDER BY RAND() LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new WordRow(
                        rs.getInt("word_id"),
                        rs.getString("vocabulary"),
                        rs.getString("meaning"),
                        rs.getString("image_path")
                );
            }
        }
    }

    // ========= Button Style Helpers =========

    private void styleNormalButton(JButton b) {
        b.setFont(new Font("SansSerif", Font.PLAIN, 14));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(10, 54));
        b.setBackground(Color.WHITE);
    }

    private void stylePurpleButton(JButton b) {
        b.setOpaque(true);
        b.setBackground(PURPLE);
        b.setForeground(Color.WHITE);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleNeutralButton(JButton b) {
        b.setOpaque(true);
        b.setBackground(BTN_NEUTRAL);
        b.setForeground(new Color(60, 60, 60));
        b.setBorderPainted(true);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // ========= Inner Classes =========

    private static class WordRow {
        int wordId;
        String vocab;
        String meaning;
        String imagePath;

        WordRow(int wordId, String vocab, String meaning, String imagePath) {
            this.wordId = wordId;
            this.vocab = vocab;
            this.meaning = meaning;
            this.imagePath = imagePath;
        }
    }

    private static class CategoryItem {
        String label;
        String value;

        CategoryItem(String label, String value) {
            this.label = label;
            this.value = value;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}