package controller;

import vo.UserVO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final UserVO currentUser;

    private JLabel lblBalance;
    private JLabel lblNowTime;

    private CardLayout cardLayout;
    private JPanel contentPanel;

    // ✅ 把 panel 存成欄位，才能互相溝通
    private LearningPanel learningPanel;
    private ShopPanel shopPanel;
    private PointsPanel pointsPanel;
    private RecordPanel recordPanel;
    private AdminPanel adminPanel;

    private static final String CARD_LEARNING = "learning";
    private static final String CARD_SHOP = "shop";
    private static final String CARD_POINTS = "points";
    private static final String CARD_RECORD = "record";
    private static final String CARD_ADMIN = "admin";

    private Timer clockTimer;

    public MainFrame(UserVO user) {
        this.currentUser = user;

        setTitle("German Learning Management System");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();
        startClock();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // ===== Left Sidebar =====
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(240, 0));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(new EmptyBorder(18, 16, 18, 16));
        leftPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblUser = new JLabel("User: " + currentUser.getUsername());
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblUser.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblRole = new JLabel("Role: " + currentUser.getRole());
        lblRole.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblBalance = new JLabel("Balance: " + currentUser.getBalance());
        lblBalance.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblNowTime = new JLabel("Time: --");
        lblNowTime.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftPanel.add(lblUser);
        leftPanel.add(Box.createVerticalStrut(6));
        leftPanel.add(lblRole);
        leftPanel.add(Box.createVerticalStrut(6));
        leftPanel.add(lblBalance);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(lblNowTime);
        leftPanel.add(Box.createVerticalStrut(24));

        JButton btnLearning = makeSidebarButton("Learning");
        JButton btnShop = makeSidebarButton("Shop");
        JButton btnPoints = makeSidebarButton("Points");
        JButton btnRecord = makeSidebarButton("Record");
        JButton btnAdmin = makeSidebarButton("Admin");

        if (currentUser.getRole() == null || !"admin".equalsIgnoreCase(currentUser.getRole())) {
            btnAdmin.setEnabled(false);
            btnAdmin.setToolTipText("Admin only");
        }

        leftPanel.add(btnLearning);
        leftPanel.add(Box.createVerticalStrut(12));
        leftPanel.add(btnShop);
        leftPanel.add(Box.createVerticalStrut(12));
        leftPanel.add(btnPoints);
        leftPanel.add(Box.createVerticalStrut(12));
        leftPanel.add(btnRecord);
        leftPanel.add(Box.createVerticalStrut(12));
        leftPanel.add(btnAdmin);

        leftPanel.add(Box.createVerticalGlue());

        JButton btnLogout = makeSidebarButton("Logout");
        btnLogout.setBackground(new Color(183, 183, 189));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.addActionListener(e -> doLogout());
        leftPanel.add(btnLogout);

        add(leftPanel, BorderLayout.WEST);

        // ===== Right Cards =====
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // ✅ 存成欄位
        learningPanel = new LearningPanel(currentUser, this);
        shopPanel = new ShopPanel(currentUser, this);
        pointsPanel = new PointsPanel(currentUser, this);
        recordPanel = new RecordPanel(currentUser, this);
        adminPanel = new AdminPanel(currentUser);

        contentPanel.add(learningPanel, CARD_LEARNING);
        contentPanel.add(shopPanel, CARD_SHOP);
        contentPanel.add(pointsPanel, CARD_POINTS);
        contentPanel.add(recordPanel, CARD_RECORD);
        contentPanel.add(adminPanel, CARD_ADMIN);

        add(contentPanel, BorderLayout.CENTER);

        btnLearning.addActionListener(e -> showLearning());
        btnShop.addActionListener(e -> showShop());
        btnPoints.addActionListener(e -> showPoints());
        btnRecord.addActionListener(e -> showRecord());
        btnAdmin.addActionListener(e -> showAdmin());

        cardLayout.show(contentPanel, CARD_LEARNING);
    }

    private JButton makeSidebarButton(String text) {
        JButton b = new JButton(text);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        b.setPreferredSize(new Dimension(200, 44));
        b.setFont(new Font("SansSerif", Font.PLAIN, 15));
        b.setFocusPainted(false);
        return b;
    }

    private void startClock() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        clockTimer = new Timer(1000, e -> lblNowTime.setText("Time: " + LocalDateTime.now().format(fmt)));
        clockTimer.setInitialDelay(0);
        clockTimer.start();
    }

    private void doLogout() {
        int ok = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Logout", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        if (clockTimer != null) clockTimer.stop();

        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
        dispose();
    }

    // =========================
    // 對外提供切換方法
    // =========================
    public void showLearning() {
        cardLayout.show(contentPanel, CARD_LEARNING);
    }

    public void showShop() {
        cardLayout.show(contentPanel, CARD_SHOP);
    }

    public void showPoints() {
        cardLayout.show(contentPanel, CARD_POINTS);
    }

    public void showRecord() {
        cardLayout.show(contentPanel, CARD_RECORD);
    }

    public void showAdmin() {
        cardLayout.show(contentPanel, CARD_ADMIN);
    }

    // ShopPanel 呼叫：設定類別後跳到 Learning
    public void openLearningCategory(String category) {
        learningPanel.setCategory(category);   // 讓 LearningPanel 知道要出哪個題庫
        showLearning();                        // 切卡
    }

    public void refreshBalance(int newBalance) {
        lblBalance.setText("Balance: " + newBalance);
    }
}