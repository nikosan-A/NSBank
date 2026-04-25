package com.nsbank;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.regex.Pattern;

/**
 * RegistrationForm – new user account creation screen.
 */
public class RegistrationForm extends JFrame {

    private static final Color BRAND_DARK   = new Color(10, 61, 98);
    private static final Color BRAND_ACCENT = new Color(0, 150, 199);
    private static final Color BRAND_LIGHT  = new Color(232, 246, 253);
    private static final Color WHITE        = Color.WHITE;

    private final JFrame parent;

    // ── Input fields ──────────────────────────────────────────
    private JTextField     tfName;
    private JTextField     tfEmail;
    private JTextField     tfNumber;
    private JPasswordField pfMpin;
    private JPasswordField pfConfirmMpin;
    private JLabel         lblError;

    public RegistrationForm(JFrame parent) {
        this.parent = parent;
        initUI();
        pack();
        setTitle("NSBank – Create Account");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    // ── UI BUILD ──────────────────────────────────────────────
    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(WHITE);
        root.setPreferredSize(new Dimension(400, 620));

        // ── Top Banner ────────────────────────────────────────
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

        JLabel lblTitle = new JLabel("Create an Account");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(WHITE);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Unlock Your Financial Power. Register now.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(180, 215, 240));
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        banner.add(btnBack);
        banner.add(Box.createVerticalStrut(8));
        banner.add(lblTitle);
        banner.add(Box.createVerticalStrut(4));
        banner.add(lblSub);

        // ── Form ──────────────────────────────────────────────
        JPanel form = new JPanel();
        form.setBackground(WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(24, 30, 20, 30));

        tfName        = createTextField("Full Name");
        tfEmail       = createTextField("Email Address");
        tfNumber      = createTextField("Mobile Number (09XXXXXXXXX)");
        pfMpin        = createPasswordField("6-Digit MPIN");
        pfConfirmMpin = createPasswordField("Confirm MPIN");

        lblError = new JLabel(" ");
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblError.setForeground(new Color(200, 30, 30));
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnRegister = createPrimaryButton("SIGN UP");
        JButton btnLogin    = createOutlineButton("LOGIN INSTEAD");

        btnRegister.addActionListener(e -> handleRegister());
        btnLogin.addActionListener(e -> goBack());

        form.add(makeLabel("Full Name"));
        form.add(tfName);
        form.add(Box.createVerticalStrut(12));
        form.add(makeLabel("Email"));
        form.add(tfEmail);
        form.add(Box.createVerticalStrut(12));
        form.add(makeLabel("Mobile Number"));
        form.add(tfNumber);
        form.add(Box.createVerticalStrut(12));
        form.add(makeLabel("MPIN"));
        form.add(pfMpin);
        form.add(Box.createVerticalStrut(12));
        form.add(makeLabel("Confirm MPIN"));
        form.add(pfConfirmMpin);
        form.add(Box.createVerticalStrut(10));
        form.add(lblError);
        form.add(Box.createVerticalStrut(14));
        form.add(btnRegister);
        form.add(Box.createVerticalStrut(10));
        form.add(btnLogin);

        root.add(banner, BorderLayout.NORTH);
        root.add(new JScrollPane(form), BorderLayout.CENTER);

        setContentPane(root);
    }

    // ── REGISTER LOGIC ────────────────────────────────────────
    private void handleRegister() {
        String name        = tfName.getText().trim();
        String email       = tfEmail.getText().trim();
        String number      = tfNumber.getText().trim();
        String mpin        = new String(pfMpin.getPassword()).trim();
        String confirmMpin = new String(pfConfirmMpin.getPassword()).trim();

        // ── Validation ────────────────────────────────────────
        if (name.isEmpty() || email.isEmpty() || number.isEmpty()
                || mpin.isEmpty() || confirmMpin.isEmpty()) {
            setError("⚠ Please fill in all fields.");
            return;
        }
        if (!isValidName(name)) {
            setError("⚠ Name should contain letters only.");
            return;
        }
        if (!isValidEmail(email)) {
            setError("⚠ Invalid email address format.");
            return;
        }
        if (!isValidNumber(number)) {
            setError("⚠ Mobile number must be 11 digits starting with 09.");
            return;
        }
        if (!mpin.matches("\\d{6}")) {
            setError("⚠ MPIN must be exactly 4 digits.");
            return;
        }
        if (!mpin.equals(confirmMpin)) {
            setError("⚠ MPINs do not match.");
            return;
        }

        // ── DB Operation ──────────────────────────────────────
        if (UserAuthentication.numberExists(number)) {
            setError("⚠ Mobile number already registered.");
            return;
        }
        if (UserAuthentication.emailExists(email)) {
            setError("⚠ Email already registered.");
            return;
        }

        boolean success = UserAuthentication.register(name, email, number, mpin);
        if (success) {
            JOptionPane.showMessageDialog(this,
                "Account created successfully!\nYou can now log in.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            goBack();
        } else {
            setError("⚠ Registration failed. Please try again.");
        }
    }

    // ── Validation helpers ────────────────────────────────────
    private boolean isValidName(String n)   { return n.matches("[a-zA-Z ]{2,}"); }
    private boolean isValidEmail(String e)  { return Pattern.matches("^[^@]+@[^@]+\\.[^@]+$", e); }
    private boolean isValidNumber(String n) { return n.matches("09\\d{9}"); }

    private void setError(String msg) { lblError.setText(msg); }

    private void goBack() {
        parent.setVisible(true);
        dispose();
    }

    // ── Component factories ───────────────────────────────────
    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(BRAND_DARK);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField createTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.setToolTipText(placeholder);
        return tf;
    }

    private JPasswordField createPasswordField(String placeholder) {
        JPasswordField pf = new JPasswordField();
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        pf.setAlignmentX(Component.LEFT_ALIGNMENT);
        pf.setToolTipText(placeholder);
        return pf;
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

    private JButton createOutlineButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(WHITE);
        btn.setForeground(BRAND_DARK);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(BRAND_ACCENT, 2));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }
}
