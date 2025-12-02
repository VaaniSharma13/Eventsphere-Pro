package com.eventsphere;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class StudentViewExamsPanel extends JPanel {
    private JTable table;
    private JButton registerBtn;
    private JLabel seatInfo;
    private int[] eventIds;  // Parallel to table rows
    private String username;

    public StudentViewExamsPanel(String username) {
        this.username = username;

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(220, 236, 209));

        JLabel heading = new JLabel("Available Exams & Your Seat", SwingConstants.CENTER);
        heading.setFont(new Font("Serif", Font.BOLD, 22));
        heading.setForeground(new Color(34, 139, 34));
        add(heading, BorderLayout.NORTH);

        table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        registerBtn = new JButton("Register for Selected Exam");
        seatInfo = new JLabel(" "); // Room for seat info
        bottomPanel.add(registerBtn);
        bottomPanel.add(seatInfo);
        add(bottomPanel, BorderLayout.SOUTH);

        registerBtn.addActionListener(e -> registerForExam());
        table.getSelectionModel().addListSelectionListener(e -> updateSeatInfo());

        loadExams();
    }

    private void loadExams() {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/eventsphere_pro", "root", "Tiger_1309")) {
            Statement st = conn.createStatement();
            ResultSet rsUser = st.executeQuery("SELECT id FROM users WHERE username='" + username + "'");
            int uid = -1;
            if (rsUser.next()) uid = rsUser.getInt(1);
            if (uid == -1) return;

            Statement eventSt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            ResultSet rs = eventSt.executeQuery(
                "SELECT id, title, event_date, event_time, location, max_seats FROM events WHERE event_type='EXAM' ORDER BY event_date"
            );

            // Count events for eventIds array
            rs.last(); int rowCount = rs.getRow(); rs.beforeFirst();
            eventIds = new int[rowCount];

            Vector<String> cols = new Vector<>();
            cols.add("Title"); cols.add("Date");
            cols.add("Time"); cols.add("Location"); cols.add("Max Seats");
            Vector<Vector<Object>> data = new Vector<>();
            int idx = 0;
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                eventIds[idx++] = rs.getInt("id");
                row.add(rs.getString("title"));
                row.add(rs.getDate("event_date"));
                row.add(rs.getTime("event_time"));
                row.add(rs.getString("location"));
                row.add(rs.getInt("max_seats"));
                data.add(row);
            }
            table.setModel(new javax.swing.table.DefaultTableModel(data, cols));
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading exams: " + ex.getMessage());
        }
    }

    // Register for the selected exam
    private void registerForExam() {
        int sel = table.getSelectedRow();
        if (sel == -1) {
            JOptionPane.showMessageDialog(this, "Select an exam first!");
            return;
        }
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/eventsphere_pro", "root", "Tiger_1309")) {
            Statement st = conn.createStatement();
            ResultSet rsU = st.executeQuery("SELECT id FROM users WHERE username='" + username + "'");
            int uid = -1;
            if (rsU.next()) uid = rsU.getInt("id");
            if (uid == -1) return;

            int eventId = eventIds[sel];

            // Check if already registered
            ResultSet rsReg = st.executeQuery("SELECT * FROM registrations WHERE event_id=" + eventId + " AND user_id=" + uid);
            if (rsReg.next()) {
                JOptionPane.showMessageDialog(this, "You are already registered for this exam.");
                return;
            }

            // Check seat limit
            ResultSet rsEvent = st.executeQuery("SELECT max_seats FROM events WHERE id=" + eventId);
            int maxSeats = -1;
            if (rsEvent.next()) maxSeats = rsEvent.getInt("max_seats");
            ResultSet rsCount = st.executeQuery("SELECT COUNT(*) FROM registrations WHERE event_id=" + eventId);
            int regCount = (rsCount.next()) ? rsCount.getInt(1) : 0;
            if (regCount >= maxSeats) {
                JOptionPane.showMessageDialog(this, "Exam is full! Cannot register.");
                return;
            }

            // Register (insert, seat number is assigned by teacher later)
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO registrations (user_id, event_id, status) VALUES (?, ?, 'APPROVED')");
            ps.setInt(1, uid);
            ps.setInt(2, eventId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Registered! Wait for teacher to allot your seat.");
            updateSeatInfo();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // Update seat info for selected event (and enable/disable registerBtn)
    private void updateSeatInfo() {
        int sel = table.getSelectedRow();
        seatInfo.setText(" ");
        if (sel == -1) {
            registerBtn.setEnabled(false);
            return;
        }
        registerBtn.setEnabled(true);

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/eventsphere_pro", "root", "Tiger_1309")) {
            Statement st = conn.createStatement();
            ResultSet rsU = st.executeQuery("SELECT id FROM users WHERE username='" + username + "'");
            int uid = -1;
            if (rsU.next()) uid = rsU.getInt("id");
            if (uid == -1) return;
            int eventId = eventIds[sel];
            ResultSet rsReg = st.executeQuery("SELECT seat_number FROM registrations WHERE event_id=" + eventId + " AND user_id=" + uid);
            if (rsReg.next()) {
                int seat = rsReg.getInt("seat_number");
                if (rsReg.wasNull()) {
                    seatInfo.setText(" Registered. Waiting for teacher to allot seat.");
                } else {
                    seatInfo.setText(" Registered. Your Seat Number: " + seat);
                }
                registerBtn.setEnabled(false);
            } else {
                seatInfo.setText(" Not registered.");
                registerBtn.setEnabled(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
