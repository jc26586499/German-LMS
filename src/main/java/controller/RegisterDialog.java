package controller;

import service.UserService;
import service.impl.UserServiceImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * RegisterDialog (JDialog)
 * - Create account (username/password/confirm)
 * - Password eye toggle for both fields
 * - Fix bottom buttons clipped by increasing dialog/card size
 */
public class RegisterDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private JTextField txtUsername;

    private JPasswordField txtPassword;
    private JPasswordField txtConfirm;

    private JButton btnEye1;
    private JButton btnEye2;

    private boolean passVisible1 = false;
    private boolean passVisible2 = false;

    private char echo1;
    private char echo2;

    private JButton btnCreate;
    private JButton btnCancel;

    private boolean registerSuccess = false;
    private String registeredUsername = null;

    private final UserService userService = new UserServiceImpl();

    public RegisterDialog(Frame owner) {
        super(owner, "Create account", true);

        
        setSize(620, 440);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(new Color(245, 247, 250));
        setContentPane(root);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(22, 26, 22, 26));

        // ✅ 修正：卡片本身也加大
        card.setPreferredSize(new Dimension(520, 340));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(6, 0, 6, 0);

        JLabel lblTitle = new JLabel("Create account", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(new Color(45, 52, 54));
        gc.gridy = 0;
        gc.insets = new Insets(0, 0, 14, 0);
        card.add(lblTitle, gc);

        // Username
        JLabel lblU = new JLabel("Username");
        lblU.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblU.setForeground(new Color(70, 70, 70));
        gc.gridy = 1;
        gc.insets = new Insets(0, 0, 6, 0);
        card.add(lblU, gc);

        txtUsername = new JTextField();
        styleTextField(txtUsername);
        gc.gridy = 2;
        gc.insets = new Insets(0, 0, 12, 0);
        card.add(txtUsername, gc);

        // Password
        JLabel lblP = new JLabel("Password");
        lblP.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblP.setForeground(new Color(70, 70, 70));
        gc.gridy = 3;
        gc.insets = new Insets(0, 0, 6, 0);
        card.add(lblP, gc);

        JPanel pwRow1 = new JPanel(new BorderLayout(8, 0));
        pwRow1.setOpaque(false);

        txtPassword = new JPasswordField();
        styleTextField(txtPassword);
        echo1 = txtPassword.getEchoChar();

        btnEye1 = new JButton("👁");
        styleEyeButton(btnEye1);
        btnEye1.addActionListener(e -> togglePassword1());

        pwRow1.add(txtPassword, BorderLayout.CENTER);
        pwRow1.add(btnEye1, BorderLayout.EAST);

        gc.gridy = 4;
        gc.insets = new Insets(0, 0, 12, 0);
        card.add(pwRow1, gc);

        // Confirm password
        JLabel lblC = new JLabel("Confirm password");
        lblC.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblC.setForeground(new Color(70, 70, 70));
        gc.gridy = 5;
        gc.insets = new Insets(0, 0, 6, 0);
        card.add(lblC, gc);

        JPanel pwRow2 = new JPanel(new BorderLayout(8, 0));
        pwRow2.setOpaque(false);

        txtConfirm = new JPasswordField();
        styleTextField(txtConfirm);
        echo2 = txtConfirm.getEchoChar();

        btnEye2 = new JButton("👁");
        styleEyeButton(btnEye2);
        btnEye2.addActionListener(e -> togglePassword2());

        pwRow2.add(txtConfirm, BorderLayout.CENTER);
        pwRow2.add(btnEye2, BorderLayout.EAST);

        gc.gridy = 6;
        gc.insets = new Insets(0, 0, 16, 0);
        card.add(pwRow2, gc);

        // Buttons
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 12, 0));
        btnRow.setOpaque(false);

        btnCancel = new JButton("Cancel");
        styleSecondaryButton(btnCancel);
        btnCancel.addActionListener(e -> dispose());

        btnCreate = new JButton("Create");
        stylePrimaryButton(btnCreate);
        btnCreate.addActionListener(e -> doRegister());

        btnRow.add(btnCancel);
        btnRow.add(btnCreate);

        gc.gridy = 7;
        gc.insets = new Insets(0, 0, 0, 0);
        card.add(btnRow, gc);

        root.add(card);

        // Enter -> create
        txtUsername.addActionListener(e -> doRegister());
        txtPassword.addActionListener(e -> doRegister());
        txtConfirm.addActionListener(e -> doRegister());
    }

    private void togglePassword1() {
        passVisible1 = !passVisible1;
        if (passVisible1) {
            txtPassword.setEchoChar((char) 0);
            btnEye1.setText("🙈");
        } else {
            txtPassword.setEchoChar(echo1);
            btnEye1.setText("👁");
        }
    }

    private void togglePassword2() {
        passVisible2 = !passVisible2;
        if (passVisible2) {
            txtConfirm.setEchoChar((char) 0);
            btnEye2.setText("🙈");
        } else {
            txtConfirm.setEchoChar(echo2);
            btnEye2.setText("👁");
        }
    }

    private void doRegister() {
        String username = txtUsername.getText().trim();
        String pw = new String(txtPassword.getPassword());
        String cf = new String(txtConfirm.getPassword());

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username cannot be empty.");
            return;
        }

        btnCreate.setEnabled(false);
        btnCancel.setEnabled(false);

        try {
            // ✅ 用你 interface 的三參數版本
            userService.register(username, pw, cf);

            registerSuccess = true;
            registeredUsername = username;

            JOptionPane.showMessageDialog(this, "Create account success!");
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage() == null ? ex.toString() : ex.getMessage(),
                    "Create account failed",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            btnCreate.setEnabled(true);
            btnCancel.setEnabled(true);
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

    private void styleEyeButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(48, 38));
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

    public boolean isRegisterSuccess() {
        return registerSuccess;
    }

    public String getRegisteredUsername() {
        return registeredUsername;
    }
}