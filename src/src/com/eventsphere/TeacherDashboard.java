package com.eventsphere;
import javax.swing.*;
import java.awt.*;
public class TeacherDashboard extends JFrame {
    private static final Color FOREST_GREEN = new Color(34, 139, 34);
    private static final Color LIGHT_FOREST = new Color(199, 221, 178);
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private String username;
    public TeacherDashboard(String username) {
        this.username = username;
        setTitle("📒 Teacher Dashboard - EventSphere Pro 📒");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel navBar = new JPanel(new GridLayout(1, 5, 10, 0));
        navBar.setBackground(FOREST_GREEN);
        navBar.setPreferredSize(new Dimension(900, 60));
        JButton examBtn = createNavButton("Post Exam");
        JButton seatBtn = createNavButton("Assign Exam Seats");
        JButton viewBtn = createNavButton("View Events");
        JButton addAssignBtn = createNavButton("Add Assignment");
        JButton viewAssignBtn = createNavButton("View Assignments");
        JButton logoutBtn = createNavButton("Logout");
        navBar.add(examBtn);
        navBar.add(seatBtn);
        navBar.add(viewBtn);
        navBar.add(addAssignBtn);
        navBar.add(viewAssignBtn);
        navBar.add(logoutBtn);
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(LIGHT_FOREST);
        mainPanel.add(new TeacherExamPanel(username), "exam");
        mainPanel.add(new TeacherAssignmentPanel(username), "assign");
        mainPanel.add(new TeacherSeatAssignPanel(username), "seat");
        mainPanel.add(new TeacherViewEventsPanel(), "view");
        mainPanel.add(new TeacherViewAssignmentsPanel(), "viewassign");
        mainPanel.add(createWelcomePanel("Welcome, " + username + "!"), "welcome");
        cardLayout.show(mainPanel, "welcome");

        examBtn.addActionListener(e -> cardLayout.show(mainPanel, "exam"));
        seatBtn.addActionListener(e -> cardLayout.show(mainPanel, "seat"));
        viewBtn.addActionListener(e -> cardLayout.show(mainPanel, "view"));
        addAssignBtn.addActionListener(e -> cardLayout.show(mainPanel, "assign"));
        viewAssignBtn.addActionListener(e -> cardLayout.show(mainPanel, "viewassign"));
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
        btn.setForeground(new Color(75, 0, 130));
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
