package com.nsbank;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * MainDashboard – the welcome/landing screen.
 * Users choose to Log In or Create an Account.
 */
public class MainDashboard extends JFrame {

    // ── Palette ───────────────────────────────────────────────
    private static final Color BRAND_DARK   = new Color(10, 61, 98);   // deep navy
    private static final Color BRAND_ACCENT = new Color(0, 150, 199);  // sky blue
    private static final Color BRAND_LIGHT  = new Color(232, 246, 253);
    private static final Color WHITE        = Color.WHITE;

    // ── Components ────────────────────────────────────────────
    private JButton btnLogin;
    private JButton btnSignUp;

    public MainDashboard() {
        initUI();
        pack();
        setTitle("NSBank – Neighborhood Savings Bank");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        // ── Listeners ─────────────────────────────────────────
        btnLogin.addActionListener(e -> {
            new LoginForm(this).setVisible(true);
            setVisible(false);
        });

        btnSignUp.addActionListener(e -> {
            new RegistrationForm(this).setVisible(true);
            setVisible(false);
        });
    }

    // ── UI BUILD ──────────────────────────────────────────────
    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BRAND_DARK);
        root.setPreferredSize(new Dimension(380, 580));

        // ── Header ────────────────────────────────────────────
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(50, 30, 20, 30));

        JLabel lblIcon = new JLabel("🏦");
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblName = new JLabel("NSBank");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblName.setForeground(WHITE);
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTagline = new JLabel("Neighborhood Savings Bank");
        lblTagline.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTagline.setForeground(new Color(180, 215, 240));
        lblTagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(lblIcon);
        header.add(Box.createVerticalStrut(10));
        header.add(lblName);
        header.add(Box.createVerticalStrut(4));
        header.add(lblTagline);

        // ── Body ──────────────────────────────────────────────
        JPanel body = new JPanel();
        body.setBackground(BRAND_LIGHT);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(40, 30, 40, 30));

        JLabel lblSlogan = new JLabel("<html><center>Trusted Banking App.<br>Bank smarter. Bank local.</center></html>");
        lblSlogan.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSlogan.setForeground(BRAND_DARK);
        lblSlogan.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSlogan.setHorizontalAlignment(SwingConstants.CENTER);

        btnSignUp = createPrimaryButton("CREATE AN ACCOUNT");
        btnLogin  = createOutlineButton("LOG IN");

        JLabel disclaimer = new JLabel("<html><center><small>By Francis Nikko Altares.</small></center></html>");
        disclaimer.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        disclaimer.setForeground(Color.GRAY);
        disclaimer.setAlignmentX(Component.CENTER_ALIGNMENT);

        body.add(lblSlogan);
        body.add(Box.createVerticalStrut(30));
        body.add(btnSignUp);
        body.add(Box.createVerticalStrut(12));
        body.add(btnLogin);
        body.add(Box.createVerticalStrut(20));
        body.add(disclaimer);

        root.add(header, BorderLayout.NORTH);
        root.add(body, BorderLayout.CENTER);

        setContentPane(root);
    }

    // ── Button factories ──────────────────────────────────────
    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(BRAND_ACCENT);
        btn.setForeground(WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    private JButton createOutlineButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(WHITE);
        btn.setForeground(BRAND_DARK);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(BRAND_ACCENT, 2));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }
}
