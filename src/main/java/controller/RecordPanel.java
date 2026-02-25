package controller;

import util.Tool;
import vo.UserVO;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class RecordPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final UserVO user;
    private final MainFrame mainFrame;

    // ===== Rules =====
    private static final int DAILY_POINTS = 1; // 每天打卡 +1
    private static final int BONUS_3 = 15;
    private static final int BONUS_20 = 50;
    private static final int BONUS_FINAL = 100;

    // 右上角規則卡的寬度
    private static final int RULE_BOX_WIDTH = 380;

    // UI
    private JLabel lblTodayStatus;
    private JLabel lblMonthCount;
    private JLabel lblStreak;
    private JLabel lblMonthTitle;

    private JButton btnPrevMonth;
    private JButton btnNextMonth;
    private JButton btnCheckIn;
    private JButton btnRefresh;

    private JTable tblCalendar;
    private DefaultTableModel calModel;

    private JTable tblDates;
    private DefaultTableModel datesModel;

    // NEW: rules box
    private JTextArea txtRules;

    // Calendar state
    private YearMonth currentYM = YearMonth.now();
    private Set<Integer> checkedDays = new HashSet<>(); // 本月打卡的 day-of-month 集合

    private static final DateTimeFormatter YM_FMT = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public RecordPanel(UserVO user, MainFrame mainFrame) {
        this.user = user;
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        initUI();
        refreshAll();
    }

    private void initUI() {
        // ===== Title =====
        JLabel title = new JLabel("Record - Daily Check-in");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(45, 52, 54));
        title.setBorder(new EmptyBorder(18, 18, 10, 18));
        add(title, BorderLayout.NORTH);

        // ===== Center =====
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(0, 18, 18, 18));
        add(center, BorderLayout.CENTER);

        // ===============================
        // Top Area: infoLeft & rulesCard
        // ===============================
        JPanel infoLeft = new JPanel();
        infoLeft.setOpaque(false);
        infoLeft.setLayout(new BoxLayout(infoLeft, BoxLayout.Y_AXIS));
        // 這裡的左邊距設為 8，跟下方的日曆標題對齊
        infoLeft.setBorder(new EmptyBorder(10, 8, 10, 10)); 

        lblTodayStatus = new JLabel("Today: ...");
        lblTodayStatus.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTodayStatus.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblMonthCount = new JLabel("This month check-ins: ...");
        lblMonthCount.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblMonthCount.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblStreak = new JLabel("Streak: ...");
        lblStreak.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblStreak.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10)); // 邊距設為 0
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnCheckIn = new JButton("Check-in");
        btnCheckIn.addActionListener(e -> doCheckIn());

        btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> refreshAll());

        btnRow.add(btnCheckIn);
        btnRow.add(Box.createHorizontalStrut(10)); // 手動補間距
        btnRow.add(btnRefresh);

        infoLeft.add(lblTodayStatus);
        infoLeft.add(Box.createVerticalStrut(8));
        infoLeft.add(lblMonthCount);
        infoLeft.add(Box.createVerticalStrut(8));
        infoLeft.add(lblStreak);
        infoLeft.add(Box.createVerticalStrut(10));
        infoLeft.add(btnRow);

        // ---- Right rules card ----
        JPanel rulesCard = new JPanel(new BorderLayout());
        rulesCard.setBackground(Color.WHITE);
        rulesCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel rulesTitle = new JLabel("Rules (Streak & Points)");
        rulesTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        rulesCard.add(rulesTitle, BorderLayout.NORTH);

        JTextArea rulesText = new JTextArea(
                "\n• Daily Check-in: +1 Point\n\n" +
                "• Monthly Milestones:\n" +
                "- Day 3 Reward: +15 Points\n" +
                "- Day 20 Reward: +50 Points\n" +
                "- Full Month Reward: +100 Points"
        );
        rulesText.setEditable(false);
        rulesText.setOpaque(false);
        rulesText.setFont(new Font("SansSerif", Font.PLAIN, 13));
        rulesCard.add(rulesText, BorderLayout.CENTER);

        // Top splitpane
        JSplitPane topSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, infoLeft, rulesCard);
        topSplit.setResizeWeight(0.65); // 稍微給左邊多一點空間
        topSplit.setBorder(null);
        topSplit.setDividerSize(6);
        topSplit.setEnabled(false); // 禁用拖動，強制跟隨下方

        center.add(topSplit, BorderLayout.NORTH);

        // ===============================
        // Bottom Area: Calendar + Date list
        // ===============================
        JSplitPane bottomSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        bottomSplit.setResizeWeight(0.65);
        bottomSplit.setBorder(null);
        bottomSplit.setDividerSize(6);

        center.add(bottomSplit, BorderLayout.CENTER);

        // ---- Left: Calendar panel ----
        JPanel calPanel = new JPanel(new BorderLayout());
        calPanel.setOpaque(false);

        JPanel calHeader = new JPanel(new BorderLayout());
        calHeader.setOpaque(false);
        calHeader.setBorder(new EmptyBorder(8, 8, 8, 8)); // 統一左邊距 8

        btnPrevMonth = new JButton("<");
        btnNextMonth = new JButton(">");
        btnPrevMonth.addActionListener(e -> { currentYM = currentYM.minusMonths(1); refreshCalendarOnly(); });
        btnNextMonth.addActionListener(e -> { currentYM = currentYM.plusMonths(1); refreshCalendarOnly(); });

        lblMonthTitle = new JLabel(currentYM.format(YM_FMT), SwingConstants.CENTER);
        lblMonthTitle.setFont(new Font("SansSerif", Font.BOLD, 15));

        calHeader.add(btnPrevMonth, BorderLayout.WEST);
        calHeader.add(lblMonthTitle, BorderLayout.CENTER);
        calHeader.add(btnNextMonth, BorderLayout.EAST);

        calPanel.add(calHeader, BorderLayout.NORTH);

        String[] cols = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        calModel = new DefaultTableModel(cols, 6) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        tblCalendar = new JTable(calModel);
        tblCalendar.setRowHeight(44);
        tblCalendar.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tblCalendar.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        tblCalendar.setDefaultRenderer(Object.class, new CalendarCellRenderer());

        calPanel.add(new JScrollPane(tblCalendar), BorderLayout.CENTER);

     // ---- Right: Date list ----
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setOpaque(false);

        JLabel listTitle = new JLabel("This Month Check-in Dates");
        listTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        listTitle.setBorder(new EmptyBorder(8, 8, 8, 8));
        listPanel.add(listTitle, BorderLayout.NORTH);

     // 1. 初始化 Model (單欄)
        datesModel = new DefaultTableModel(new Object[]{"Date"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

     // 2. 初始化 Table
        tblDates = new JTable(datesModel);
        tblDates.setRowHeight(28);
        tblDates.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tblDates.setTableHeader(null); // 隱藏表頭，看起來更像清單
        tblDates.setShowGrid(false);   // 隱藏格線
        tblDates.setIntercellSpacing(new Dimension(0, 0));

        // 3. ✅ 關鍵步驟：建立捲軸並把 Table 放進去
        JScrollPane scrollPane = new JScrollPane(tblDates);
        
        // 設定捲軸永遠在需要時出現 (垂直自動)
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        // 設定捲軸背景跟隨主題 (白色)
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        // 4. ✅ 把「捲軸」加進面板，而不是直接加 Table
        listPanel.add(scrollPane, BorderLayout.CENTER);

        // 5. 將 listPanel 放入右邊分割視窗
        bottomSplit.setRightComponent(listPanel);
        
        //將清單塞入捲軸，再加進面板
        listPanel.add(new JScrollPane(tblDates), BorderLayout.CENTER);

        //把兩個大面板塞進下方的分割視窗
        bottomSplit.setLeftComponent(calPanel);
        bottomSplit.setRightComponent(listPanel);	

        // ✅ 強制同步與對齊
        // 使用 Listener 監聽拖動，讓上方跟著下方走
        bottomSplit.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, e -> {
            topSplit.setDividerLocation(bottomSplit.getDividerLocation());
        });

        // 初始位置設定
        SwingUtilities.invokeLater(() -> {
            int initialPos = (int)(center.getWidth() * 0.65);
            bottomSplit.setDividerLocation(initialPos);
            topSplit.setDividerLocation(initialPos);
        });
    }

    // ============ Main refresh ============

    private void refreshAll() {
        try (Connection conn = Tool.getDb()) {
            if (conn == null) throw new Exception("DB connection failed.");

            boolean checkedToday = isCheckedInToday(conn, user.getUserId());
            int monthCount = getThisMonthCount(conn, user.getUserId()); // 以系統當月
            int streak = calcStreak(conn, user.getUserId());

            lblTodayStatus.setText("Today: " + (checkedToday ? "✅ Checked-in" : "❌ Not yet"));
            lblMonthCount.setText("This month check-ins: " + monthCount);
            lblStreak.setText("Streak: " + streak + " day(s)");

            btnCheckIn.setEnabled(!checkedToday);

            // calendar + list 用 currentYM（可切月）
            loadMonthCheckins(conn);
            renderCalendar();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage() == null ? ex.toString() : ex.getMessage(),
                    "Load Record Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshCalendarOnly() {
        try (Connection conn = Tool.getDb()) {
            if (conn == null) throw new Exception("DB connection failed.");
            loadMonthCheckins(conn);
            renderCalendar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage() == null ? ex.toString() : ex.getMessage(),
                    "Calendar Load Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ============ Check-in action ============

    private void doCheckIn() {
        btnCheckIn.setEnabled(false);

        try (Connection conn = Tool.getDb()) {
            if (conn == null) throw new Exception("DB connection failed.");

            // 1) 防重複打卡
            if (isCheckedInToday(conn, user.getUserId())) {
                JOptionPane.showMessageDialog(this, "你今天已經打卡過了！");
                refreshAll();
                return;
            }

            // 2) 寫入打卡紀錄
            insertCheckIn(conn, user.getUserId());

            // 3) 每日基礎獎勵 +1 點
            int newBalance = addPoints(conn, user.getUserId(), DAILY_POINTS,
                    "Daily Check-in " + LocalDate.now().format(DATE_FMT));

            // 4) 計算 Streak (給使用者看，跨月不歸零)
            int streak = calcStreak(conn, user.getUserId());

            // 5) 獎勵判定：改用「本月累積次數」來發獎金 (每個月獨立計算)
            int thisMonthCount = getThisMonthCount(conn, user.getUserId());
            int monthLen = YearMonth.now().lengthOfMonth();

            int bonus = 0;
            int threshold = -1;

            // 判定里程碑
            if (thisMonthCount == 3) { 
                bonus = BONUS_3; 
                threshold = 3; 
            } else if (thisMonthCount == 20) { 
                bonus = BONUS_20; 
                threshold = 20; 
            } else if (thisMonthCount == monthLen) { 
                bonus = BONUS_FINAL; 
                threshold = monthLen; 
            }

            // 6) 若達到月里程碑，發放獎金 (同一個月、同一個門檻只領一次)
            if (bonus > 0) {
                String ym = YearMonth.now().format(YM_FMT);
                // 這裡的 token 會包含月份，所以下個月 1 號開始，token 就不一樣了，可以重新挑戰
                String token = "Month Milestone " + threshold + " (" + ym + ")";

                if (!hasBonusPaidThisMonth(conn, user.getUserId(), token)) {
                    newBalance = addPoints(conn, user.getUserId(), bonus, token);

                    JOptionPane.showMessageDialog(this,
                            "Check-in Successful! (+1 Point)\n" +
                            "🎉 Monthly Milestone Reached\n "+"Day"+ threshold +"："+ bonus + " Points\n" +
                            "Current Streak:：" + streak + " Days!\n" +
                            "Current Balance：" + newBalance);
                } else {
                    JOptionPane.showMessageDialog(this, "Check-in Successful! (+1 Point)\nCurrent Streak：" + streak + " Days!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Check-in Successful! (+1 Point)\nCurrent Streak:" + streak + " Days！");
            }

            // 7) 更新 UI 與 Sidebar
            user.setBalance(newBalance);
            mainFrame.refreshBalance(newBalance);
            refreshAll();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Check-in Failed：" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            refreshAll();
        }
    }

    // ============ Calendar data loading & rendering ============

    private void loadMonthCheckins(Connection conn) throws Exception {
        checkedDays.clear();
        datesModel.setRowCount(0);
        
        int monthLen = currentYM.lengthOfMonth();
        int counter = 0; // 用來計算這是本月第幾次打卡

        String sql = "SELECT DISTINCT DATE(study_date) AS d " +
                     "FROM study_logs " +
                     "WHERE user_id=? AND YEAR(study_date)=? AND MONTH(study_date)=? " +
                     "ORDER BY d ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, user.getUserId());
            ps.setInt(2, currentYM.getYear());
            ps.setInt(3, currentYM.getMonthValue());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    counter++; // 1. 先累加次數
                    
                    java.sql.Date d = rs.getDate("d");
                    LocalDate ld = d.toLocalDate();
                    
                    // 2. 更新日曆用的集合
                    checkedDays.add(ld.getDayOfMonth());
                    
                    // 3. 準備顯示在右側清單的字串
                    String dateStr = d.toString();

                        // ✅ 精確的里程碑文字邏輯
                        if (counter == 3) {
                            dateStr += " (3 Days Milestone 🎉)";
                        } else if (counter == 20) {
                            dateStr += " (20 Days Milestone 🎉)";
                        } else if (counter == monthLen) {
                            dateStr += " (Full Month Milestone 🏆)";
                        }

                        datesModel.addRow(new Object[]{dateStr});
                    }

                 
            }
        }
    }

    private void renderCalendar() {
        lblMonthTitle.setText(currentYM.format(YM_FMT));

        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 7; c++) {
                calModel.setValueAt("", r, c);
            }
        }

        LocalDate first = currentYM.atDay(1);
        int daysInMonth = currentYM.lengthOfMonth();

        int firstDow = first.getDayOfWeek().getValue(); // Mon=1..Sun=7
        int startCol = (firstDow == 7) ? 0 : firstDow; // Sun->0, Mon->1 ... Sat->6

        int day = 1;
        int r = 0;
        int c = startCol;

        while (day <= daysInMonth) {
            calModel.setValueAt(String.valueOf(day), r, c);

            day++;
            c++;
            if (c >= 7) {
                c = 0;
                r++;
                if (r >= 6) break;
            }
        }

        tblCalendar.repaint();
    }

 // ============ Render highlight ============

    private class CalendarCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {

            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, false, false, row, column);

            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setOpaque(true);

            String s = (value == null) ? "" : value.toString().trim();
            lbl.setText(s);

            // default style
            lbl.setBackground(Color.WHITE);
            lbl.setForeground(Color.BLACK);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 14));

            // empty cell
            if (s.isEmpty()) {
                lbl.setBackground(new Color(250, 250, 250));
                return lbl;
            }

            int day;
            try {
                day = Integer.parseInt(s);
            } catch (Exception e) {
                return lbl;
            }

            LocalDate today = LocalDate.now();
            boolean isThisMonth =
                    currentYM.getYear() == today.getYear()
                            && currentYM.getMonthValue() == today.getMonthValue();

            boolean isToday = isThisMonth && day == today.getDayOfMonth();
            boolean isChecked = checkedDays.contains(day);

            // ✅ 有打卡：淡綠
            if (isChecked) {
                lbl.setBackground(new Color(180, 235, 195));
            }

            // ✅ 今天：如果今天也有打卡 → 深綠；否則淡黃
            if (isToday) {
                lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
                if (isChecked) {
                    lbl.setBackground(new Color(120, 200, 140)); // 今天 + 已打卡：深綠
                } else {
                    lbl.setBackground(new Color(255, 245, 200)); // 今天未打卡：淡黃
                }
            }

            return lbl;
        }
    }
    // ============ streak logic ============

    private int calcStreak(Connection conn, int userId) throws Exception {
        int streak = 0;
        LocalDate d = LocalDate.now();

        while (true) {
            if (existsCheckInOnDate(conn, userId, d)) {
                streak++;
                d = d.minusDays(1);
            } else {
                break;
            }
        }
        return streak;
    }

    private boolean existsCheckInOnDate(Connection conn, int userId, LocalDate date) throws Exception {
        String sql = "SELECT 1 FROM study_logs WHERE user_id=? AND DATE(study_date)=? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, java.sql.Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ============ basic counts ============

    private boolean isCheckedInToday(Connection conn, int userId) throws Exception {
        String sql = "SELECT 1 FROM study_logs WHERE user_id=? AND DATE(study_date)=CURDATE() LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private int getThisMonthCount(Connection conn, int userId) throws Exception {
        String sql = "SELECT COUNT(DISTINCT DATE(study_date)) AS c " +
                "FROM study_logs " +
                "WHERE user_id=? AND YEAR(study_date)=YEAR(CURDATE()) AND MONTH(study_date)=MONTH(CURDATE())";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt("c");
            }
        }
    }

    private void insertCheckIn(Connection conn, int userId) throws Exception {
        String sql = "INSERT INTO study_logs (user_id, study_date) VALUES (?, NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    // ============ bonus & anti-duplicate ============

    private boolean hasBonusPaidThisMonth(Connection conn, int userId, String token) throws Exception {
        String sql = "SELECT 1 FROM transactions " +
                "WHERE user_id=? AND description=? " +
                "AND YEAR(t_date)=YEAR(CURDATE()) AND MONTH(t_date)=MONTH(CURDATE()) " +
                "LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, token);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // 統一加點：更新 users.balance + insert transactions，回傳 newBalance
    private int addPoints(Connection conn, int userId, int points, String desc) throws Exception {
        int current = getBalance(conn, userId);
        int newBalance = current + points;

        try (PreparedStatement ps = conn.prepareStatement("UPDATE users SET balance=? WHERE user_id=?")) {
            ps.setInt(1, newBalance);
            ps.setInt(2, userId);
            int rows = ps.executeUpdate();
            if (rows != 1) throw new Exception("Update balance failed.");
        }

        try (PreparedStatement ps2 = conn.prepareStatement(
                "INSERT INTO transactions (user_id, amount, description) VALUES (?,?,?)")) {
            ps2.setInt(1, userId);
            ps2.setInt(2, points);
            ps2.setString(3, desc);
            ps2.executeUpdate();
        }

        return newBalance;
    }

    private int getBalance(Connection conn, int userId) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("SELECT balance FROM users WHERE user_id=?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new Exception("User not found: user_id=" + userId);
                return rs.getInt("balance");
            }
        }
    }
}