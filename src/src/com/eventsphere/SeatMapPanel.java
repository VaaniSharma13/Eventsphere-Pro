package com.eventsphere;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class SeatMapPanel extends JPanel {
    public SeatMapPanel(String username) {
        setBackground(new Color(199, 221, 178));
        setLayout(new BorderLayout());

        JLabel heading = new JLabel("Exam/Event Seat Map (Demo grid)", SwingConstants.CENTER);
        heading.setFont(new Font("Serif", Font.BOLD, 22));
        heading.setForeground(new Color(34, 139, 34));
        add(heading, BorderLayout.NORTH);

        // Sample 10x10 seat map visualization
        add(new SeatGridDemo(), BorderLayout.CENTER);
    }

    static class SeatGridDemo extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int rows = 8, cols = 10, size = 30, gap = 10;
            for(int r=0; r<rows; r++)
                for(int c=0; c<cols; c++) {
                    int x = c*(size+gap)+40, y = r*(size+gap)+40;
                    // Demo: random occupancy
                    boolean occupied = (r+c)%4==0;
                    g.setColor(occupied ? Color.RED : Color.GREEN.darker());
                    g.fillRect(x, y, size, size);
                    g.setColor(Color.BLACK);
                    g.drawRect(x, y, size, size);
                }
        }
        public Dimension getPreferredSize() { return new Dimension(500, 350); }
    }
}
