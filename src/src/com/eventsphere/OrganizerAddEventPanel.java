package com.eventsphere;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class OrganizerAddEventPanel extends JPanel {
    public OrganizerAddEventPanel(String username) {
        setLayout(new GridLayout(11, 2, 10, 8));
        setBackground(new Color(220, 236, 209));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel[] labels = {
            new JLabel("Title:"), new JLabel("Description:"),
            new JLabel("Date (YYYY-MM-DD):"), new JLabel("Time (HH:MM):"),
            new JLabel("Location:"), new JLabel("Category:"),
            new JLabel("Type (CLASS/EXAM/EVENT):"), new JLabel("Max Seats:"),
            new JLabel("Registration Required:")
        };
        for (JLabel l : labels) l.setForeground(new Color(34,139,34));
        JTextField tfTitle = new JTextField();
        JTextArea tfDesc = new JTextArea(2, 16);
        JTextField tfDate = new JTextField("2025-12-01");
        JTextField tfTime = new JTextField("12:00");
        JTextField tfLoc = new JTextField();
        String[] cats = {"ACADEMIC", "ENTERTAINMENT", "MISCELLANEOUS"};
        JComboBox<String> cbCat = new JComboBox<>(cats);
        String[] types = {"CLASS", "EXAM", "EVENT"};
        JComboBox<String> cbType = new JComboBox<>(types);
        JTextField tfSeats = new JTextField("0");
        JCheckBox cbReg = new JCheckBox();
        cbReg.setSelected(true);

        add(labels[0]); add(tfTitle);
        add(labels[1]); add(new JScrollPane(tfDesc));
        add(labels[2]); add(tfDate);
        add(labels[3]); add(tfTime);
        add(labels[4]); add(tfLoc);
        add(labels[5]); add(cbCat);
        add(labels[6]); add(cbType);
        add(labels[7]); add(tfSeats);
        add(labels[8]); add(cbReg);

        JButton addEvent = new JButton("Create Event for Registration");
        addEvent.setForeground(new Color(75, 0, 130));
        add(addEvent); add(new JLabel(""));

        addEvent.addActionListener(e -> {
            // Validate input
            if (tfTitle.getText().trim().isEmpty() || tfDate.getText().trim().isEmpty() ||
                tfTime.getText().trim().isEmpty() || tfLoc.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!");
                return;
            }
            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/eventsphere_pro", "root", "Tiger_1309")) {
                // Organizer id (created_by)
                Statement st = conn.createStatement();
                ResultSet rsId = st.executeQuery(
                        "SELECT id FROM users WHERE username='" + username + "'"
                );
                int oid = -1;
                if (rsId.next()) oid = rsId.getInt("id");
                if (oid == -1) {
                    JOptionPane.showMessageDialog(this, "Organizer not found!");
                    return;
                }
                // Insert event
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO events (title, description, event_date, event_time, location, category, event_type, created_by, max_seats, requires_registration, completed) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, FALSE)"
                );
                ps.setString(1, tfTitle.getText().trim());
                ps.setString(2, tfDesc.getText().trim());
                ps.setString(3, tfDate.getText().trim());
                ps.setString(4, tfTime.getText().trim());
                ps.setString(5, tfLoc.getText().trim());
                ps.setString(6, (String) cbCat.getSelectedItem());
                ps.setString(7, (String) cbType.getSelectedItem());
                ps.setInt(8, oid);
                ps.setInt(9, Integer.parseInt(tfSeats.getText()));
                ps.setBoolean(10, cbReg.isSelected());
                int updated = ps.executeUpdate();
                if (updated > 0) {
                    JOptionPane.showMessageDialog(this, "Event created! Students can now register.");
                    tfTitle.setText("");
                    tfDesc.setText(""); tfDate.setText("2025-12-01");
                    tfTime.setText("12:00"); tfLoc.setText(""); tfSeats.setText("0");
                    cbCat.setSelectedIndex(0); cbType.setSelectedIndex(0); cbReg.setSelected(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create event.");
                }
            } catch(Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
    }
}
