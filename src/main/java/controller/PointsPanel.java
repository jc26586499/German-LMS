package controller;

import util.Tool;
import util.ExcelUtil;
import vo.UserVO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PointsPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final int MIN_TOPUP = 100;

    private final UserVO user;
    private final MainFrame mainFrame;

    private JLabel lblBalance;
    private JTextField txtTopUp;
    private JButton btnTopUp;
    private JButton btnRefresh;
    private JButton btnExport;

    private JTable tblTx;
    private DefaultTableModel txModel;

    public PointsPanel(UserVO user, MainFrame mainFrame) {
        this.user = user;
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        initUI();
        refreshAll(); // 同步 DB
    }

    private void initUI() {

        // ===== Top Title =====
        JLabel title = new JLabel("Points - Balance & Top-up");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(45, 52, 54));
        title.setBorder(new EmptyBorder(18, 18, 10, 18));
        add(title, BorderLayout.NORTH);

        // ===== Center Container =====
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(0, 18, 18, 18));
        add(center, BorderLayout.CENTER);

        // ===== Transaction Table (Export) =====
        txModel = new DefaultTableModel(new Object[]{"t_id", "username", "amount", "description", "t_date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblTx = new JTable(txModel);
        tblTx.setRowHeight(30);
        tblTx.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tblTx.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

        JScrollPane sp = new JScrollPane(tblTx);
        center.add(sp, BorderLayout.CENTER);

        // ===== Balance + TopUp Row =====
        JPanel topRow = new JPanel(new GridBagLayout());
        topRow.setOpaque(false);
        topRow.setBorder(new EmptyBorder(0, 0, 12, 0));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridy = 0;
        gc.insets = new Insets(6, 6, 6, 6);
        gc.anchor = GridBagConstraints.WEST;

        lblBalance = new JLabel("Balance: " + user.getBalance());
        lblBalance.setFont(new Font("SansSerif", Font.BOLD, 15));

        gc.gridx = 0;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        topRow.add(lblBalance, gc);

        gc.gridx = 1;
        gc.weightx = 0;
        gc.fill = GridBagConstraints.NONE;
        topRow.add(new JLabel("Top-up (points):"), gc);

        txtTopUp = new JTextField("500", 8);
        txtTopUp.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gc.gridx = 2;
        topRow.add(txtTopUp, gc);

        btnTopUp = new JButton("Top-up");
        btnTopUp.addActionListener(e -> doTopUp());
        gc.gridx = 3;
        topRow.add(btnTopUp, gc);

        btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> refreshAll());
        gc.gridx = 4;
        topRow.add(btnRefresh, gc);

        btnExport = new JButton("Export Excel");
        btnExport.addActionListener(e -> doExport());
        gc.gridx = 5;
        topRow.add(btnExport, gc);

        center.add(topRow, BorderLayout.NORTH);
    }

    private void doExport() {
        // 沒資料提醒
        if (txModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No records to export.");
            return;
        }
        ExcelUtil.exportTableToExcel(tblTx, "transactions_export");
    }

    private void refreshAll() {
        try {
            int balance = fetchBalanceFromDb();
            user.setBalance(balance);

            lblBalance.setText("Balance: " + balance);
            mainFrame.refreshBalance(balance);

            loadTransactions();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage() == null ? ex.toString() : ex.getMessage(),
                    "Refresh Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doTopUp() {
        String s = txtTopUp.getText().trim();
        int amount;

        try {
            amount = Integer.parseInt(s);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Please enter a whole number\n(e.g., 500)");
            return;
        }

        if (amount < MIN_TOPUP) {
            JOptionPane.showMessageDialog(this, "Minimum top-up: " + MIN_TOPUP + " points");
            return;
        }

        int ok = JOptionPane.showConfirmDialog(
                this,
                "Top up " + amount + " points?\n(Rate: 1 TWD = 1 Point)",
                "Confirm Top-up",
                JOptionPane.YES_NO_OPTION
        );

        if (ok != JOptionPane.YES_OPTION) return;

        btnTopUp.setEnabled(false);

        try {
            Connection conn = Tool.getDb();
            if (conn == null) throw new Exception("DB connection failed.");

            try {
                int current = getBalance(conn, user.getUserId());
                int newBalance = current + amount;

                updateBalance(conn, user.getUserId(), newBalance);
                insertTransaction(conn, user.getUserId(), amount, "Top-up +" + amount);

                user.setBalance(newBalance);
                lblBalance.setText("Balance: " + newBalance);
                mainFrame.refreshBalance(newBalance);

                loadTransactions();

                JOptionPane.showMessageDialog(this, "Top-up successful!\nCurrent balance:" + newBalance);

            } finally {
                try { conn.close(); } catch (Exception ignore) {}
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage() == null ? ex.toString() : ex.getMessage(),
                    "Top-up Failed",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            btnTopUp.setEnabled(true);
        }
    }

    // ===== DB helpers =====

    private int fetchBalanceFromDb() throws Exception {
        Connection conn = Tool.getDb();
        if (conn == null) throw new Exception("DB connection failed.");
        try {
            return getBalance(conn, user.getUserId());
        } finally {
            try { conn.close(); } catch (Exception ignore) {}
        }
    }

    private int getBalance(Connection conn, int userId) throws Exception {
        String sql = "SELECT balance FROM users WHERE user_id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();

        if (!rs.next()) {
            rs.close();
            ps.close();
            throw new Exception("User not found: user_id=" + userId);
        }

        int b = rs.getInt("balance");
        rs.close();
        ps.close();
        return b;
    }

    private void updateBalance(Connection conn, int userId, int newBalance) throws Exception {
        String sql = "UPDATE users SET balance=? WHERE user_id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, newBalance);
        ps.setInt(2, userId);
        int rows = ps.executeUpdate();
        ps.close();

        if (rows != 1) throw new Exception("Update balance failed.");
    }

    private void insertTransaction(Connection conn, int userId, int amount, String desc) throws Exception {
        String sql = "INSERT INTO transactions (user_id, amount, description) VALUES (?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userId);
        ps.setInt(2, amount);
        ps.setString(3, desc);
        ps.executeUpdate();
        ps.close();
    }

    private void loadTransactions() throws Exception {
        txModel.setRowCount(0);

        Connection conn = Tool.getDb();
        if (conn == null) throw new Exception("DB connection failed.");

        try {
            String sql = "SELECT t_id, username, amount, description, t_date "
                    + "FROM view_transaction_history "
                    + "WHERE username = ? "
                    + "ORDER BY t_date DESC";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                txModel.addRow(new Object[]{
                        rs.getInt("t_id"),
                        rs.getString("username"),
                        rs.getInt("amount"),
                        rs.getString("description"),
                        rs.getTimestamp("t_date")
                });
            }

            rs.close();
            ps.close();

        } finally {
            try { conn.close(); } catch (Exception ignore) {}
        }
    }
}