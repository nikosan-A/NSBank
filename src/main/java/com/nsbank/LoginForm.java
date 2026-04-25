package com.nsbank;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * LoginForm – existing-user login screen.
 * Authenticates via mobile number + 6-digit MPIN.
 */
public class LoginForm extends JFrame {

    private static final Color BRAND_DARK   = new Color(10, 61, 98);
    private static final Color BRAND_ACCENT = new Color(0, 150, 199);
    private static final Color WHITE        = Color.WHITE;
    private static final Color BRAND_LIGHT  = new Color(232, 246, 253);

    private final JFrame parent;

    private JTextField     tfNumber;
    private JPasswordField pfMpin;
    private JLabel         lblError;

    public LoginForm(JFrame parent) {
        this.parent = parent;
        initUI();
        pack();
        setTitle("NSBank – Log In");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    // ── UI BUILD ──────────────────────────────────────────────
    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(WHITE);
        root.setPreferredSize(new Dimension(380, 520));

        // ── Banner ────────────────────────────────────────────
        JPanel banner = new JPanel();
        banner.setBackground(BRAND_DARK);
        banner.setLayout(new BoxLayout(banner, BoxLayout.Y_AXIS));
        banner.setBorder(new EmptyBorder(22, 20, 22, 20));

        JButton btnBack = new JButton("← Back");
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnBack.setForeground(new Color(180, 215, 240));
        btnBack.setContentAreaFilled(false);
        btnBack.setBorderPainted(false);
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBack.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnBack.addActionListener(e -> goBack());

        JLabel lblIcon  = new JLabel("🏦");
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        lblIcon.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel("Login to Your Account");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(WHITE);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        banner.add(btnBack);
        banner.add(Box.createVerticalStrut(8));
        banner.add(lblIcon);
        banner.add(Box.createVerticalStrut(6));
        banner.add(lblTitle);

        // ── Form ──────────────────────────────────────────────
        JPanel form = new JPanel();
        form.setBackground(WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(32, 30, 20, 30));

        tfNumber = new JTextField();
        tfNumber.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tfNumber.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        tfNumber.setAlignmentX(Component.LEFT_ALIGNMENT);

        pfMpin = new JPasswordField();
        pfMpin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pfMpin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        pfMpin.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblError = new JLabel(" ");
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblError.setForeground(new Color(200, 30, 30));
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnLogin    = createPrimaryButton("LOG IN");
        JButton btnForgot   = createLinkButton("FORGOT MPIN?");
        JButton btnSignUp   = createLinkButton("Sign up");

        btnLogin.addActionListener(e -> performLogin());
        pfMpin.addActionListener(e -> performLogin());   // Enter key shortcut

        btnForgot.addActionListener(e ->
            JOptionPane.showMessageDialog(this,
                "Please contact NSBank support to reset your MPIN.",
                "Forgot MPIN", JOptionPane.INFORMATION_MESSAGE));

        btnSignUp.addActionListener(e -> {
            new RegistrationForm(parent).setVisible(true);
            dispose();
        });

        // Row: Forgot pin (right-aligned)
        JPanel forgotRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        forgotRow.setOpaque(false);
        forgotRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        forgotRow.add(btnForgot);

        // Row: "No account yet? Sign up"
        JPanel signupRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        signupRow.setOpaque(false);
        signupRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        JLabel lblNoAcc = new JLabel("No account yet?");
        lblNoAcc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        signupRow.add(lblNoAcc);
        signupRow.add(btnSignUp);

        form.add(makeLabel("Enter your mobile number:"));
        form.add(Box.createVerticalStrut(6));
        form.add(tfNumber);
        form.add(Box.createVerticalStrut(16));
        form.add(makeLabel("Enter your MPIN:"));
        form.add(Box.createVerticalStrut(6));
        form.add(pfMpin);
        form.add(forgotRow);
        form.add(Box.createVerticalStrut(4));
        form.add(lblError);
        form.add(Box.createVerticalStrut(14));
        form.add(btnLogin);
        form.add(Box.createVerticalStrut(16));
        form.add(signupRow);

        root.add(banner, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        setContentPane(root);
    }

    // ── LOGIN LOGIC ───────────────────────────────────────────
    private void performLogin() {
        String number = tfNumber.getText().trim();
        String mpin   = new String(pfMpin.getPassword()).trim();

        if (number.isEmpty() || mpin.isEmpty()) {
            lblError.setText("⚠ Please fill in both fields.");
            return;
        }

        User user = UserAuthentication.login(number, mpin);
        if (user == null) {
            lblError.setText("⚠ Invalid mobile number or MPIN.");
            return;
        }

        lblError.setText(" ");
        dispose();

        if (user.isAdmin()) {
            new AdminDashboard(user).setVisible(true);
        } else {
            new AccountDashboard(user).setVisible(true);
        }
    }

    // ── Helpers ───────────────────────────────────────────────
    private void goBack() {
        parent.setVisible(true);
        dispose();
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(BRAND_DARK);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(BRAND_ACCENT);
        btn.setForeground(WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    private JButton createLinkButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(BRAND_ACCENT);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
