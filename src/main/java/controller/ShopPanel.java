package controller;

import util.Tool;
import vo.UserVO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ShopPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int PRICE_PER_CATEGORY = 200;

    private final UserVO user;
    private final MainFrame mainFrame;

    private JTable table;
    private DefaultTableModel model;

    public ShopPanel(UserVO user, MainFrame mainFrame) {
        this.user = user;
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        initUI();
        reloadTable();
    }

    private void initUI() {
        JLabel title = new JLabel("Shop - Unlock Categories");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(45, 52, 54));
        title.setBorder(BorderFactory.createEmptyBorder(18, 18, 10, 18));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"Category", "Total Words", "Status", "Price", "Action"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        table = new JTable(model);
        table.setRowHeight(38);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

        table.getColumn("Action").setCellRenderer(new ButtonRenderer());
        table.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder(0, 18, 18, 18));
        add(sp, BorderLayout.CENTER);
    }

    /** ✅ 重新載入：Status/Action 只用同一個 unlocked 判斷 */
    private void reloadTable() {
        model.setRowCount(0);

        try (Connection conn = Tool.getDb()) {
            if (conn == null) throw new SQLException("DB connection failed.");

            // 用你有的 view_category_stats：category + total_words
            String sql = "SELECT category, total_words FROM view_category_stats ORDER BY category";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    String category = rs.getString("category");
                    int totalWords = rs.getInt("total_words");

                    boolean isFree = "水果".equals(category);

                    // ✅ unlocked 判斷：水果免費 or DB permission 存在
                    boolean unlocked = isFree || hasPermission(conn, user.getUserId(), category);

                    String status;
                    if (isFree) status = "FREE";
                    else status = unlocked ? "UNLOCKED" : "LOCKED";

                    int price = unlocked ? 0 : PRICE_PER_CATEGORY;

                    // ✅ Action 一定跟 unlocked 同步
                    String action = unlocked ? "Enter" : "Buy";

                    model.addRow(new Object[]{category, totalWords, status, price, action});
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage() == null ? ex.toString() : ex.getMessage(),
                    "Load Shop Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /** 檢查 user_permissions 是否已解鎖（用 DB 當準） */
    private boolean hasPermission(Connection conn, int userId, String category) throws SQLException {
        String sql = "SELECT 1 FROM user_permissions WHERE user_id=? AND category_purchased=? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, category.trim()); // ✅ 防止資料有空白
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** 購買：扣點 + permissions + transactions */
    private void purchaseCategory(String category) throws Exception {
        if ("水果".equals(category)) {
            JOptionPane.showMessageDialog(this, "This is a free starter pack. Enjoy!");
            return;
        }

        Connection conn = Tool.getDb();
        if (conn == null) throw new SQLException("DB connection failed.");

        try {
            int balance = getBalanceFromDb(conn, user.getUserId());
            if (balance < PRICE_PER_CATEGORY) {
                throw new Exception("Insufficient balance\n（You need " + PRICE_PER_CATEGORY + " points）");
            }

            if (hasPermission(conn, user.getUserId(), category)) {
                throw new Exception("Already Unlocked");
            }

            int newBalance = balance - PRICE_PER_CATEGORY;
            updateBalance(conn, user.getUserId(), newBalance);
            insertPermission(conn, user.getUserId(), category.trim());
            insertTransaction(conn, user.getUserId(), -PRICE_PER_CATEGORY, "Purchase category: " + category);

            user.setBalance(newBalance);
            mainFrame.refreshBalance(newBalance);

            JOptionPane.showMessageDialog(this, "Purchase successful! Unlocked：" + category);

        } finally {
            try { conn.close(); } catch (Exception ignore) {}
        }
    }

    private int getBalanceFromDb(Connection conn, int userId) throws SQLException {
        String sql = "SELECT balance FROM users WHERE user_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("User not found: user_id=" + userId);
                return rs.getInt("balance");
            }
        }
    }

    private void updateBalance(Connection conn, int userId, int newBalance) throws SQLException {
        String sql = "UPDATE users SET balance=? WHERE user_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newBalance);
            ps.setInt(2, userId);
            int rows = ps.executeUpdate();
            if (rows != 1) throw new SQLException("Update balance failed.");
        }
    }

    private void insertPermission(Connection conn, int userId, String category) throws SQLException {
        String sql = "INSERT INTO user_permissions (user_id, category_purchased) VALUES (?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, category);
            ps.executeUpdate();
        }
    }

    private void insertTransaction(Connection conn, int userId, int amount, String desc) throws SQLException {
        String sql = "INSERT INTO transactions (user_id, amount, description) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, amount);
            ps.setString(3, desc);
            ps.executeUpdate();
        }
    }

    // =========================
    // JTable Button
    // =========================

    class ButtonRenderer extends JButton implements TableCellRenderer {
        private static final long serialVersionUID = 1L;

        public ButtonRenderer() {
            setOpaque(true);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            String text = String.valueOf(value);
            setText(text);

            if ("Buy".equals(text)) setBackground(new Color(47, 128, 237));
            else setBackground(new Color(39, 174, 96));

            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private static final long serialVersionUID = 1L;

        private JButton button;
        private String label;
        private boolean clicked;
        private int rowIndex;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setForeground(Color.WHITE);

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column) {

            label = String.valueOf(value);
            rowIndex = row;
            clicked = true;

            button.setText(label);
            if ("Buy".equals(label)) button.setBackground(new Color(47, 128, 237));
            else button.setBackground(new Color(39, 174, 96));

            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                handleAction(rowIndex);
            }
            clicked = false;
            return label;
        }

        private void handleAction(int row) {
            String category = String.valueOf(model.getValueAt(row, 0));
            String action = String.valueOf(model.getValueAt(row, 4));

            try {
                if ("Buy".equals(action)) {
                    purchaseCategory(category);

                    SwingUtilities.invokeLater(() -> {
                        reloadTable();
                        table.revalidate();
                        table.repaint();
                    });

                } else {
                    // ✅ 真正跳轉到 LearningPanel + 指定題庫
                    mainFrame.openLearningCategory(category);
                }
            } catch (Throwable t) {
                t.printStackTrace();
                String msg = t.getMessage();
                if (msg == null || msg.trim().isEmpty()) msg = t.toString();
                JOptionPane.showMessageDialog(ShopPanel.this, msg, "Action Failed", JOptionPane.ERROR_MESSAGE);
            }
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }
}