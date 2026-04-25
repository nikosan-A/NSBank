package com.nsbank;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * AccountDashboard – the main hub for a logged-in user.
 *
 * Panels (accessed via nav bar):
 *   Home  |  Cash In  |  Cash Out / Send  |  TXN History  |  Settings  |  Log Out
 */
public class AccountDashboard extends JFrame {

    // ── Palette ───────────────────────────────────────────────
    private static final Color BRAND_DARK   = new Color(10, 61, 98);
    private static final Color BRAND_ACCENT = new Color(0, 150, 199);
    private static final Color BRAND_LIGHT  = new Color(232, 246, 253);
    private static final Color GREEN        = new Color(39, 174, 96);
    private static final Color RED          = new Color(192, 57, 43);
    private static final Color WHITE        = Color.WHITE;

    private User currentUser;

    // ── Layout ────────────────────────────────────────────────
    private JPanel      contentPanel;
    private CardLayout  cardLayout;
    private JLabel      lblBalanceHome;

    // ── Panels ────────────────────────────────────────────────
    private JPanel homePanel;
    private JPanel cashInPanel;
    private JPanel cashOutPanel;
    private JPanel txnPanel;
    private JPanel settingsPanel;

    // ── Nav buttons ───────────────────────────────────────────
    private JButton btnNavHome;
    private JButton btnNavCashIn;
    private JButton btnNavCashOut;
    private JButton btnNavTxn;
    private JButton btnNavSettings;
    private JButton btnNavLogout;

    public AccountDashboard(User user) {
        this.currentUser = user;
        initUI();
        loadHomePanel();
        setTitle("NSBank – Account Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(520, 660));
        setPreferredSize(new Dimension(600, 700));
        pack();
        setLocationRelativeTo(null);
    }

    // ═══════════════════════════════════════════════════════════
    //  UI BUILD
    // ═══════════════════════════════════════════════════════════
    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BRAND_LIGHT);

        root.add(buildTopBar(),  BorderLayout.NORTH);
        root.add(buildContent(), BorderLayout.CENTER);
        root.add(buildNavBar(),  BorderLayout.SOUTH);

        setContentPane(root);
    }

    // ── Top bar ───────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BRAND_DARK);
        bar.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel lblWelcome = new JLabel("Welcome back, " + currentUser.getName() + "!");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblWelcome.setForeground(WHITE);

        JLabel lblMotivation = new JLabel("\"Save today, secure tomorrow.\"");
        lblMotivation.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblMotivation.setForeground(new Color(180, 215, 240));

        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setOpaque(false);
        left.add(lblWelcome);
        left.add(lblMotivation);

        bar.add(left, BorderLayout.WEST);
        return bar;
    }

    // ── Card-layout content area ──────────────────────────────
    private JPanel buildContent() {
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BRAND_LIGHT);

        homePanel     = buildHomePanel();
        cashInPanel   = buildCashInPanel();
        cashOutPanel  = buildCashOutPanel();
        txnPanel      = buildTxnPanel();
        settingsPanel = buildSettingsPanel();

        contentPanel.add(homePanel,     "home");
        contentPanel.add(cashInPanel,   "cashin");
        contentPanel.add(cashOutPanel,  "cashout");
        contentPanel.add(txnPanel,      "txn");
        contentPanel.add(settingsPanel, "settings");

        return contentPanel;
    }

    // ── Navigation bar ────────────────────────────────────────
    private JPanel buildNavBar() {
        JPanel nav = new JPanel(new GridLayout(1, 6));
        nav.setBackground(BRAND_DARK);
        nav.setBorder(new EmptyBorder(4, 0, 6, 0));
        nav.setPreferredSize(new Dimension(0, 64));

        btnNavHome     = navButton("🏠", "Home");
        btnNavCashIn   = navButton("📥", "Cash In");
        btnNavCashOut  = navButton("📤", "Cash Out");
        btnNavTxn      = navButton("↔", "TXN");
        btnNavSettings = navButton("⚙", "Settings");
        btnNavLogout   = navButton("🚪", "Log Out");

        btnNavHome.addActionListener(e -> showCard("home"));
        btnNavCashIn.addActionListener(e -> showCard("cashin"));
        btnNavCashOut.addActionListener(e -> showCard("cashout"));
        btnNavTxn.addActionListener(e -> { refreshTxnPanel(); showCard("txn"); });
        btnNavSettings.addActionListener(e -> showCard("settings"));
        btnNavLogout.addActionListener(e -> logout());

        nav.add(btnNavHome);
        nav.add(btnNavCashIn);
        nav.add(btnNavCashOut);
        nav.add(btnNavTxn);
        nav.add(btnNavSettings);
        nav.add(btnNavLogout);
        return nav;
    }

    // ═══════════════════════════════════════════════════════════
    //  HOME PANEL
    // ═══════════════════════════════════════════════════════════
    private JPanel buildHomePanel() {
        JPanel p = new JPanel();
        p.setBackground(BRAND_LIGHT);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(20, 24, 20, 24));

        // ── Balance Card ──────────────────────────────────────
        JPanel balCard = new JPanel();
        balCard.setBackground(BRAND_DARK);
        balCard.setLayout(new BoxLayout(balCard, BoxLayout.Y_AXIS));
        balCard.setBorder(new EmptyBorder(20, 24, 20, 24));
        balCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        balCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblBalLbl = new JLabel("BALANCE");
        lblBalLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblBalLbl.setForeground(new Color(180, 215, 240));
        lblBalLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblBalanceHome = new JLabel(formatPeso(currentUser.getBalance()));
        lblBalanceHome.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblBalanceHome.setForeground(WHITE);
        lblBalanceHome.setAlignmentX(Component.LEFT_ALIGNMENT);

        balCard.add(lblBalLbl);
        balCard.add(Box.createVerticalStrut(6));
        balCard.add(lblBalanceHome);

        // ── Profile card ──────────────────────────────────────
        JPanel profileCard = new JPanel(new GridLayout(3, 1, 4, 4));
        profileCard.setBackground(WHITE);
        profileCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 220, 240), 1),
                "🏠 Home – Profile Information",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), BRAND_DARK),
            new EmptyBorder(8, 12, 8, 12)));
        profileCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        profileCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        profileCard.add(infoLabel("👤 Name", currentUser.getName()));
        profileCard.add(infoLabel("📱 Number", currentUser.getNumber()));
        profileCard.add(infoLabel("✉ Email", currentUser.getEmail()));

        // ── Recent Txn (last 5) ───────────────────────────────
        String[] cols = {"Date", "Details", "Amount"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable recentTable = new JTable(model);
        styleTable(recentTable);
        JScrollPane sp = new JScrollPane(recentTable);
        sp.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 220, 240)),
            "Recent Transactions",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 13), BRAND_DARK));
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        // Load last 5 txns
        loadRecentTransactions(model, 5);

        p.add(balCard);
        p.add(Box.createVerticalStrut(14));
        p.add(profileCard);
        p.add(Box.createVerticalStrut(14));
        p.add(sp);

        return p;
    }

    private void loadHomePanel() {
        // refresh balance label from DB
        double fresh = UserAuthentication.refreshBalance(currentUser.getNumber());
        currentUser.setBalance(fresh);
        if (lblBalanceHome != null)
            lblBalanceHome.setText(formatPeso(currentUser.getBalance()));
    }

    // ═══════════════════════════════════════════════════════════
    //  CASH IN PANEL
    // ═══════════════════════════════════════════════════════════
    private JPanel buildCashInPanel() {
        JPanel p = formPanel("📥 Cash In");

        JLabel lblAmt = makeLabel("Enter amount to cash in:");
        JTextField tfAmount = formTextField("Numeric values only");

        JLabel lblHint = new JLabel("Make sure the value is numeric only.");
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblHint.setForeground(Color.GRAY);
        lblHint.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnSubmit = createPrimaryButton("SUBMIT");
        JButton btnCancel = createOutlineButton("CANCEL");

        btnSubmit.addActionListener(e -> {
            String input = tfAmount.getText().trim();
            if (input.isEmpty()) { showError("Please enter an amount."); return; }
            double amount;
            try { amount = Double.parseDouble(input); }
            catch (NumberFormatException ex) { showError("Invalid amount."); return; }
            if (amount <= 0) { showError("Amount must be greater than 0."); return; }

            boolean ok = UserAuthentication.cashIn(currentUser, amount);
            if (ok) {
                JOptionPane.showMessageDialog(this,
                    "Cash In successful!\nNew Balance: " + formatPeso(currentUser.getBalance()),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                tfAmount.setText("");
                loadHomePanel();
            } else {
                showError("Cash In failed. Try again.");
            }
        });

        btnCancel.addActionListener(e -> { tfAmount.setText(""); showCard("home"); });

        p.add(lblAmt);
        p.add(Box.createVerticalStrut(6));
        p.add(tfAmount);
        p.add(Box.createVerticalStrut(4));
        p.add(lblHint);
        p.add(Box.createVerticalStrut(20));
        p.add(btnSubmit);
        p.add(Box.createVerticalStrut(10));
        p.add(btnCancel);
        return p;
    }

    // ═══════════════════════════════════════════════════════════
    //  CASH OUT / SEND MONEY PANEL
    // ═══════════════════════════════════════════════════════════
    private JPanel buildCashOutPanel() {
        JPanel p = formPanel("📤 Cash Out / Send Money");

        JLabel lblRecipient = makeLabel("Recipient Mobile Number:");
        JTextField tfRecipient = formTextField("09XXXXXXXXX");

        JLabel lblAmt = makeLabel("Enter cash out amount:");
        JTextField tfAmount = formTextField("Numeric values only");

        JLabel lblHint = new JLabel("Make sure the value is numeric only.");
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblHint.setForeground(Color.GRAY);
        lblHint.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnSubmit = createPrimaryButton("SUBMIT");
        JButton btnCancel = createOutlineButton("CANCEL");

        btnSubmit.addActionListener(e -> {
            String recipientNum = tfRecipient.getText().trim();
            String input        = tfAmount.getText().trim();

            if (recipientNum.isEmpty() || input.isEmpty()) {
                showError("Please fill in all fields."); return;
            }
            if (!recipientNum.matches("09\\d{9}")) {
                showError("Invalid recipient number."); return;
            }
            if (recipientNum.equals(currentUser.getNumber())) {
                showError("You cannot send money to yourself."); return;
            }
            if (!UserAuthentication.numberExists(recipientNum)) {
                showError("Recipient number not found."); return;
            }

            double amount;
            try { amount = Double.parseDouble(input); }
            catch (NumberFormatException ex) { showError("Invalid amount."); return; }
            if (amount <= 0) { showError("Amount must be greater than 0."); return; }
            if (amount > currentUser.getBalance()) {
                showError("Insufficient balance."); return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                "Send " + formatPeso(amount) + " to " + recipientNum + "?",
                "Confirm Transfer", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            boolean ok = UserAuthentication.sendMoney(currentUser, recipientNum, amount);
            if (ok) {
                JOptionPane.showMessageDialog(this,
                    "Transfer successful!\nNew Balance: " + formatPeso(currentUser.getBalance()),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                tfRecipient.setText("");
                tfAmount.setText("");
                loadHomePanel();
            } else {
                showError("Transfer failed. Try again.");
            }
        });

        btnCancel.addActionListener(e -> {
            tfRecipient.setText(""); tfAmount.setText(""); showCard("home");
        });

        p.add(lblRecipient);
        p.add(Box.createVerticalStrut(6));
        p.add(tfRecipient);
        p.add(Box.createVerticalStrut(14));
        p.add(lblAmt);
        p.add(Box.createVerticalStrut(6));
        p.add(tfAmount);
        p.add(Box.createVerticalStrut(4));
        p.add(lblHint);
        p.add(Box.createVerticalStrut(20));
        p.add(btnSubmit);
        p.add(Box.createVerticalStrut(10));
        p.add(btnCancel);
        return p;
    }

    // ═══════════════════════════════════════════════════════════
    //  TRANSACTION HISTORY PANEL
    // ═══════════════════════════════════════════════════════════
    private DefaultTableModel txnModel;

    private JPanel buildTxnPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BRAND_LIGHT);
        p.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel("↔  Transaction History");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(BRAND_DARK);
        title.setBorder(new EmptyBorder(0, 0, 12, 0));

        String[] cols = {"Date", "Details", "Amount"};
        txnModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(txnModel);
        styleTable(table);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(200, 220, 240)));

        p.add(title, BorderLayout.NORTH);
        p.add(sp,    BorderLayout.CENTER);
        return p;
    }

    private void refreshTxnPanel() {
        if (txnModel == null) return;
        txnModel.setRowCount(0);
        loadRecentTransactions(txnModel, 0); // 0 = all
    }

    // ═══════════════════════════════════════════════════════════
    //  SETTINGS PANEL
    // ═══════════════════════════════════════════════════════════
    private JPanel buildSettingsPanel() {
        JPanel p = formPanel("⚙ Settings");

        JLabel lblSection = new JLabel("Change MPIN");
        lblSection.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSection.setForeground(BRAND_DARK);
        lblSection.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField pfOld     = passwordField("Current MPIN");
        JPasswordField pfNew     = passwordField("New 6-digit MPIN");
        JPasswordField pfConfirm = passwordField("Confirm New MPIN");
        JLabel lblErr = new JLabel(" ");
        lblErr.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblErr.setForeground(new Color(200, 30, 30));
        lblErr.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnChange = createPrimaryButton("CHANGE MPIN");

        btnChange.addActionListener(e -> {
            String old  = new String(pfOld.getPassword()).trim();
            String nw   = new String(pfNew.getPassword()).trim();
            String conf = new String(pfConfirm.getPassword()).trim();

            if (old.isEmpty() || nw.isEmpty() || conf.isEmpty()) {
                lblErr.setText("⚠ Fill in all MPIN fields."); return;
            }
            if (!nw.matches("\\d{6}")) {
                lblErr.setText("⚠ New MPIN must be exactly 4 digits."); return;
            }
            if (!nw.equals(conf)) {
                lblErr.setText("⚠ New MPINs do not match."); return;
            }

            boolean ok = UserAuthentication.changePin(currentUser.getNumber(), old, nw);
            if (ok) {
                currentUser.setPin(nw);
                lblErr.setText(" ");
                pfOld.setText(""); pfNew.setText(""); pfConfirm.setText("");
                JOptionPane.showMessageDialog(this, "MPIN changed successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                lblErr.setText("⚠ Current MPIN is incorrect.");
            }
        });

        p.add(lblSection);
        p.add(Box.createVerticalStrut(12));
        p.add(makeLabel("Current MPIN"));
        p.add(Box.createVerticalStrut(6));
        p.add(pfOld);
        p.add(Box.createVerticalStrut(12));
        p.add(makeLabel("New MPIN"));
        p.add(Box.createVerticalStrut(6));
        p.add(pfNew);
        p.add(Box.createVerticalStrut(12));
        p.add(makeLabel("Confirm New MPIN"));
        p.add(Box.createVerticalStrut(6));
        p.add(pfConfirm);
        p.add(Box.createVerticalStrut(8));
        p.add(lblErr);
        p.add(Box.createVerticalStrut(16));
        p.add(btnChange);
        return p;
    }

    // ═══════════════════════════════════════════════════════════
    //  HELPERS
    // ═══════════════════════════════════════════════════════════
    private void showCard(String name) {
        if ("home".equals(name)) loadHomePanel();
        cardLayout.show(contentPanel, name);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to log out?",
            "Log Out", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> new MainDashboard().setVisible(true));
        }
    }

    private void loadRecentTransactions(DefaultTableModel model, int limit) {
        String sql = "SELECT date, type, amount FROM transactions " +
                     "WHERE user_id = ? ORDER BY date DESC" +
                     (limit > 0 ? " LIMIT " + limit : "");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentUser.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("date"),
                    rs.getString("type"),
                    formatPeso(rs.getDouble("amount"))
                });
            }
        } catch (SQLException e) {
            System.err.println("[AccountDashboard] LoadTxn error: " + e.getMessage());
        }
    }

    private String formatPeso(double amount) {
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("en", "PH"));
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        return "₱" + nf.format(amount);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ── Component factories ───────────────────────────────────
    private JPanel formPanel(String title) {
        JPanel p = new JPanel();
        p.setBackground(WHITE);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(28, 30, 20, 30));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setForeground(BRAND_DARK);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lbl);
        p.add(Box.createVerticalStrut(20));
        return p;
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(BRAND_DARK);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JLabel infoLabel(String key, String value) {
        JLabel lbl = new JLabel(key + ":  " + value);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(new Color(40, 60, 80));
        return lbl;
    }

    private JTextField formTextField(String tip) {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.setToolTipText(tip);
        return tf;
    }

    private JPasswordField passwordField(String tip) {
        JPasswordField pf = new JPasswordField();
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        pf.setAlignmentX(Component.LEFT_ALIGNMENT);
        pf.setToolTipText(tip);
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

    private JButton navButton(String icon, String label) {
        JButton btn = new JButton("<html><center>" + icon + "<br><small>" + label + "</small></center></html>");
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 11));
        btn.setForeground(new Color(180, 215, 240));
        btn.setBackground(BRAND_DARK);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(26);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(BRAND_DARK);
        table.getTableHeader().setForeground(WHITE);
        table.setSelectionBackground(BRAND_LIGHT);
        table.setGridColor(new Color(220, 230, 240));
    }
}
