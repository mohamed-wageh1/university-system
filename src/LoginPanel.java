import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * LoginPanel provides the user authentication interface
 * 
 * This panel handles user login with username and password validation.
 */
public class LoginPanel extends JPanel {
    private AuthenticationService authService;
    private UniversityManagementGUI mainFrame;
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;
    
    public LoginPanel(AuthenticationService authService, UniversityManagementGUI mainFrame) {
        this.authService = authService;
        this.mainFrame = mainFrame;
        
        initializeComponents();
        layoutComponents();
        setupEventListeners();
    }
    
    /**
     * Initializes the GUI components
     */
    private void initializeComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        statusLabel = new JLabel(" "); // Empty space for status messages
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Set default button
        loginButton.setDefaultCapable(true);
    }
    
    /**
     * Layouts the components in the panel
     */
    private void layoutComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        
        // Create main login panel
        JPanel loginFormPanel = new JPanel();
        loginFormPanel.setLayout(new BoxLayout(loginFormPanel, BoxLayout.Y_AXIS));
        loginFormPanel.setBackground(Color.WHITE);
        loginFormPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        
        // Title
        JLabel titleLabel = new JLabel("University Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(0, 102, 204));
        
        JLabel subtitleLabel = new JLabel("Alamein International University");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setForeground(new Color(102, 102, 102));
        
        // Form fields
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(10, 10, 10, 10);
        fieldsPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(passwordField, gbc);
        
        // Login button
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(20, 10, 10, 10);
        loginButton.setPreferredSize(new Dimension(100, 30));
        fieldsPanel.add(loginButton, gbc);
        
        // Add components to login form
        loginFormPanel.add(Box.createVerticalStrut(20));
        loginFormPanel.add(titleLabel);
        loginFormPanel.add(Box.createVerticalStrut(5));
        loginFormPanel.add(subtitleLabel);
        loginFormPanel.add(Box.createVerticalStrut(30));
        loginFormPanel.add(fieldsPanel);
        loginFormPanel.add(Box.createVerticalStrut(20));
        loginFormPanel.add(statusLabel);
        loginFormPanel.add(Box.createVerticalStrut(20));
        
        // Center the login form
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(245, 245, 245));
        centerPanel.add(loginFormPanel);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Add sample credentials info
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(245, 245, 245));
        JLabel infoLabel = new JLabel("<html><center>Sample Credentials:<br/>" +
                                     "Student: S2023001 / student123<br/>" +
                                     "Faculty: F001 / faculty123<br/>" +
                                     "Admin: A001 / staff123<br/>" +
                                     "System Admin: admin / admin123</center></html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(102, 102, 102));
        infoPanel.add(infoLabel);
        
        add(infoPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Sets up event listeners for the components
     */
    private void setupEventListeners() {
        ActionListener loginAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        };
        
        loginButton.addActionListener(loginAction);
        passwordField.addActionListener(loginAction); // Enter key in password field
    }
    
    /**
     * Performs the login operation
     */
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty()) {
            showStatus("Please enter username", Color.RED);
            usernameField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            showStatus("Please enter password", Color.RED);
            passwordField.requestFocus();
            return;
        }
        
        // Disable login button during authentication
        loginButton.setEnabled(false);
        showStatus("Authenticating...", Color.BLUE);
        
        // Perform authentication
        SwingUtilities.invokeLater(() -> {
            if (authService.login(username, password)) {
                showStatus("Login successful!", Color.GREEN);
                User user = authService.getCurrentUser();
                mainFrame.handleLogin(user);
            } else {
                showStatus("Invalid username or password", Color.RED);
                passwordField.setText("");
                passwordField.requestFocus();
            }
            loginButton.setEnabled(true);
        });
    }
    
    /**
     * Shows status message with specified color
     */
    private void showStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }
    
    /**
     * Clears the input fields
     */
    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        statusLabel.setText(" ");
        usernameField.requestFocus();
    }
}
