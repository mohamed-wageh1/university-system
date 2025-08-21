import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main class for the Alamein International University Management System
 * 
 * This is the entry point of the application that initializes all services
 * and starts the GUI-based user interface.
 * 
 * @author University Management System
 * @version 2.0
 */
public class Main {
    
    public static void main(String[] args) {
        // Initialize services
        AuthenticationService authService = new AuthenticationService();
        StudentService studentService = new StudentService();
        FacultyService facultyService = new FacultyService();
        CourseService courseService = new CourseService();
        AdminService adminService = new AdminService();
        
        // Initialize sample data
        initializeSampleData(authService, studentService, facultyService, courseService);
        
        // Start the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            UniversityManagementGUI gui = new UniversityManagementGUI(
                authService, studentService, facultyService, courseService, adminService);
            gui.setVisible(true);
        });
    }
    
    /**
     * Initializes sample data for testing purposes
     */
    private static void initializeSampleData(AuthenticationService authService, 
                                           StudentService studentService,
                                           FacultyService facultyService,
                                           CourseService courseService) {
        
        // Create sample users
        User adminUser = new User("admin", "admin123", UserRole.ADMIN_STAFF, "Admin User");
        User sysAdminUser = new User("sysadmin", "sysadmin123", UserRole.SYSTEM_ADMIN, "System Administrator");
        User faculty1User = new User("F2024001", "faculty123", UserRole.FACULTY, "Dr. John Smith");
        User faculty2User = new User("F2024002", "faculty123", UserRole.FACULTY, "Prof. Jane Doe");
        User student1User = new User("S2023001", "student123", UserRole.STUDENT, "Alice Johnson");
        User student2User = new User("S2023002", "student123", UserRole.STUDENT, "Bob Wilson");
        User student3User = new User("S2023003", "student123", UserRole.STUDENT, "Carol Brown");
        
        authService.registerUser(adminUser);
        authService.registerUser(sysAdminUser);
        authService.registerUser(faculty1User);
        authService.registerUser(faculty2User);
        authService.registerUser(student1User);
        authService.registerUser(student2User);
        authService.registerUser(student3User);
        
        // Create sample students
        Student student1 = new Student("S2023001", "Alice Johnson", "alice@university.edu", 
                                     "Computer Science", 2023);
        Student student2 = new Student("S2023002", "Bob Wilson", "bob@university.edu", 
                                     "Engineering", 2023);
        Student student3 = new Student("S2023003", "Carol Brown", "carol@university.edu", 
                                     "Mathematics", 2023);
        
        studentService.addStudent(student1);
        studentService.addStudent(student2);
        studentService.addStudent(student3);
        
        // Create sample faculty
        Faculty faculty1 = new Faculty("F2024001", "Dr. John Smith", "john.smith@university.edu", 
                                     "Computer Science", "Associate Professor");
        Faculty faculty2 = new Faculty("F2024002", "Prof. Jane Doe", "jane.doe@university.edu", 
                                     "Engineering", "Professor");
        
        facultyService.addFaculty(faculty1);
        facultyService.addFaculty(faculty2);
        
        // Create sample courses
        Course course1 = new Course("CS101", "Introduction to Programming", "Learn basic programming concepts", 3, "F2024001");
        Course course2 = new Course("CS201", "Data Structures", "Advanced data structures and algorithms", 3, "F2024001");
        Course course3 = new Course("ENG101", "Engineering Fundamentals", "Basic engineering principles", 4, "F2024002");
        
        courseService.addCourse(course1);
        courseService.addCourse(course2);
        courseService.addCourse(course3);
        
        // Enroll some students in courses
        studentService.enrollStudentInCourse("S2023001", "CS101");
        studentService.enrollStudentInCourse("S2023001", "CS201");
        studentService.enrollStudentInCourse("S2023002", "CS101");
        studentService.enrollStudentInCourse("S2023002", "ENG101");
        studentService.enrollStudentInCourse("S2023003", "CS101");
        
        courseService.enrollStudent("CS101", "S2023001");
        courseService.enrollStudent("CS101", "S2023002");
        courseService.enrollStudent("CS101", "S2023003");
        courseService.enrollStudent("CS201", "S2023001");
        courseService.enrollStudent("ENG101", "S2023002");
        
        // Add some sample grades
        Grade grade1 = new Grade(85.5);
        Grade grade2 = new Grade("B+");
        Grade grade3 = new Grade(92.0);
        
        studentService.addGrade("S2023001", "CS101", grade1);
        studentService.addGrade("S2023002", "CS101", grade2);
        studentService.addGrade("S2023001", "CS201", grade3);
        
        System.out.println("Sample data initialized successfully!");
        System.out.println();
        System.out.println("=== Sample Login Credentials ===");
        System.out.println("Student: S2023001 / student123");
        System.out.println("Faculty: F2024001 / faculty123");
        System.out.println("Admin: admin / admin123");
        System.out.println("System Admin: sysadmin / sysadmin123");
        System.out.println("================================");
    }
}