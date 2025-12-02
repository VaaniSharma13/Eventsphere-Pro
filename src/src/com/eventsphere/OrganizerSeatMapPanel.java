package com.eventsphere;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;
public class OrganizerSeatMapPanel extends JPanel {
    private JComboBox<String> eventBox;
    private JTable seatTable;
    private String[] eventIds;
    private JButton updateBtn;

    public OrganizerSeatMapPanel(String username) {
        setLayout(new BorderLayout(10,10));
        setBackground(new Color(245,245,255));

        JLabel heading = new JLabel("Seat Allotment & Visualization", SwingConstants.CENTER);
        heading.setFont(new Font("Serif", Font.BOLD, 22));
        heading.setForeground(new Color(34, 139, 34));
        add(heading, BorderLayout.NORTH);

        eventBox = new JComboBox<>();
        fillEvents(username);
        add(eventBox, BorderLayout.WEST);

        seatTable = new JTable();
        JScrollPane scroll = new JScrollPane(seatTable);
        add(scroll, BorderLayout.CENTER);

        updateBtn = new JButton("Update Seat Assignment");
        updateBtn.setForeground(new Color(75, 0, 130));
        add(updateBtn, BorderLayout.SOUTH);

        eventBox.addActionListener(e -> loadSeats());
        updateBtn.addActionListener(e -> updateSeatAssignment());

        if (eventBox.getItemCount() > 0) loadSeats();
    }
    private void fillEvents(String username) {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/eventsphere_pro", "root", "Tiger_1309")) {
            Statement st = conn.createStatement();
            ResultSet rsU = st.executeQuery("SELECT id FROM users WHERE username='" + username + "'");
            int oid = -1;
            if (rsU.next()) oid = rsU.getInt("id");
            if (oid == -1) return;
            ResultSet rs = st.executeQuery(
                    "SELECT id, title FROM events WHERE created_by=" + oid + " ORDER BY event_date DESC"
            );
            List<String> ids = new ArrayList<>();
            while (rs.next()) {
                eventBox.addItem(rs.getString(2)); // title
                ids.add(rs.getString(1));
            }
            eventIds = ids.toArray(new String[0]);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading events: " + ex.getMessage());
        }
    }
    private void loadSeats() {
        if (eventBox.getSelectedIndex() == -1) return;
        String eventId = eventIds[eventBox.getSelectedIndex()];
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/eventsphere_pro", "root", "Tiger_1309")) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT u.username, r.seat_number, r.id FROM registrations r " +
                "JOIN users u ON r.user_id = u.id WHERE r.event_id = ? ORDER BY r.seat_number"
            );
            ps.setString(1, eventId);
            ResultSet rs = ps.executeQuery();
            Vector<String> cols = new Vector<>(Arrays.asList("Username", "Seat Number", "RegID"));
            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString(1));
                row.add(rs.getObject(2));
                row.add(rs.getInt(3)); 
                data.add(row);
            }
            seatTable.setModel(new javax.swing.table.DefaultTableModel(data, cols) {
                @Override
                public boolean isCellEditable(int row, int col) {
                    return getColumnName(col).equals("Seat Number");
                }
            });
            // Hide the RegID
            if(seatTable.getColumnCount() == 3) {
                seatTable.removeColumn(seatTable.getColumnModel().getColumn(2)); 
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading seats: " + ex.getMessage());
        }
    }
    private void updateSeatAssignment() {
        if (eventBox.getSelectedIndex() == -1) return;
        String eventId = eventIds[eventBox.getSelectedIndex()];
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/eventsphere_pro", "root", "Tiger_1309")) {
            for (int i = 0; i < seatTable.getRowCount(); ++i) {
                Object seatObj = seatTable.getValueAt(i, 1);
                String username = seatTable.getValueAt(i, 0).toString();
                ResultSet rsUid = conn.createStatement().executeQuery("SELECT id FROM users WHERE username='" + username + "'");
                int uid = -1;
                if (rsUid.next()) uid = rsUid.getInt(1);

                PreparedStatement ps2 = conn.prepareStatement(
                    "UPDATE registrations SET seat_number = ? WHERE event_id = ? AND user_id = ?"
                );
                if (seatObj == null || seatObj.toString().isEmpty())
                    ps2.setNull(1, Types.INTEGER);
                else
                    ps2.setInt(1, Integer.parseInt(seatObj.toString()));
                ps2.setString(2, eventId);
                ps2.setInt(3, uid);
                ps2.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Seats updated!");
            loadSeats();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating seats: " + ex.getMessage());
        }
    }
}
