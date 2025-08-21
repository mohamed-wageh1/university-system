import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

/**
 * StudentPanel provides the student interface
 * 
 * This panel allows students to view their profile, courses, grades,
 * and manage their enrollment.
 */
public class StudentPanel extends JPanel {
    private AuthenticationService authService;
    private StudentService studentService;
    private CourseService courseService;
    private UniversityManagementGUI mainFrame;
    
    private JTabbedPane tabbedPane;
    private JTable enrolledCoursesTable;
    private JTable availableCoursesTable;
    private JTable gradesTable;
    private JLabel studentInfoLabel;
    
    public StudentPanel(AuthenticationService authService, StudentService studentService,
                       CourseService courseService, UniversityManagementGUI mainFrame) {
        this.authService = authService;
        this.studentService = studentService;
        this.courseService = courseService;
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
        enrolledCoursesTable = new JTable();
        availableCoursesTable = new JTable();
        gradesTable = new JTable();
        
        studentInfoLabel = new JLabel();
        studentInfoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
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
        tabbedPane.addTab("Available Courses", createAvailableCoursesTab());
        tabbedPane.addTab("Grades", createGradesTab());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    /**
     * Creates the header panel with user info and logout
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0, 102, 204));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel titleLabel = new JLabel("Student Portal");
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
        infoPanel.setBorder(BorderFactory.createTitledBorder("Student Information"));
        
        infoPanel.add(studentInfoLabel);
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
        
        JScrollPane scrollPane = new JScrollPane(enrolledCoursesTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Enrolled Courses"));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton dropButton = new JButton("Drop Selected Course");
        dropButton.addActionListener(e -> dropSelectedCourse());
        buttonPanel.add(dropButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Creates the available courses tab
     */
    private JPanel createAvailableCoursesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(availableCoursesTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Available Courses"));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton enrollButton = new JButton("Enroll in Selected Course");
        enrollButton.addActionListener(e -> enrollInSelectedCourse());
        buttonPanel.add(enrollButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Creates the grades tab
     */
    private JPanel createGradesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(gradesTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Academic Record"));
        
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
                case 1: refreshEnrolledCoursesData(); break;
                case 2: refreshAvailableCoursesData(); break;
                case 3: refreshGradesData(); break;
            }
        });
    }
    
    /**
     * Refreshes all data when panel becomes visible
     */
    public void refreshData() {
        refreshProfileData();
        refreshEnrolledCoursesData();
        refreshAvailableCoursesData();
        refreshGradesData();
    }
    
    /**
     * Refreshes profile data
     */
    private void refreshProfileData() {
        User currentUser = authService.getCurrentUser();
        if (currentUser != null) {
            Student student = studentService.getStudent(currentUser.getUsername());
            if (student != null) {
                String info = String.format(
                    "<html><b>Student ID:</b> %s<br/>" +
                    "<b>Name:</b> %s<br/>" +
                    "<b>Email:</b> %s<br/>" +
                    "<b>Major:</b> %s<br/>" +
                    "<b>Enrollment Year:</b> %d<br/>" +
                    "<b>Status:</b> %s<br/>" +
                    "<b>GPA:</b> %.2f<br/>" +
                    "<b>Academic Standing:</b> %s<br/>" +
                    "<b>Completed Courses:</b> %d</html>",
                    student.getStudentId(),
                    student.getFullName(),
                    student.getEmail(),
                    student.getMajor(),
                    student.getEnrollmentYear(),
                    student.getStatus(),
                    student.getGpa(),
                    student.getAcademicStanding(),
                    student.getCompletedCourses()
                );
                studentInfoLabel.setText(info);
            }
        }
    }
    
    /**
     * Refreshes enrolled courses data
     */
    private void refreshEnrolledCoursesData() {
        User currentUser = authService.getCurrentUser();
        if (currentUser != null) {
            Student student = studentService.getStudent(currentUser.getUsername());
            if (student != null) {
                List<String> enrolledCourses = student.getEnrolledCourses();
                
                String[] columns = {"Course ID", "Course Name", "Credits", "Instructor", "Status"};
                DefaultTableModel model = new DefaultTableModel(columns, 0);
                
                for (String courseId : enrolledCourses) {
                    Course course = courseService.getCourse(courseId);
                    if (course != null) {
                        model.addRow(new Object[]{
                            course.getCourseId(),
                            course.getCourseName(),
                            course.getCreditHours(),
                            course.getInstructorId(),
                            course.getStatus()
                        });
                    }
                }
                
                enrolledCoursesTable.setModel(model);
            }
        }
    }
    
    /**
     * Refreshes available courses data
     */
    private void refreshAvailableCoursesData() {
        List<Course> availableCourses = courseService.getAvailableCourses();
        
        String[] columns = {"Course ID", "Course Name", "Credits", "Instructor", "Available Spots"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        for (Course course : availableCourses) {
            model.addRow(new Object[]{
                course.getCourseId(),
                course.getCourseName(),
                course.getCreditHours(),
                course.getInstructorId(),
                course.getAvailableSpots()
            });
        }
        
        availableCoursesTable.setModel(model);
    }
    
    /**
     * Refreshes grades data
     */
    private void refreshGradesData() {
        User currentUser = authService.getCurrentUser();
        if (currentUser != null) {
            Student student = studentService.getStudent(currentUser.getUsername());
            if (student != null) {
                Map<String, Grade> grades = student.getGrades();
                
                String[] columns = {"Course ID", "Course Name", "Letter Grade", "Percentage", "Grade Points"};
                DefaultTableModel model = new DefaultTableModel(columns, 0);
                
                for (Map.Entry<String, Grade> entry : grades.entrySet()) {
                    String courseId = entry.getKey();
                    Grade grade = entry.getValue();
                    Course course = courseService.getCourse(courseId);
                    
                    String courseName = course != null ? course.getCourseName() : "Unknown";
                    
                    model.addRow(new Object[]{
                        courseId,
                        courseName,
                        grade.getLetterGrade(),
                        String.format("%.1f%%", grade.getPercentage()),
                        String.format("%.1f", grade.getGradePoints())
                    });
                }
                
                gradesTable.setModel(model);
            }
        }
    }
    
    /**
     * Enrolls student in selected course
     */
    private void enrollInSelectedCourse() {
        int selectedRow = availableCoursesTable.getSelectedRow();
        if (selectedRow == -1) {
            mainFrame.showError("Please select a course to enroll in.");
            return;
        }
        
        String courseId = (String) availableCoursesTable.getValueAt(selectedRow, 0);
        User currentUser = authService.getCurrentUser();
        
        if (studentService.enrollStudentInCourse(currentUser.getUsername(), courseId) &&
            courseService.enrollStudent(courseId, currentUser.getUsername())) {
            mainFrame.showSuccess("Successfully enrolled in course: " + courseId);
            refreshEnrolledCoursesData();
            refreshAvailableCoursesData();
        } else {
            mainFrame.showError("Failed to enroll in course. Please check if you meet the requirements.");
        }
    }
    
    /**
     * Drops student from selected course
     */
    private void dropSelectedCourse() {
        int selectedRow = enrolledCoursesTable.getSelectedRow();
        if (selectedRow == -1) {
            mainFrame.showError("Please select a course to drop.");
            return;
        }
        
        String courseId = (String) enrolledCoursesTable.getValueAt(selectedRow, 0);
        String courseName = (String) enrolledCoursesTable.getValueAt(selectedRow, 1);
        
        if (mainFrame.showConfirmation("Are you sure you want to drop " + courseName + "?")) {
            User currentUser = authService.getCurrentUser();
            
            if (studentService.dropStudentFromCourse(currentUser.getUsername(), courseId) &&
                courseService.dropStudent(courseId, currentUser.getUsername())) {
                mainFrame.showSuccess("Successfully dropped course: " + courseName);
                refreshEnrolledCoursesData();
                refreshAvailableCoursesData();
            } else {
                mainFrame.showError("Failed to drop course.");
            }
        }
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
