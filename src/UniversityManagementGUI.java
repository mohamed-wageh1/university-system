import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main GUI application for the University Management System
 * 
 * This class creates the main window and manages the different panels
 * based on user authentication and role-based access.
 */
public class UniversityManagementGUI extends JFrame {
    private AuthenticationService authService;
    private StudentService studentService;
    private FacultyService facultyService;
    private CourseService courseService;
    private AdminService adminService;
    
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    // Panel instances
    private LoginPanel loginPanel;
    private StudentPanel studentPanel;
    private FacultyPanel facultyPanel;
    private AdminPanel adminPanel;
    private SystemAdminPanel systemAdminPanel;
    
    public UniversityManagementGUI(AuthenticationService authService,
                                 StudentService studentService,
                                 FacultyService facultyService,
                                 CourseService courseService,
                                 AdminService adminService) {
        this.authService = authService;
        this.studentService = studentService;
        this.facultyService = facultyService;
        this.courseService = courseService;
        this.adminService = adminService;
        
        initializeGUI();
        createPanels();
        setupMenuBar();
        
        // Show login panel initially
        cardLayout.show(mainPanel, "LOGIN");
    }
    
    /**
     * Initializes the main GUI components
     */
    private void initializeGUI() {
        setTitle("Alamein International University Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Create card layout for switching panels
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);
    }
    
    /**
     * Creates all the different panels for different user roles
     */
    private void createPanels() {
        // Create login panel
        loginPanel = new LoginPanel(authService, this);
        mainPanel.add(loginPanel, "LOGIN");
        
        // Create role-based panels
        studentPanel = new StudentPanel(authService, studentService, courseService, this);
        mainPanel.add(studentPanel, "STUDENT");
        
        facultyPanel = new FacultyPanel(authService, facultyService, courseService, studentService, this);
        mainPanel.add(facultyPanel, "FACULTY");
        
        adminPanel = new AdminPanel(authService, studentService, facultyService, courseService, adminService, this);
        mainPanel.add(adminPanel, "ADMIN");
        
        systemAdminPanel = new SystemAdminPanel(authService, studentService, facultyService, courseService, adminService, this);
        mainPanel.add(systemAdminPanel, "SYSTEM_ADMIN");
    }
    
    /**
     * Sets up the menu bar
     */
    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    /**
     * Shows the about dialog
     */
    private void showAboutDialog() {
        String message = "Alamein International University Management System\n" +
                        "Version 2.0\n\n" +
                        "A comprehensive university management system\n" +
                        "with role-based access control and data persistence.\n\n" +
                        "© 2025 Alamein International University";
        
        JOptionPane.showMessageDialog(this, message, "About", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Handles successful login by showing the appropriate panel
     */
    public void handleLogin(User user) {
        switch (user.getRole()) {
            case STUDENT:
                studentPanel.refreshData();
                cardLayout.show(mainPanel, "STUDENT");
                break;
            case FACULTY:
                facultyPanel.refreshData();
                cardLayout.show(mainPanel, "FACULTY");
                break;
            case ADMIN_STAFF:
                adminPanel.refreshData();
                cardLayout.show(mainPanel, "ADMIN");
                break;
            case SYSTEM_ADMIN:
                systemAdminPanel.refreshData();
                cardLayout.show(mainPanel, "SYSTEM_ADMIN");
                break;
        }
        
        // Update title to show current user
        setTitle("Alamein International University Management System - " + user.getFullName() + " (" + user.getRole() + ")");
    }
    
    /**
     * Handles logout by returning to login panel
     */
    public void handleLogout() {
        authService.logout();
        cardLayout.show(mainPanel, "LOGIN");
        setTitle("Alamein International University Management System");
        loginPanel.clearFields();
    }
    
    /**
     * Shows an error message
     */
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Shows a success message
     */
    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Shows a confirmation dialog
     */
    public boolean showConfirmation(String message) {
        int result = JOptionPane.showConfirmDialog(this, message, "Confirmation", 
                                                  JOptionPane.YES_NO_OPTION, 
                                                  JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }
}
