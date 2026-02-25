package controller;

import com.formdev.flatlaf.FlatLightLaf;
import service.UserService;
import service.impl.UserServiceImpl;
import vo.UserVO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * FlatLaf Minimal Professional Login UI (JFrame)
 * - Includes "Create account" button -> opens RegisterDialog
 * - Enter triggers login (on text fields)
 * - Password eye toggle
 */
public class LoginUI extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnCreate;

    private JButton btnEye;
    private boolean passwordVisible = false;
    private char defaultEchoChar;

    private final UserService userService = new UserServiceImpl();

    public LoginUI() {
        setTitle("German LMS - Login");
        setSize(560, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(new Color(245, 247, 250));
        setContentPane(root);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(22, 26, 22, 26));
        card.setPreferredSize(new Dimension(420, 290));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(6, 0, 6, 0);

        JLabel lblTitle = new JLabel("German LMS", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(new Color(45, 52, 54));
        gc.gridy = 0;
        gc.insets = new Insets(0, 0, 2, 0);
        card.add(lblTitle, gc);

        JLabel lblSub = new JLabel("Sign in to continue", SwingConstants.CENTER);
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSub.setForeground(new Color(120, 120, 120));
        gc.gridy = 1;
        gc.insets = new Insets(0, 0, 16, 0);
        card.add(lblSub, gc);

        // Username
        JLabel lblU = new JLabel("Username");
        lblU.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblU.setForeground(new Color(70, 70, 70));
        gc.gridy = 2;
        gc.insets = new Insets(0, 0, 6, 0);
        card.add(lblU, gc);

        txtUsername = new JTextField();
        styleTextField(txtUsername);
        gc.gridy = 3;
        gc.insets = new Insets(0, 0, 14, 0);
        card.add(txtUsername, gc);

        // Password
        JLabel lblP = new JLabel("Password");
        lblP.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblP.setForeground(new Color(70, 70, 70));
        gc.gridy = 4;
        gc.insets = new Insets(0, 0, 6, 0);
        card.add(lblP, gc);

        // ===== Password row with eye =====
        JPanel pwRow = new JPanel(new BorderLayout(8, 0));
        pwRow.setOpaque(false);

        txtPassword = new JPasswordField();
        styleTextField(txtPassword);

        defaultEchoChar = txtPassword.getEchoChar();

        btnEye = new JButton("👁");
        btnEye.setFocusPainted(false);
        btnEye.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEye.setPreferredSize(new Dimension(48, 38));
        btnEye.addActionListener(e -> togglePassword());

        pwRow.add(txtPassword, BorderLayout.CENTER);
        pwRow.add(btnEye, BorderLayout.EAST);

        gc.gridy = 5;
        gc.insets = new Insets(0, 0, 16, 0);
        card.add(pwRow, gc);

        // Buttons
        JPanel btnRow = new JPanel(new GridLayout(1, 3, 10, 0));
        btnRow.setOpaque(false);

        JButton btnExit = new JButton("Exit");
        styleSecondaryButton(btnExit);
        btnExit.addActionListener(e -> System.exit(0));

        btnCreate = new JButton("Register");
        styleSecondaryButton(btnCreate);
        btnCreate.addActionListener(e -> openRegisterDialog());

        btnLogin = new JButton("Login");
        stylePrimaryButton(btnLogin);
        btnLogin.addActionListener(e -> doLogin());

        btnRow.add(btnExit);
        btnRow.add(btnCreate);
        btnRow.add(btnLogin);

        gc.gridy = 6;
        gc.insets = new Insets(0, 0, 0, 0);
        card.add(btnRow, gc);

        root.add(card);

        txtUsername.addActionListener(e -> doLogin());
        txtPassword.addActionListener(e -> doLogin());

        // Demo default
        //txtUsername.setText("admin");
        //txtPassword.setText("1234");
    }

    private void togglePassword() {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            txtPassword.setEchoChar((char) 0);
            btnEye.setText("🙈");
        } else {
            txtPassword.setEchoChar(defaultEchoChar);
            btnEye.setText("👁");
        }
    }

    private void openRegisterDialog() {
        RegisterDialog dialog = new RegisterDialog(this);
        dialog.setVisible(true);

        if (dialog.isRegisterSuccess()) {
            txtUsername.setText(dialog.getRegisteredUsername());
            txtPassword.setText("");
            txtPassword.requestFocusInWindow();
        }
    }

    private void doLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        btnLogin.setEnabled(false);
        btnCreate.setEnabled(false);

        try {
            UserVO user = userService.login(username, password);
            MainFrame main = new MainFrame(user);
            main.setVisible(true);
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            btnLogin.setEnabled(true);
            btnCreate.setEnabled(true);
        }
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(10, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(7, 10, 7, 10)
        ));
    }

    private void stylePrimaryButton(JButton btn) {
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBackground(new Color(47, 128, 237));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(10, 40));
    }

    private void styleSecondaryButton(JButton btn) {
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(10, 40));
    }

    public static void main(String[] args) {
        try {
            FlatLightLaf.setup();
        } catch (Exception ignored) {}

        EventQueue.invokeLater(() -> {
            try {
                new LoginUI().setVisible(true);
            } catch (Throwable t) {
                t.printStackTrace();
                JOptionPane.showMessageDialog(null, t.toString(), "Crash", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}