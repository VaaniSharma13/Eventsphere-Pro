package com.eventsphere;
import javax.swing.*;
import java.awt.*;
public class OrganizerDashboard extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final Color FOREST_GREEN = new Color(34, 139, 34);
    private static final Color LIGHT_FOREST = new Color(199, 221, 178);
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private String username;
    public OrganizerDashboard(String username) {
        this.username = username;
        setTitle("🎪 Organizer Dashboard - EventSphere Pro 🎪");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel navBar = new JPanel(new GridLayout(1, 2, 10, 0));
        navBar.setBackground(FOREST_GREEN);
        navBar.setPreferredSize(new Dimension(900, 60));

        JButton addEventBtn = createNavButton("Add Event");
        JButton seatMapBtn = createNavButton("Seat Allotment");
        JButton logoutBtn = createNavButton("Logout");

        navBar.add(addEventBtn);
        navBar.add(seatMapBtn);
        navBar.add(logoutBtn);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(LIGHT_FOREST);
        mainPanel.add(new OrganizerAddEventPanel(username), "addevent");
        mainPanel.add(new OrganizerSeatMapPanel(username), "seatmap");
        mainPanel.add(createWelcomePanel("Welcome, " + username + "!"), "welcome");
        cardLayout.show(mainPanel, "welcome");
        
        addEventBtn.addActionListener(e -> cardLayout.show(mainPanel, "addevent"));
        seatMapBtn.addActionListener(e -> cardLayout.show(mainPanel, "seatmap"));
        logoutBtn.addActionListener(e -> {
            this.dispose();
            new LoginFrame();
        });
        add(navBar, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }
    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(75, 0, 130)); // Indigo
        btn.setFont(new Font("SansSerif", Font.BOLD, 18));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        return btn;
    }
    private JPanel createWelcomePanel(String message) {
        JPanel panel = new JPanel();
        panel.setBackground(LIGHT_FOREST);
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font("Serif", Font.BOLD, 36));
        label.setForeground(FOREST_GREEN);
        panel.add(label);
        return panel;
    }
}
