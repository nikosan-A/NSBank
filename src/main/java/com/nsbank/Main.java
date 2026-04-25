package com.nsbank;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

/**
 * Main – application entry point.
 * Applies FlatLaf theme, then opens the Main Dashboard.
 */
public class Main {
    public static void main(String[] args) {
        // Apply FlatLaf modern Look & Feel
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 10);
        } catch (Exception e) {
            System.err.println("Failed to apply FlatLaf theme: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            MainDashboard dashboard = new MainDashboard();
            dashboard.setVisible(true);
        });
    }
}
