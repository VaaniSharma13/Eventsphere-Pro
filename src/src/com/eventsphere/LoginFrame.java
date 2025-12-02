package com.eventsphere;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final Color FOREST_GREEN = new Color(34, 139, 34);
    private static final Color LIGHT_FOREST = new Color(199, 221, 178);
    private static final Color DARK_BLUE = new Color(25, 25, 112);    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;
    public LoginFrame() {
        setTitle("🌲 EventSphere Pro - Login 🌲");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(LIGHT_FOREST);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        JLabel titleLabel = new JLabel("Welcome to EventSphere Pro!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titleLabel.setForeground(FOREST_GREEN);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBackground(LIGHT_FOREST);
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        userLabel.setForeground(DARK_BLUE);
        usernameField = new JTextField();
        
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        passLabel.setForeground(DARK_BLUE);
        passwordField = new JPasswordField();        
        JLabel roleLabel = new JLabel("I am a:");
        roleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        roleLabel.setForeground(DARK_BLUE);
        roleBox = new JComboBox<>(new String[]{"STUDENT", "TEACHER", "ORGANIZER"});
        
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(FOREST_GREEN);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        loginButton.addActionListener(this::loginAction);
        
        formPanel.add(userLabel);
        formPanel.add(usernameField);
        formPanel.add(passLabel);
        formPanel.add(passwordField);
        formPanel.add(roleLabel);
        formPanel.add(roleBox);
        formPanel.add(new JLabel(""));
        formPanel.add(loginButton);
        
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalGlue());        
        add(mainPanel);
        setVisible(true);
    }
    private void loginAction(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleBox.getSelectedItem();
        
        if (username.equals("student1") && password.equals("password123") && role.equals("STUDENT")) {
            this.dispose();
            new StudentDashboard(username);
;
        } else if (username.equals("teacher1") && password.equals("password123") && role.equals("TEACHER")) {
            this.dispose();
            new TeacherDashboard(username);
        } else if (username.equals("organizer1") && password.equals("password123") && role.equals("ORGANIZER")) {
            this.dispose();
            new OrganizerDashboard(username);
        } else {
            JOptionPane.showMessageDialog(this, "Wrong username or password!");
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}
