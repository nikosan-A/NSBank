package com.nsbank;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/**
 * AdminDashboard – admin-only panel.
 *
 * Dropdown options:
 *   - View all users
 *   - View all transactions
 *   - View transactions per user
 */
public class AdminDashboard extends JFrame {

    private static final Color BRAND_DARK   = new Color(10, 61, 98);
    private static final Color BRAND_ACCENT = new Color(0, 150, 199);
    private static final Color BRAND_LIGHT  = new Color(232, 246, 253);
    private static final Color WHITE        = Color.WHITE;

    private final User adminUser;

    // ── Components ────────────────────────────────────────────
    private JComboBox<String> cmbView;
    private JComboBox<String> cmbUserFilter;
    private JTable            table;
    private DefaultTableModel tableModel;
    private JPanel            filterPanel;

    public AdminDashboard(User adminUser) {
        this.adminUser = adminUser;
        initUI();
        setTitle("NSBank – Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(700, 560));
        setPreferredSize(new Dimension(780, 600));
        pack();
        setLocationRelativeTo(null);

        // Default view
        loadAllUsers();
    }

    // ═══════════════════════════════════════════════════════════
    //  UI BUILD
    // ═══════════════════════════════════════════════════════════
    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BRAND_LIGHT);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildBody(),   BorderLayout.CENTER);

        setContentPane(root);
    }

    // ── Header ────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BRAND_DARK);
        header.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel lblTitle = new JLabel("WELCOME, ADMIN!");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(WHITE);

        JButton btnLogout = new JButton("🚪 Log Out");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setForeground(WHITE);
        btnLogout.setBackground(new Color(192, 57, 43));
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new MainDashboard().setVisible(true));
        });

        header.add(lblTitle,   BorderLayout.WEST);
        header.add(btnLogout,  BorderLayout.EAST);
        return header;
    }

    // ── Body ──────────────────────────────────────────────────
    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setBackground(BRAND_LIGHT);
        body.setBorder(new EmptyBorder(16, 20, 20, 20));

        body.add(buildControls(), BorderLayout.NORTH);
        body.add(buildTable(),    BorderLayout.CENTER);
        return body;
    }

    // ── Controls row ──────────────────────────────────────────
    private JPanel buildControls() {
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        controls.setOpaque(false);

        cmbView = new JComboBox<>(new String[]{
            "View all users",
            "View all transactions",
            "View transactions per user"
        });
        cmbView.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbView.setPreferredSize(new Dimension(220, 36));

        // User filter combo (visible only for "per user" mode)
        cmbUserFilter = new JComboBox<>();
        cmbUserFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbUserFilter.setPreferredSize(new Dimension(180, 36));
        loadUserFilterOptions();

        filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        filterPanel.setOpaque(false);
        filterPanel.add(cmbUserFilter);
        filterPanel.setVisible(false);

        cmbView.addActionListener(e -> {
            String choice = (String) cmbView.getSelectedItem();
            if (choice == null) return;
            switch (choice) {
                case "View all users":
                    filterPanel.setVisible(false);
                    loadAllUsers();
                    break;
                case "View all transactions":
                    filterPanel.setVisible(false);
                    loadAllTransactions();
                    break;
                case "View transactions per user":
                    filterPanel.setVisible(true);
                    loadTransactionsForSelectedUser();
                    break;
            }
        });

        cmbUserFilter.addActionListener(e -> {
            if ("View transactions per user".equals(cmbView.getSelectedItem())) {
                loadTransactionsForSelectedUser();
            }
        });

        controls.add(cmbView);
        controls.add(filterPanel);
        return controls;
    }

    // ── Table ─────────────────────────────────────────────────
    private JScrollPane buildTable() {
        tableModel = new DefaultTableModel() {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(26);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(BRAND_DARK);
        table.getTableHeader().setForeground(WHITE);
        table.setSelectionBackground(BRAND_LIGHT);
        table.setGridColor(new Color(220, 230, 240));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 220, 240), 1),
            "Records",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 13), BRAND_DARK));
        return sp;
    }

    // ═══════════════════════════════════════════════════════════
    //  DATA LOADERS
    // ═══════════════════════════════════════════════════════════
    private void loadAllUsers() {
        setColumns("Name", "Email", "Number", "Balance", "Role");
        tableModel.setRowCount(0);
        String sql = "SELECT name, email, number, balance, role FROM users ORDER BY name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("number"),
                    "₱" + String.format("%,.2f", rs.getDouble("balance")),
                    rs.getString("role")
                });
            }
        } catch (SQLException e) {
            System.err.println("[AdminDashboard] LoadAllUsers: " + e.getMessage());
        }
    }

    private void loadAllTransactions() {
        setColumns("Date", "Name", "Type", "Amount");
        tableModel.setRowCount(0);
        String sql = "SELECT t.date, u.name, t.type, t.amount " +
                     "FROM transactions t " +
                     "JOIN users u ON t.user_id = u.id " +
                     "ORDER BY t.date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("date"),
                    rs.getString("name"),
                    rs.getString("type"),
                    "₱" + String.format("%,.2f", rs.getDouble("amount"))
                });
            }
        } catch (SQLException e) {
            System.err.println("[AdminDashboard] LoadAllTxn: " + e.getMessage());
        }
    }

    private void loadTransactionsForSelectedUser() {
        String selected = (String) cmbUserFilter.getSelectedItem();
        if (selected == null || selected.isEmpty()) return;

        // extract number from "Name (09XXXXXXXXX)" format
        String number = selected.replaceAll(".*\\((.*)\\)", "$1");

        setColumns("Date", "Type", "Amount");
        tableModel.setRowCount(0);
        String sql = "SELECT t.date, t.type, t.amount " +
                     "FROM transactions t " +
                     "JOIN users u ON t.user_id = u.id " +
                     "WHERE u.number = ? ORDER BY t.date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, number);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("date"),
                    rs.getString("type"),
                    "₱" + String.format("%,.2f", rs.getDouble("amount"))
                });
            }
        } catch (SQLException e) {
            System.err.println("[AdminDashboard] LoadTxnPerUser: " + e.getMessage());
        }
    }

    private void loadUserFilterOptions() {
        cmbUserFilter.removeAllItems();
        String sql = "SELECT name, number FROM users WHERE role = 'user' ORDER BY name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cmbUserFilter.addItem(rs.getString("name") + " (" + rs.getString("number") + ")");
            }
        } catch (SQLException e) {
            System.err.println("[AdminDashboard] LoadUserFilter: " + e.getMessage());
        }
    }

    // ── Helper: set column headers ────────────────────────────
    private void setColumns(String... columns) {
        tableModel.setColumnCount(0);
        for (String col : columns) tableModel.addColumn(col);
    }
}
