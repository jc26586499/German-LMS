package controller;

import util.Tool;
import vo.UserVO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import util.ExcelUtil;

public class AdminPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final UserVO user;

    private JTable tblWords;
    private DefaultTableModel model;

    private JButton btnReload;
    private JButton btnSave;

    // 用來比對是否有修改：word_id -> 原始資料
    private final Map<Integer, RowSnapshot> original = new HashMap<>();

    public AdminPanel(UserVO user) {
        this.user = user;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        initUI();

        if (!isAdmin()) {
            showNoPermission();
            return;
        }

        reloadWords();
    }

    private boolean isAdmin() {
        if (user == null || user.getRole() == null) return false;
        return "admin".equalsIgnoreCase(user.getRole());
    }

    private void initUI() {
        JLabel title = new JLabel("Admin - Words Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(45, 52, 54));
        title.setBorder(new EmptyBorder(18, 18, 10, 18));
        add(title, BorderLayout.NORTH);

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topRow.setOpaque(false);
        topRow.setBorder(new EmptyBorder(0, 18, 10, 18));

        btnReload = new JButton("Reload");
        btnReload.addActionListener(e -> reloadWords());

        btnSave = new JButton("Save");
        btnSave.addActionListener(e -> saveChanges());

        JButton btnExport = new JButton("Export Excel");
        btnExport.addActionListener(e ->
                ExcelUtil.exportTableToExcel(tblWords, "words_export"));
        
        topRow.add(btnReload);
        topRow.add(btnSave);
        topRow.add(btnExport);
        
        add(topRow, BorderLayout.SOUTH);

        model = new DefaultTableModel(
                new Object[]{"word_id", "vocabulary", "meaning", "category", "image_path"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // word_id 不可改，其餘可 inline edit
                return column != 0;
            }
        };

        tblWords = new JTable(model);
        tblWords.setRowHeight(30);
        tblWords.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tblWords.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

        JScrollPane sp = new JScrollPane(tblWords);
        sp.setBorder(new EmptyBorder(0, 18, 18, 18));
        add(sp, BorderLayout.CENTER);
    }

    private void showNoPermission() {
        removeAll();

        JLabel msg = new JLabel("No Permission: Admin Only", SwingConstants.CENTER);
        msg.setFont(new Font("SansSerif", Font.BOLD, 18));
        msg.setForeground(new Color(180, 60, 60));

        add(msg, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private void reloadWords() {
        if (!isAdmin()) return;

        model.setRowCount(0);
        original.clear();

        try {
            Connection conn = Tool.getDb();
            if (conn == null) throw new Exception("DB connection failed.");

            try {
                String sql = "SELECT word_id, vocabulary, meaning, category, image_path FROM words ORDER BY word_id";
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    int wordId = rs.getInt("word_id");
                    String vocabulary = rs.getString("vocabulary");
                    String meaning = rs.getString("meaning");
                    String category = rs.getString("category");
                    String imagePath = rs.getString("image_path");

                    model.addRow(new Object[]{wordId, vocabulary, meaning, category, imagePath});
                    original.put(wordId, new RowSnapshot(vocabulary, meaning, category, imagePath));
                }

                rs.close();
                ps.close();

            } finally {
                try { conn.close(); } catch (Exception ignore) {}
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage() == null ? ex.toString() : ex.getMessage(),
                    "Reload Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveChanges() {
        if (!isAdmin()) return;

        // 先把 JTable 目前正在編輯的 cell commit 掉
        if (tblWords.isEditing()) {
            tblWords.getCellEditor().stopCellEditing();
        }

        int updated = 0;

        try {
            Connection conn = Tool.getDb();
            if (conn == null) throw new Exception("DB connection failed.");

            try {
                String sql = "UPDATE words SET vocabulary=?, meaning=?, category=?, image_path=? WHERE word_id=?";
                PreparedStatement ps = conn.prepareStatement(sql);

                for (int r = 0; r < model.getRowCount(); r++) {
                    int wordId = toInt(model.getValueAt(r, 0));
                    String vocabulary = toStr(model.getValueAt(r, 1));
                    String meaning = toStr(model.getValueAt(r, 2));
                    String category = toStr(model.getValueAt(r, 3));
                    String imagePath = toStr(model.getValueAt(r, 4));

                    RowSnapshot old = original.get(wordId);
                    if (old == null) {
                        // 理論上不會發生（除非你之後做新增列）
                        continue;
                    }

                    if (!old.equalsTo(vocabulary, meaning, category, imagePath)) {
                        ps.setString(1, vocabulary);
                        ps.setString(2, meaning);
                        ps.setString(3, category);
                        ps.setString(4, imagePath);
                        ps.setInt(5, wordId);

                        int rows = ps.executeUpdate();
                        if (rows == 1) {
                            updated++;
                            // 更新 snapshot，避免連按 Save 重複更新
                            original.put(wordId, new RowSnapshot(vocabulary, meaning, category, imagePath));
                        }
                    }
                }

                ps.close();

            } finally {
                try { conn.close(); } catch (Exception ignore) {}
            }

            JOptionPane.showMessageDialog(this, "Save completed. Updated rows: " + updated);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage() == null ? ex.toString() : ex.getMessage(),
                    "Save Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private int toInt(Object v) {
        if (v == null) return 0;
        if (v instanceof Integer) return (Integer) v;
        return Integer.parseInt(v.toString().trim());
    }

    private String toStr(Object v) {
        return v == null ? "" : v.toString();
    }

    private static class RowSnapshot {
        String vocabulary;
        String meaning;
        String category;
        String imagePath;

        RowSnapshot(String vocabulary, String meaning, String category, String imagePath) {
            this.vocabulary = vocabulary == null ? "" : vocabulary;
            this.meaning = meaning == null ? "" : meaning;
            this.category = category == null ? "" : category;
            this.imagePath = imagePath == null ? "" : imagePath;
        }

        boolean equalsTo(String vocabulary, String meaning, String category, String imagePath) {
            String v = vocabulary == null ? "" : vocabulary;
            String m = meaning == null ? "" : meaning;
            String c = category == null ? "" : category;
            String i = imagePath == null ? "" : imagePath;
            return this.vocabulary.equals(v)
                    && this.meaning.equals(m)
                    && this.category.equals(c)
                    && this.imagePath.equals(i);
        }
    }
}