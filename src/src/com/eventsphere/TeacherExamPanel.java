package com.eventsphere;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class TeacherExamPanel extends JPanel {
    public TeacherExamPanel(String username) {
        setLayout(new GridLayout(6, 2, 10, 6));
        setBackground(new Color(220, 236, 209));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        JLabel lTitle = new JLabel("Exam Name:");
        JTextField tTitle = new JTextField();
        JLabel lDate = new JLabel("Date (YYYY-MM-DD):");
        JTextField tDate = new JTextField();
        JLabel lTime = new JLabel("Time (HH:MM):");
        JTextField tTime = new JTextField();
        JLabel lLoc = new JLabel("Room/Hall:");
        JTextField tLoc = new JTextField();
        JLabel lSeats = new JLabel("No. of Seats:");
        JTextField tSeats = new JTextField();

        JButton postBtn = new JButton("Post Exam Schedule");

        add(lTitle); add(tTitle);
        add(lDate); add(tDate);
        add(lTime); add(tTime);
        add(lLoc); add(tLoc);
        add(lSeats); add(tSeats);
        add(postBtn); add(new JLabel(""));

        postBtn.addActionListener(e -> {
            if (tTitle.getText().isEmpty() || tDate.getText().isEmpty() || tSeats.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!");
                return;
            }
            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/eventsphere_pro", "root", "Tiger_1309")) {
                Statement st = conn.createStatement();
                ResultSet rsU = st.executeQuery("SELECT id FROM users WHERE username='" + username + "'");
                int tid = -1;
                if (rsU.next()) tid = rsU.getInt("id");
                if (tid == -1) return;
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO events (title, event_date, event_time, location, event_type, created_by, max_seats, category) VALUES (?, ?, ?, ?, 'EXAM', ?, ?, 'ACADEMIC')"
                );
                ps.setString(1, tTitle.getText().trim());
                ps.setString(2, tDate.getText().trim());
                ps.setString(3, tTime.getText().trim());
                ps.setString(4, tLoc.getText().trim());
                ps.setInt(5, tid);
                ps.setInt(6, Integer.parseInt(tSeats.getText().trim()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Exam scheduled!");
                tTitle.setText(""); tDate.setText(""); tTime.setText("");
                tLoc.setText(""); tSeats.setText("");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
    }
}
