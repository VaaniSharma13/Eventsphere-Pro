package com.eventsphere;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.ArrayList;

public class StudentSeatMapPanel extends JPanel {
    private JComboBox<String> eventBox;
    private String[] eventIds;
    private JPanel seatGrid;
    private String studentUsername;

    public StudentSeatMapPanel(String username) {
        this.studentUsername = username;
        setLayout(new BorderLayout(10,10));
        setBackground(new Color(220,236,209));

        JLabel heading = new JLabel("Your Event Seat Map", SwingConstants.CENTER);
        heading.setFont(new Font("Serif", Font.BOLD, 22));
        heading.setForeground(new Color(34, 139, 34));
        add(heading, BorderLayout.NORTH);

        eventBox = new JComboBox<>();
        fillEvents(username);
        add(eventBox, BorderLayout.WEST);

        seatGrid = new JPanel();
        seatGrid.setBackground(Color.WHITE);
        add(seatGrid, BorderLayout.CENTER);

        eventBox.addActionListener(e -> showSeatMap());

        if (eventBox.getItemCount() > 0) showSeatMap();
    }

    private void fillEvents(String username) {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/eventsphere_pro", "root", "Tiger_1309")) {
            Statement st = conn.createStatement();
            ResultSet rsU = st.executeQuery("SELECT id FROM users WHERE username='" + username + "'");
            int uid = -1;
            if (rsU.next()) uid = rsU.getInt("id");
            if (uid == -1) return;
            ResultSet rs = st.executeQuery(
                "SELECT e.id, e.title FROM events e " +
                "JOIN registrations r ON e.id = r.event_id " +
                "WHERE r.user_id = "+uid+" AND r.status='APPROVED' " +
                "  AND (e.event_type<>'Personal Event' OR e.created_by<>"+uid+") " +
                "ORDER BY e.event_date DESC"
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

    // Show a visualization for the event
    private void showSeatMap() {
        seatGrid.removeAll();
        if (eventBox.getSelectedIndex() == -1) {
            seatGrid.revalidate(); seatGrid.repaint(); return;
        }
        String eventId = eventIds[eventBox.getSelectedIndex()];
        int maxSeats = 0;
        Map<Integer, String> seatToUser = new HashMap<>();
        int userSeat = -1;

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/eventsphere_pro", "root", "Tiger_1309")) {
            Statement st = conn.createStatement();

            // Get max seats for the event
            ResultSet maxRS = st.executeQuery("SELECT max_seats FROM events WHERE id="+eventId);
            if (maxRS.next()) maxSeats = maxRS.getInt(1);

            // Get seat assignments
            PreparedStatement ps = conn.prepareStatement(
                "SELECT r.seat_number, u.username FROM registrations r " +
                "JOIN users u ON r.user_id=u.id WHERE r.event_id=? AND r.seat_number IS NOT NULL"
            );
            ps.setString(1, eventId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int seatNo = rs.getInt(1);
                String uname = rs.getString(2);
                seatToUser.put(seatNo, uname);
                if(uname.equals(studentUsername)) userSeat = seatNo;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading seats: " + ex.getMessage());
        }

        // Display grid: 10 seats per row
        int gridCols = 10, totalSeats = Math.max(maxSeats, seatToUser.size());
        int rows = (int)Math.ceil(totalSeats / (double)gridCols);
        seatGrid.setLayout(new GridLayout(rows, gridCols, 5, 5));

        for (int seat = 1; seat <= totalSeats; ++seat) {
            JButton btn = new JButton(String.valueOf(seat));
            btn.setEnabled(false);
            if (seatToUser.containsKey(seat)) {
                String uname = seatToUser.get(seat);
                if (uname.equals(studentUsername)) {
                    btn.setBackground(new Color(155,255,155)); 
                    btn.setText(seat + " (You)");
                } else {
                    btn.setBackground(new Color(220, 120, 120)); 
                }
            } else {
                btn.setBackground(Color.LIGHT_GRAY); 
            }
            seatGrid.add(btn);
        }
        seatGrid.revalidate();
        seatGrid.repaint();
    }
}
