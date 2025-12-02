package com.eventsphere;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class BrowseEventsPanel extends JPanel {
    public BrowseEventsPanel(String username) {
        setLayout(new BorderLayout());
        setBackground(new Color(199, 221, 178));

        JLabel heading = new JLabel("Browse and Register For Events", SwingConstants.CENTER);
        heading.setFont(new Font("Serif", Font.BOLD, 22));
        heading.setForeground(new Color(34, 139, 34));
        add(heading, BorderLayout.NORTH);

        JTable eventTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(eventTable);
        add(scrollPane, BorderLayout.CENTER);

        // *** BROWN TABLE FONT ***
        eventTable.setForeground(new Color(94, 67, 41)); // Brown
        eventTable.setFont(new Font("SansSerif", Font.PLAIN, 15));

        JButton registerBtn = new JButton("Register for Selected Event");
        registerBtn.setForeground(new Color(34,139,34));
        add(registerBtn, BorderLayout.SOUTH);

        loadTable(username, eventTable);

        registerBtn.addActionListener(e -> {
            int row = eventTable.getSelectedRow();
            if(row == -1) {
                JOptionPane.showMessageDialog(this, "Select an event to register!");
            } else {
                String title = (String) eventTable.getValueAt(row, 0);
                try (Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/eventsphere_pro", "root", "Tiger_1309");
                     Statement st = conn.createStatement()) {
                    ResultSet rsId = st.executeQuery(
                        "SELECT id FROM users WHERE username='" + username + "'"
                    );
                    int uid = -1; if (rsId.next()) uid = rsId.getInt("id");
                    int eid = -1;
                    ResultSet rsE = st.executeQuery("SELECT id FROM events WHERE title='" + title + "'");
                    if (rsE.next()) eid = rsE.getInt(1);
                    if (uid != -1 && eid != -1) {
                        PreparedStatement ps = conn.prepareStatement(
                            "INSERT IGNORE INTO registrations (user_id, event_id, status) VALUES (?, ?, 'APPROVED')"
                        );
                        ps.setInt(1, uid); ps.setInt(2, eid);
                        ps.executeUpdate();
                        JOptionPane.showMessageDialog(this, "Registered!");
                        loadTable(username, eventTable);
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        });
    }

    private void loadTable(String username, JTable table) {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/eventsphere_pro", "root", "Tiger_1309")) {
            Statement st = conn.createStatement();
            String sql = "SELECT title, category, event_type, event_date, event_time, location, max_seats " +
                    "FROM events WHERE requires_registration=TRUE AND completed=FALSE";
            ResultSet rs = st.executeQuery(sql);
            Vector<String> cols = new Vector<>();
            cols.add("Title"); cols.add("Category"); cols.add("Type"); cols.add("Date");
            cols.add("Time"); cols.add("Location"); cols.add("Seats");
            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for(int i=1;i<=7;i++) row.add(rs.getObject(i));
                data.add(row);
            }
            table.setModel(new javax.swing.table.DefaultTableModel(data, cols));
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}
