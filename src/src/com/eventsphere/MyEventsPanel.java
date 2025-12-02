package com.eventsphere;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class MyEventsPanel extends JPanel {
    private JTable eventsTable;

    public MyEventsPanel(String username) {
        setLayout(new BorderLayout(10,10));
        setBackground(new Color(199, 221, 178));

        JLabel heading = new JLabel("My Events (All Registered)", SwingConstants.CENTER);
        heading.setFont(new Font("Serif", Font.BOLD, 22));
        heading.setForeground(new Color(34, 139, 34));
        add(heading, BorderLayout.NORTH);

        eventsTable = new JTable();
        styleTable(eventsTable);
        JScrollPane scroll = new JScrollPane(eventsTable);
        add(scroll, BorderLayout.CENTER);

        JButton completeBtn = new JButton("Mark as Complete");
        completeBtn.setForeground(new Color(94, 67, 41));
        add(completeBtn, BorderLayout.EAST);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadTable(username));
        add(refreshBtn, BorderLayout.SOUTH);

        loadTable(username);

        completeBtn.addActionListener(e -> {
            int selected = eventsTable.getSelectedRow();
            if (selected == -1) {
                JOptionPane.showMessageDialog(this, "Select an event to mark as complete!");
                return;
            }
            String title = (String) eventsTable.getValueAt(selected, 0);
            String date = eventsTable.getValueAt(selected, 3).toString();

            int res = JOptionPane.showConfirmDialog(this,
                    "Mark event '" + title + "' as complete?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (res != JOptionPane.YES_OPTION) return;

            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/eventsphere_pro", "root", "Tiger_1309")) {
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE events SET completed=TRUE WHERE title=? AND event_date=?"
                );
                ps.setString(1, title);
                ps.setString(2, date);
                int updated = ps.executeUpdate();
                if (updated > 0) {
                    JOptionPane.showMessageDialog(this, "Event marked as complete!");
                    loadTable(username);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to mark as complete.");
                }
            } catch(Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
    }

    // Loads all events student has registered for, with seat no if assigned
    private void loadTable(String username) {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/eventsphere_pro", "root", "Tiger_1309")) {

            Statement st = conn.createStatement();
            ResultSet rsId = st.executeQuery(
                "SELECT id FROM users WHERE username='" + username + "'"
            );
            int uid = -1;
            if (rsId.next()) uid = rsId.getInt("id");
            if (uid == -1) return;

            String sql =
                "SELECT e.title, e.category, e.event_type, e.event_date, e.event_time, e.location, e.completed, r.seat_number " +
                "FROM events e JOIN registrations r ON e.id = r.event_id " +
                "WHERE r.user_id = ? AND r.status = 'APPROVED' " +
                "ORDER BY e.event_date DESC, e.event_time DESC";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, uid);

            ResultSet rs = ps.executeQuery();
            Vector<String> cols = new Vector<>();
            cols.add("Title"); cols.add("Category"); cols.add("Type"); cols.add("Date");
            cols.add("Time"); cols.add("Location"); cols.add("Completed"); cols.add("Seat No.");

            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString(1));    // Title
                row.add(rs.getString(2));    // Category
                row.add(rs.getString(3));    // Type
                row.add(rs.getDate(4));      // Date
                row.add(rs.getTime(5));      // Time
                row.add(rs.getString(6));    // Location
                row.add(rs.getBoolean(7) ? "✓" : ""); // Completed
                row.add(rs.getObject(8));    // Seat Number
                data.add(row);
            }
            eventsTable.setModel(new javax.swing.table.DefaultTableModel(data, cols));
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading events: " + ex.getMessage());
        }
    }

    private void styleTable(JTable t) {
        t.setForeground(new Color(94,67,41));
        t.setFont(new Font("SansSerif", Font.PLAIN, 15));
        t.setRowHeight(25);
    }
}
