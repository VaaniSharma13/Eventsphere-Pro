package com.eventsphere;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddPersonalEventPanel extends JPanel {
    public AddPersonalEventPanel(String username) {
        setLayout(new GridLayout(7, 2, 10, 10));
        setBackground(new Color(220, 236, 209));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel t1 = new JLabel("Title:");
        t1.setForeground(new Color(34, 139, 34));
        add(t1);
        JTextField tf1 = new JTextField();
        add(tf1);

        JLabel t2 = new JLabel("Date (YYYY-MM-DD):");
        t2.setForeground(new Color(34, 139, 34));
        add(t2);
        JTextField tf2 = new JTextField("2025-12-01");
        add(tf2);

        JLabel t3 = new JLabel("Time (HH:MM):");
        t3.setForeground(new Color(34, 139, 34));
        add(t3);
        JTextField tf3 = new JTextField("14:00");
        add(tf3);

        JLabel t4 = new JLabel("Location:");
        t4.setForeground(new Color(34, 139, 34));
        add(t4);
        JTextField tf4 = new JTextField();
        add(tf4);

        JLabel t5 = new JLabel("Category:");
        t5.setForeground(new Color(34, 139, 34));
        add(t5);
        String[] cats = {"ACADEMIC", "ENTERTAINMENT", "MISCELLANEOUS"};
        JComboBox<String> cb5 = new JComboBox<>(cats);
        add(cb5);

        JButton addEvent = new JButton("Add Event");
        addEvent.setBackground(Color.WHITE);
        addEvent.setForeground(new Color(0, 51, 204)); // Blue!
        addEvent.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(addEvent);
        add(new JLabel(""));

        addEvent.addActionListener(e -> {
            if (tf1.getText().trim().isEmpty() || tf2.getText().trim().isEmpty() ||
                tf3.getText().trim().isEmpty() || tf4.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!");
                return;
            }

            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/eventsphere_pro", "root", "Tiger_1309")) {
                // 1. Find user ID
                Statement st = conn.createStatement();
                ResultSet rsId = st.executeQuery(
                        "SELECT id FROM users WHERE username='" + username + "'"
                );
                int uid = -1;
                if (rsId.next()) uid = rsId.getInt("id");
                if (uid == -1) {
                    JOptionPane.showMessageDialog(this, "User not found!");
                    return;
                }
                // 2. Insert event
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO events (title, event_date, event_time, location, category, event_type, created_by, requires_registration, completed, description) VALUES (?, ?, ?, ?, ?, 'EVENT', ?, FALSE, FALSE, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, tf1.getText().trim());
                ps.setString(2, tf2.getText().trim());
                ps.setString(3, tf3.getText().trim());
                ps.setString(4, tf4.getText().trim());
                ps.setString(5, (String) cb5.getSelectedItem());
                ps.setInt(6, uid);
                ps.setString(7, "Personal Event");

                int result = ps.executeUpdate();

                if (result > 0) {
                    // 3. Register user for their event
                    ResultSet generatedKeys = ps.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int eventId = generatedKeys.getInt(1);
                        PreparedStatement regPs = conn.prepareStatement(
                                "INSERT INTO registrations (user_id, event_id, status) VALUES (?, ?, 'APPROVED')"
                        );
                        regPs.setInt(1, uid);
                        regPs.setInt(2, eventId);
                        regPs.executeUpdate();
                    }
                    JOptionPane.showMessageDialog(this, "Event Added Successfully!");
                    tf1.setText("");
                    tf2.setText("2025-12-01");
                    tf3.setText("14:00");
                    tf4.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add event!");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
    }
}
