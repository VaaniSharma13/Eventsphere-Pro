package com.eventsphere;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class TeacherViewAssignmentsPanel extends JPanel {
    private JTable table;

    public TeacherViewAssignmentsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 245, 220));

        JLabel heading = new JLabel("All Posted Assignments", SwingConstants.CENTER);
        heading.setFont(new Font("Serif", Font.BOLD, 22));
        heading.setForeground(new Color(75, 0, 130));
        add(heading, BorderLayout.NORTH);

        table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadAssignments());
        add(refreshBtn, BorderLayout.SOUTH);

        loadAssignments();
    }

    private void loadAssignments() {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/eventsphere_pro", "root", "Tiger_1309")) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(
                "SELECT a.id, a.title, a.description, a.due_date, u.username AS posted_by " +
                "FROM assignments a JOIN users u ON a.posted_by = u.id ORDER BY a.due_date"
            );
            Vector<String> cols = new Vector<>();
            cols.add("ID"); cols.add("Title"); cols.add("Description");
            cols.add("Due Date"); cols.add("Posted By");
            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("title"));
                row.add(rs.getString("description"));
                row.add(rs.getDate("due_date"));
                row.add(rs.getString("posted_by"));
                data.add(row);
            }
            table.setModel(new javax.swing.table.DefaultTableModel(data, cols));
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading assignments: " + ex.getMessage());
        }
    }
}
