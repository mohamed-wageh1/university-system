import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * FacultyPanel provides the faculty interface
 * 
 * This panel allows faculty to view their profile, courses they teach,
 * students in their courses, and add grades.
 */
public class FacultyPanel extends JPanel {
    private AuthenticationService authService;
    private FacultyService facultyService;
    private CourseService courseService;
    private StudentService studentService;
    private UniversityManagementGUI mainFrame;
    
    private JTabbedPane tabbedPane;
    private JTable myCoursesTable;
    private JTable courseStudentsTable;
    private JTable allStudentsTable;
    private JLabel facultyInfoLabel;
    
    public FacultyPanel(AuthenticationService authService, FacultyService facultyService,
                       CourseService courseService, StudentService studentService,
                       UniversityManagementGUI mainFrame) {
        this.authService = authService;
        this.facultyService = facultyService;
        this.courseService = courseService;
        this.studentService = studentService;
        this.mainFrame = mainFrame;
        
        initializeComponents();
        layoutComponents();
        setupEventListeners();
    }
    
    /**
     * Initializes the GUI components
     */
    private void initializeComponents() {
        tabbedPane = new JTabbedPane();
        
        // Initialize tables
        myCoursesTable = new JTable();
        courseStudentsTable = new JTable();
        allStudentsTable = new JTable();
        
        facultyInfoLabel = new JLabel();
        facultyInfoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
    }
    
    /**
     * Layouts the components in the panel
     */
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create tabs
        tabbedPane.addTab("Profile", createProfileTab());
        tabbedPane.addTab("My Courses", createMyCoursesTab());
        tabbedPane.addTab("Course Students", createCourseStudentsTab());
        tabbedPane.addTab("All Students", createAllStudentsTab());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    /**
     * Creates the header panel with user info and logout
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0, 102, 204));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel titleLabel = new JLabel("Faculty Portal");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> mainFrame.handleLogout());
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(logoutButton, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Creates the profile tab
     */
    private JPanel createProfileTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Faculty Information"));
        
        infoPanel.add(facultyInfoLabel);
        infoPanel.add(Box.createVerticalStrut(20));
        
        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.addActionListener(e -> showChangePasswordDialog());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(changePasswordButton);
        
        panel.add(infoPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Creates the my courses tab
     */
    private JPanel createMyCoursesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(myCoursesTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Courses I Teach"));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates the course students tab
     */
    private JPanel createCourseStudentsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Course selection
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel courseLabel = new JLabel("Select Course:");
        JComboBox<String> courseComboBox = new JComboBox<>();
        courseComboBox.addActionListener(e -> refreshCourseStudentsData((String) courseComboBox.getSelectedItem()));
        
        selectionPanel.add(courseLabel);
        selectionPanel.add(courseComboBox);
        
        JScrollPane scrollPane = new JScrollPane(courseStudentsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Students in Selected Course"));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addGradeButton = new JButton("Add Grade for Selected Student");
        addGradeButton.addActionListener(e -> showAddGradeDialog((String) courseComboBox.getSelectedItem()));
        buttonPanel.add(addGradeButton);
        
        panel.add(selectionPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Store reference to combo box for refreshing
        panel.putClientProperty("courseComboBox", courseComboBox);
        
        return panel;
    }
    
    /**
     * Creates the all students tab
     */
    private JPanel createAllStudentsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(allStudentsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("All Students"));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Sets up event listeners
     */
    private void setupEventListeners() {
        // Tab change listener to refresh data
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            switch (selectedIndex) {
                case 0: refreshProfileData(); break;
                case 1: refreshMyCoursesData(); break;
                case 2: refreshCourseStudentsTab(); break;
                case 3: refreshAllStudentsData(); break;
            }
        });
    }
    
    /**
     * Refreshes all data when panel becomes visible
     */
    public void refreshData() {
        refreshProfileData();
        refreshMyCoursesData();
        refreshCourseStudentsTab();
        refreshAllStudentsData();
    }
    
    /**
     * Refreshes profile data
     */
    private void refreshProfileData() {
        User currentUser = authService.getCurrentUser();
        if (currentUser != null) {
            Faculty faculty = facultyService.getFaculty(currentUser.getUsername());
            if (faculty != null) {
                String info = String.format(
                    "<html><b>Faculty ID:</b> %s<br/>" +
                    "<b>Name:</b> %s<br/>" +
                    "<b>Email:</b> %s<br/>" +
                    "<b>Department:</b> %s<br/>" +
                    "<b>Position:</b> %s<br/>" +
                    "<b>Office:</b> %s<br/>" +
                    "<b>Phone:</b> %s<br/>" +
                    "<b>Course Load:</b> %d courses</html>",
                    faculty.getFacultyId(),
                    faculty.getFullName(),
                    faculty.getEmail(),
                    faculty.getDepartment(),
                    faculty.getPosition(),
                    faculty.getOfficeLocation() != null ? faculty.getOfficeLocation() : "Not specified",
                    faculty.getPhoneNumber() != null ? faculty.getPhoneNumber() : "Not specified",
                    faculty.getCourseLoad()
                );
                facultyInfoLabel.setText(info);
            }
        }
    }
    
    /**
     * Refreshes my courses data
     */
    private void refreshMyCoursesData() {
        User currentUser = authService.getCurrentUser();
        if (currentUser != null) {
            List<Course> courses = courseService.getCoursesByInstructor(currentUser.getUsername());
            
            String[] columns = {"Course ID", "Course Name", "Credits", "Enrolled Students", "Max Capacity", "Status"};
            DefaultTableModel model = new DefaultTableModel(columns, 0);
            
            for (Course course : courses) {
                model.addRow(new Object[]{
                    course.getCourseId(),
                    course.getCourseName(),
                    course.getCreditHours(),
                    course.getEnrollmentCount(),
                    course.getMaxCapacity(),
                    course.getStatus()
                });
            }
            
            myCoursesTable.setModel(model);
        }
    }
    
    /**
     * Refreshes course students tab
     */
    private void refreshCourseStudentsTab() {
        User currentUser = authService.getCurrentUser();
        if (currentUser != null) {
            // Get the course students tab panel
            JPanel courseStudentsTab = (JPanel) tabbedPane.getComponentAt(2);
            @SuppressWarnings("unchecked")
            JComboBox<String> courseComboBox = (JComboBox<String>) courseStudentsTab.getClientProperty("courseComboBox");
            
            if (courseComboBox != null) {
                courseComboBox.removeAllItems();
                
                List<Course> courses = courseService.getCoursesByInstructor(currentUser.getUsername());
                for (Course course : courses) {
                    courseComboBox.addItem(course.getCourseId());
                }
                
                // Select first course if available
                if (courseComboBox.getItemCount() > 0) {
                    courseComboBox.setSelectedIndex(0);
                    refreshCourseStudentsData((String) courseComboBox.getSelectedItem());
                }
            }
        }
    }
    
    /**
     * Refreshes course students data for selected course
     */
    private void refreshCourseStudentsData(String courseId) {
        if (courseId == null) {
            courseStudentsTable.setModel(new DefaultTableModel());
            return;
        }
        
        List<Student> students = studentService.getStudentsInCourse(courseId);
        
        String[] columns = {"Student ID", "Name", "Email", "Major", "GPA", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        for (Student student : students) {
            model.addRow(new Object[]{
                student.getStudentId(),
                student.getFullName(),
                student.getEmail(),
                student.getMajor(),
                String.format("%.2f", student.getGpa()),
                student.getStatus()
            });
        }
        
        courseStudentsTable.setModel(model);
    }
    
    /**
     * Refreshes all students data
     */
    private void refreshAllStudentsData() {
        List<Student> students = studentService.getAllStudents();
        
        String[] columns = {"Student ID", "Name", "Email", "Major", "Enrollment Year", "GPA", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        for (Student student : students) {
            model.addRow(new Object[]{
                student.getStudentId(),
                student.getFullName(),
                student.getEmail(),
                student.getMajor(),
                student.getEnrollmentYear(),
                String.format("%.2f", student.getGpa()),
                student.getStatus()
            });
        }
        
        allStudentsTable.setModel(model);
    }
    
    /**
     * Shows add grade dialog
     */
    private void showAddGradeDialog(String courseId) {
        if (courseId == null) {
            mainFrame.showError("Please select a course first.");
            return;
        }
        
        int selectedRow = courseStudentsTable.getSelectedRow();
        if (selectedRow == -1) {
            mainFrame.showError("Please select a student to grade.");
            return;
        }
        
        String studentId = (String) courseStudentsTable.getValueAt(selectedRow, 0);
        String studentName = (String) courseStudentsTable.getValueAt(selectedRow, 1);
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Grade", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        JLabel infoLabel = new JLabel(String.format("Adding grade for: %s (%s) in course: %s", studentName, studentId, courseId));
        infoLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JRadioButton percentageRadio = new JRadioButton("Percentage", true);
        JRadioButton letterRadio = new JRadioButton("Letter Grade");
        ButtonGroup gradeTypeGroup = new ButtonGroup();
        gradeTypeGroup.add(percentageRadio);
        gradeTypeGroup.add(letterRadio);
        
        JTextField percentageField = new JTextField(10);
        JComboBox<String> letterComboBox = new JComboBox<>(new String[]{"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "D-", "F"});
        letterComboBox.setEnabled(false);
        
        percentageRadio.addActionListener(e -> {
            percentageField.setEnabled(true);
            letterComboBox.setEnabled(false);
        });
        
        letterRadio.addActionListener(e -> {
            percentageField.setEnabled(false);
            letterComboBox.setEnabled(true);
        });
        
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = 2;
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(infoLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(percentageRadio, gbc);
        gbc.gridx = 1;
        panel.add(percentageField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(letterRadio, gbc);
        gbc.gridx = 1;
        panel.add(letterComboBox, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Grade");
        JButton cancelButton = new JButton("Cancel");
        
        addButton.addActionListener(e -> {
            try {
                Grade grade;
                if (percentageRadio.isSelected()) {
                    double percentage = Double.parseDouble(percentageField.getText().trim());
                    grade = new Grade(percentage);
                } else {
                    String letterGrade = (String) letterComboBox.getSelectedItem();
                    grade = new Grade(letterGrade);
                }
                
                if (studentService.addGrade(studentId, courseId, grade)) {
                    mainFrame.showSuccess("Grade added successfully for " + studentName);
                    dialog.dispose();
                    refreshCourseStudentsData(courseId);
                } else {
                    mainFrame.showError("Failed to add grade. Student may not be enrolled in this course.");
                }
            } catch (NumberFormatException ex) {
                mainFrame.showError("Please enter a valid percentage (0-100).");
            } catch (IllegalArgumentException ex) {
                mainFrame.showError("Invalid grade: " + ex.getMessage());
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    /**
     * Shows change password dialog
     */
    private void showChangePasswordDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Change Password", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        JPasswordField oldPasswordField = new JPasswordField(15);
        JPasswordField newPasswordField = new JPasswordField(15);
        JPasswordField confirmPasswordField = new JPasswordField(15);
        
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Current Password:"), gbc);
        gbc.gridx = 1;
        panel.add(oldPasswordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1;
        panel.add(newPasswordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        panel.add(confirmPasswordField, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton changeButton = new JButton("Change");
        JButton cancelButton = new JButton("Cancel");
        
        changeButton.addActionListener(e -> {
            String oldPassword = new String(oldPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(dialog, "New passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (authService.changePassword(oldPassword, newPassword)) {
                JOptionPane.showMessageDialog(dialog, "Password changed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to change password. Check your current password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(changeButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
}
