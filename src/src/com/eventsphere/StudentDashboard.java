package com.eventsphere;
import javax.swing.*;
import java.awt.*;
public class StudentDashboard extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final Color FOREST_GREEN = new Color(34, 139, 34);
    private static final Color LIGHT_FOREST = new Color(199, 221, 178);
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private String username;
    public StudentDashboard(String username) {
        this.username = username;
        setTitle("🎓 Student Dashboard - EventSphere Pro 🎓");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        JPanel navBar = new JPanel(new GridLayout(1, 5, 10, 0));
        navBar.setBackground(FOREST_GREEN);
        navBar.setPreferredSize(new Dimension(900, 60));

        JButton myEventsBtn = createNavButton("My Events");
        JButton browseEventsBtn = createNavButton("Browse Events");
        JButton seatMapBtn = createNavButton("Seat Maps");
        JButton addPersonalBtn = createNavButton("Add Personal Event");
        JButton viewAssignBtn = createNavButton("View Assignments");
        JButton viewExamsBtn = createNavButton("View Exams");
        JButton logoutBtn = createNavButton("Logout");

        navBar.add(myEventsBtn);
        navBar.add(browseEventsBtn);
        navBar.add(seatMapBtn);
        navBar.add(addPersonalBtn);
        navBar.add(viewAssignBtn);
        navBar.add(viewExamsBtn);
        navBar.add(logoutBtn);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(LIGHT_FOREST);
        mainPanel.add(new MyEventsPanel(username), "myevents");
        mainPanel.add(new BrowseEventsPanel(username), "browse");
        mainPanel.add(new StudentSeatMapPanel(username), "seatmap");   
        mainPanel.add(new AddPersonalEventPanel(username), "addpersonal");
        mainPanel.add(new StudentViewAssignmentsPanel(), "viewassign");
        mainPanel.add(new StudentViewExamsPanel(username), "viewexams");
        mainPanel.add(createWelcomePanel("Welcome, " + username + "!"), "welcome");

        cardLayout.show(mainPanel, "welcome");
        myEventsBtn.addActionListener(e -> cardLayout.show(mainPanel, "myevents"));
        browseEventsBtn.addActionListener(e -> cardLayout.show(mainPanel, "browse"));
        seatMapBtn.addActionListener(e -> cardLayout.show(mainPanel, "seatmap"));
        addPersonalBtn.addActionListener(e -> cardLayout.show(mainPanel, "addpersonal"));
        viewAssignBtn.addActionListener(e -> cardLayout.show(mainPanel, "viewassign"));
        viewExamsBtn.addActionListener(e -> cardLayout.show(mainPanel, "viewexams"));
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
