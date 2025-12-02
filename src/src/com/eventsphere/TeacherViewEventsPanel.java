package com.eventsphere;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class TeacherViewEventsPanel extends JPanel {
    public JTable eventsTable;

    public TeacherViewEventsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(199, 221, 178));

        eventsTable = new JTable();
        JScrollPane scroll = new JScrollPane(eventsTable);
        add(scroll, BorderLayout.CENTER);
        loadAllEvents();
    }

    public void loadAllEvents() {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/eventsphere_pro", "root", "Tiger_1309")) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(
                "SELECT id, title, event_date, event_time, category, created_by, max_seats FROM events ORDER BY event_date DESC"
            );
            Vector<String> cols = new Vector<>();
            cols.add("Event ID"); cols.add("Title"); cols.add("Date"); cols.add("Time");
            cols.add("Category"); cols.add("Organizer"); cols.add("Max Seats");
            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= cols.size(); i++) row.add(rs.getObject(i));
                data.add(row);
            }
            eventsTable.setModel(new javax.swing.table.DefaultTableModel(data, cols));
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading events: " + ex.getMessage());
        }
    }

    public int getSelectedEventId() {
        int row = eventsTable.getSelectedRow();
        if (row == -1) return -1;
        return Integer.parseInt(eventsTable.getValueAt(row, 0).toString());
    }
}
