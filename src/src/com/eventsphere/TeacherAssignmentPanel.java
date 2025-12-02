package com.eventsphere;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class TeacherAssignmentPanel extends JPanel {
    public TeacherAssignmentPanel(String username) {
        setLayout(new GridLayout(6, 2, 10, 6));
        setBackground(new Color(220, 236, 209));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        JLabel lTitle = new JLabel("Assignment Title:");
        JTextField tTitle = new JTextField();
        JLabel lDesc = new JLabel("Description:");
        JTextArea tDesc = new JTextArea(3, 20);
        JScrollPane descScroll = new JScrollPane(tDesc);
        JLabel lDue = new JLabel("Due Date (YYYY-MM-DD):");
        JTextField tDue = new JTextField();
        JButton postBtn = new JButton("Post Assignment");

        add(lTitle); add(tTitle);
        add(lDesc); add(descScroll);
        add(lDue); add(tDue);
        add(postBtn); add(new JLabel(""));

        postBtn.addActionListener(e -> {
            if (tTitle.getText().isEmpty() || tDue.getText().isEmpty() || tDesc.getText().isEmpty()) {
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
                    "INSERT INTO assignments (title, description, due_date, posted_by) VALUES (?, ?, ?, ?)"
                );
                ps.setString(1, tTitle.getText().trim());
                ps.setString(2, tDesc.getText().trim());
                ps.setString(3, tDue.getText().trim());
                ps.setInt(4, tid);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Assignment posted!");
                tTitle.setText(""); tDesc.setText(""); tDue.setText("");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
    }
}

