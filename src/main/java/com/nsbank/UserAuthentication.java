package com.nsbank;

import java.sql.*;

/**
 * UserAuthentication – handles login, registration, pin change, and
 * balance operations against the MySQL database.
 */
public class UserAuthentication {

    // ── LOGIN ──────────────────────────────────────────────────────────────
    /**
     * Attempts login with mobile number + 6-digit MPIN.
     * @return User object on success, null on failure.
     */
    public static User login(String number, String pin) {
        String sql = "SELECT * FROM users WHERE number = ? AND pin = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, number.trim());
            ps.setString(2, pin.trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("[Auth] Login error: " + e.getMessage());
        }
        return null;
    }

    // ── REGISTER ───────────────────────────────────────────────────────────
    /**
     * Registers a brand-new user.
     * @return true on success, false if number/email already exists or DB error.
     */
    public static boolean register(String name, String email, String number, String pin) {
        if (numberExists(number) || emailExists(email)) return false;

        String sql = "INSERT INTO users (name, email, number, pin, balance, role) VALUES (?,?,?,?,0.00,'user')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setString(2, email.trim());
            ps.setString(3, number.trim());
            ps.setString(4, pin.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[Auth] Register error: " + e.getMessage());
        }
        return false;
    }

    // ── CHANGE PIN ─────────────────────────────────────────────────────────
    public static boolean changePin(String number, String oldPin, String newPin) {
        // Verify old pin first
        User u = login(number, oldPin);
        if (u == null) return false;

        String sql = "UPDATE users SET pin = ? WHERE number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPin.trim());
            ps.setString(2, number.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[Auth] ChangePin error: " + e.getMessage());
        }
        return false;
    }

    // ── CASH IN ────────────────────────────────────────────────────────────
    public static boolean cashIn(User user, double amount) {
        String sqlUpdate = "UPDATE users SET balance = balance + ? WHERE number = ?";
        String sqlTxn    = "INSERT INTO transactions (user_number, type, amount, date, user_id) VALUES (?, 'Cash In', ?, NOW(), ?)";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(sqlUpdate);
                 PreparedStatement ps2 = conn.prepareStatement(sqlTxn)) {
                ps1.setDouble(1, amount);
                ps1.setString(2, user.getNumber());
                ps1.executeUpdate();

                ps2.setString(1, user.getNumber());
                ps2.setDouble(2, amount);
                ps2.setInt(3, user.getId());
                ps2.executeUpdate();

                conn.commit();
                user.setBalance(user.getBalance() + amount);
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                System.err.println("[Auth] CashIn rollback: " + ex.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("[Auth] CashIn error: " + e.getMessage());
        }
        return false;
    }

    // ── CASH OUT / SEND MONEY ──────────────────────────────────────────────
    /**
     * Transfers money from `sender` to the user with `recipientNumber`.
     * @return true on success.
     */
    public static boolean sendMoney(User sender, String recipientNumber, double amount) {
        if (sender.getBalance() < amount) return false;

        // Fetch recipient
        User recipient = getUserByNumber(recipientNumber);
        if (recipient == null) return false;

        String sqlDebit  = "UPDATE users SET balance = balance - ? WHERE number = ?";
        String sqlCredit = "UPDATE users SET balance = balance + ? WHERE number = ?";
        String sqlTxnOut = "INSERT INTO transactions (user_number, type, amount, date, user_id) VALUES (?, ?, ?, NOW(), ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement debit  = conn.prepareStatement(sqlDebit);
                 PreparedStatement credit = conn.prepareStatement(sqlCredit);
                 PreparedStatement txnOut = conn.prepareStatement(sqlTxnOut);
                 PreparedStatement txnIn  = conn.prepareStatement(sqlTxnOut)) {

                debit.setDouble(1, amount);
                debit.setString(2, sender.getNumber());
                debit.executeUpdate();

                credit.setDouble(1, amount);
                credit.setString(2, recipientNumber);
                credit.executeUpdate();

                txnOut.setString(1, sender.getNumber());
                txnOut.setString(2, "Transfer to " + recipientNumber);
                txnOut.setDouble(3, amount);
                txnOut.setInt(4, sender.getId());
                txnOut.executeUpdate();

                txnIn.setString(1, recipientNumber);
                txnIn.setString(2, "Received from " + sender.getNumber());
                txnIn.setDouble(3, amount);
                txnIn.setInt(4, recipient.getId());
                txnIn.executeUpdate();

                conn.commit();
                sender.setBalance(sender.getBalance() - amount);
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                System.err.println("[Auth] SendMoney rollback: " + ex.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("[Auth] SendMoney error: " + e.getMessage());
        }
        return false;
    }

    // ── REFRESH USER BALANCE ───────────────────────────────────────────────
    public static double refreshBalance(String number) {
        String sql = "SELECT balance FROM users WHERE number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, number);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("balance");
        } catch (SQLException e) {
            System.err.println("[Auth] RefreshBalance error: " + e.getMessage());
        }
        return 0;
    }

    // ── HELPERS ────────────────────────────────────────────────────────────
    public static User getUserByNumber(String number) {
        String sql = "SELECT * FROM users WHERE number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, number.trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (SQLException e) {
            System.err.println("[Auth] GetUserByNumber error: " + e.getMessage());
        }
        return null;
    }

    public static boolean numberExists(String number) {
        return getUserByNumber(number) != null;
    }

    public static boolean emailExists(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("[Auth] EmailExists error: " + e.getMessage());
        }
        return false;
    }

    private static User mapUser(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("number"),
            rs.getString("pin"),
            rs.getDouble("balance"),
            rs.getString("role")
        );
    }
}
