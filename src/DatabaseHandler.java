import java.sql.*;
import java.util.*;
import java.time.LocalDateTime;

/**
 * Database handler for University Management System
 * Manages SQLite database connections and operations
 * Compatible with the existing User, Student, Faculty, Course classes
 */
public class DatabaseHandler {
    private static final String DB_URL = "jdbc:sqlite:university.db";
    private Connection connection;
    
    // Constructor - initialize database and create tables
    public DatabaseHandler() {
        try {
            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            initializeDatabase();
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
        }
    }
    
    // Initialize database connection and create tables
    private void initializeDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            createTables();
            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
        }
    }
    
    // Create all necessary tables
    private void createTables() throws SQLException {
        createUsersTable();
        createStudentsTable();
        createFacultyTable();
        createCoursesTable();
        createStudentEnrollmentsTable();
        createStudentGradesTable();
        createDepartmentsTable();
    }
    
    private void createUsersTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                username TEXT PRIMARY KEY,
                password TEXT NOT NULL,
                role TEXT NOT NULL,
                full_name TEXT NOT NULL,
                created_at TEXT,
                last_login TEXT,
                is_active BOOLEAN DEFAULT 1
            )
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }
    
    private void createStudentsTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS students (
                student_id TEXT PRIMARY KEY,
                full_name TEXT NOT NULL,
                email TEXT NOT NULL,
                major TEXT NOT NULL,
                enrollment_year INTEGER NOT NULL,
                status TEXT DEFAULT 'ACTIVE',
                gpa REAL DEFAULT 0.0
            )
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }
    
    private void createFacultyTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS faculty (
                faculty_id TEXT PRIMARY KEY,
                full_name TEXT NOT NULL,
                email TEXT NOT NULL,
                department TEXT NOT NULL,
                position TEXT NOT NULL
            )
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }
    
    private void createStudentEnrollmentsTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS student_enrollments (
                student_id TEXT,
                course_id TEXT,
                PRIMARY KEY (student_id, course_id),
                FOREIGN KEY (student_id) REFERENCES students(student_id),
                FOREIGN KEY (course_id) REFERENCES courses(course_id)
            )
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }
    
    private void createStudentGradesTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS student_grades (
                student_id TEXT,
                course_id TEXT,
                letter_grade TEXT,
                points REAL,
                semester TEXT,
                PRIMARY KEY (student_id, course_id),
                FOREIGN KEY (student_id) REFERENCES students(student_id),
                FOREIGN KEY (course_id) REFERENCES courses(course_id)
            )
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }
    
    private void createCoursesTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS courses (
                course_id TEXT PRIMARY KEY,
                course_name TEXT NOT NULL,
                description TEXT,
                credit_hours INTEGER NOT NULL,
                instructor_id TEXT,
                max_students INTEGER DEFAULT 30,
                status TEXT DEFAULT 'OPEN',
                semester TEXT,
                FOREIGN KEY (instructor_id) REFERENCES faculty(faculty_id)
            )
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }
    
    private void createDepartmentsTable() throws SQLException {
        // Simple departments table for basic functionality
        String sql = """
            CREATE TABLE IF NOT EXISTS departments (
                department_id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                location TEXT,
                head_of_department TEXT
            )
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }
    
    // User operations
    public boolean insertUser(User user) {
        String sql = """
            INSERT OR REPLACE INTO users (username, password, role, full_name, created_at, last_login, is_active)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, "placeholder"); // Password will be set properly when registerUser is called
            pstmt.setString(3, user.getRole().name());
            pstmt.setString(4, user.getFullName());
            pstmt.setString(5, user.getCreatedAt() != null ? user.getCreatedAt().toString() : LocalDateTime.now().toString());
            pstmt.setString(6, user.getLastLogin() != null ? user.getLastLogin().toString() : null);
            pstmt.setBoolean(7, user.isActive());
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error inserting user: " + e.getMessage());
            return false;
        }
    }
    
    // Student operations
    public boolean insertStudent(Student student) {
        String sql = """
            INSERT OR REPLACE INTO students (student_id, full_name, email, major, enrollment_year, status, gpa)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, student.getStudentId());
            pstmt.setString(2, student.getFullName());
            pstmt.setString(3, student.getEmail());
            pstmt.setString(4, student.getMajor());
            pstmt.setInt(5, student.getEnrollmentYear());
            pstmt.setString(6, student.getStatus().toString());
            pstmt.setDouble(7, student.getGpa());
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error inserting student: " + e.getMessage());
            return false;
        }
    }
    
    // Faculty operations
    public boolean insertFaculty(Faculty faculty) {
        String sql = """
            INSERT OR REPLACE INTO faculty (faculty_id, full_name, email, department, position)
            VALUES (?, ?, ?, ?, ?)
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, faculty.getFacultyId());
            pstmt.setString(2, faculty.getFullName());
            pstmt.setString(3, faculty.getEmail());
            pstmt.setString(4, faculty.getDepartment());
            pstmt.setString(5, faculty.getPosition());
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error inserting faculty: " + e.getMessage());
            return false;
        }
    }
    
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                User user = createUserFromResultSet(rs);
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving users: " + e.getMessage());
        }
        
        return users;
    }

    public User findUserByCredentials(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        UserRole.valueOf(rs.getString("role")),
                        rs.getString("full_name")
                );
            }
        } catch (Exception e) {
            System.err.println("Error in findUserByCredentials: " + e.getMessage());
        }
        return null;
    }

    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        String username = rs.getString("username");
        String password = rs.getString("password");
        String roleStr = rs.getString("role");
        String fullName = rs.getString("full_name");
        
        // Handle role mapping for compatibility
        UserRole role;
        try {
            role = UserRole.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            // Handle legacy role names
            switch (roleStr) {
                case "Administrative Staff":
                    role = UserRole.ADMIN_STAFF;
                    break;
                case "System Administrator":
                    role = UserRole.SYSTEM_ADMIN;
                    break;
                default:
                    role = UserRole.STUDENT; // Default fallback
            }
        }
        User user = new User(username, password, role, fullName);
        
        // Set additional fields if available
        String createdAtStr = rs.getString("created_at");
        String lastLoginStr = rs.getString("last_login");
        boolean isActive = rs.getBoolean("is_active");
        
        user.setActive(isActive);
        
        return user;
    }
    
    // Course operations
    public boolean insertCourse(Course course) {
        String sql = """
            INSERT OR REPLACE INTO courses (course_id, course_name, description, credit_hours, 
                                instructor_id, max_students, status, semester)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, course.getCourseId());
            pstmt.setString(2, course.getCourseName());
            pstmt.setString(3, course.getDescription());
            pstmt.setInt(4, course.getCreditHours());
            pstmt.setString(5, course.getInstructorId());
            pstmt.setInt(6, course.getMaxCapacity());
            pstmt.setString(7, course.getStatus().toString());
            pstmt.setString(8, course.getSemester());
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error inserting course: " + e.getMessage());
            return false;
        }
    }
    
    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Course course = new Course(
                    rs.getString("course_id"),
                    rs.getString("course_name"),
                    rs.getString("description"),
                    rs.getInt("credit_hours"),
                    rs.getString("instructor_id")
                );
                
                course.setMaxCapacity(rs.getInt("max_students"));
                course.setSemester(rs.getString("semester"));
                // Set status if available
                String statusStr = rs.getString("status");
                if (statusStr != null) {
                    try {
                        course.setStatus(CourseStatus.valueOf(statusStr));
                    } catch (IllegalArgumentException e) {
                        course.setStatus(CourseStatus.OPEN); // Default fallback
                    }
                }
                
                courses.add(course);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving courses: " + e.getMessage());
        }
        
        return courses;
    }
    
    public Course findCourseById(String courseId) {
        String sql = "SELECT * FROM courses WHERE course_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, courseId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Course course = new Course(
                        rs.getString("course_id"),
                        rs.getString("course_name"),
                        rs.getString("description"),
                        rs.getInt("credit_hours"),
                        rs.getString("instructor_id")
                    );
                    
                    course.setMaxCapacity(rs.getInt("max_students"));
                    course.setSemester(rs.getString("semester"));
                    
                    // Set status if available
                    String statusStr = rs.getString("status");
                    if (statusStr != null) {
                        try {
                            course.setStatus(CourseStatus.valueOf(statusStr));
                        } catch (IllegalArgumentException e) {
                            course.setStatus(CourseStatus.OPEN); // Default fallback
                        }
                    }
                    
                    return course;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding course: " + e.getMessage());
        }
        
        return null;
    }
    
    // Student enrollment operations using the new table structure
    public boolean enrollStudentInCourse(String studentId, String courseId) {
        String sql = """
            INSERT OR REPLACE INTO student_enrollments (student_id, course_id)
            VALUES (?, ?)
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, courseId);
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error enrolling student: " + e.getMessage());
            return false;
        }
    }
    
    public List<String> getEnrolledCourses(String studentId) {
        List<String> courses = new ArrayList<>();
        String sql = "SELECT course_id FROM student_enrollments WHERE student_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(rs.getString("course_id"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving enrolled courses: " + e.getMessage());
        }
        
        return courses;
    }
    
    public boolean addStudentGrade(String studentId, String courseId, String letterGrade, double points, String semester) {
        String sql = """
            INSERT OR REPLACE INTO student_grades (student_id, course_id, letter_grade, points, semester)
            VALUES (?, ?, ?, ?, ?)
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, courseId);
            pstmt.setString(3, letterGrade);
            pstmt.setDouble(4, points);
            pstmt.setString(5, semester);
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding grade: " + e.getMessage());
            return false;
        }
    }
    
    public Map<String, String> getStudentGrades(String studentId) {
        Map<String, String> grades = new HashMap<>();
        String sql = "SELECT course_id, letter_grade FROM student_grades WHERE student_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    grades.put(rs.getString("course_id"), rs.getString("letter_grade"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving student grades: " + e.getMessage());
        }
        
        return grades;
    }
    
    public boolean dropStudentFromCourse(String studentId, String courseId) {
        String sql = "DELETE FROM student_enrollments WHERE student_id = ? AND course_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, courseId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error dropping enrollment: " + e.getMessage());
            return false;
        }
    }
    
    // Initialize sample data
    public void initializeSampleData() {
        // Check if data already exists
        if (!getAllUsers().isEmpty()) {
            return; // Data already exists
        }
        
        // Create sample users with correct constructors
        User adminUser = new User("admin", "admin123", UserRole.ADMIN_STAFF, "Admin User");
        User sysAdminUser = new User("sysadmin", "sysadmin123", UserRole.SYSTEM_ADMIN, "System Administrator");
        User faculty1User = new User("F2024001", "faculty123", UserRole.FACULTY, "Dr. John Smith");
        User faculty2User = new User("F2024002", "faculty123", UserRole.FACULTY, "Prof. Jane Doe");
        User student1User = new User("S2023001", "student123", UserRole.STUDENT, "Alice Johnson");
        User student2User = new User("S2023002", "student123", UserRole.STUDENT, "Bob Wilson");
        User student3User = new User("S2023003", "student123", UserRole.STUDENT, "Carol Brown");
        
        insertUser(adminUser);
        insertUser(sysAdminUser);
        insertUser(faculty1User);
        insertUser(faculty2User);
        insertUser(student1User);
        insertUser(student2User);
        insertUser(student3User);
        
        // Create sample students with correct constructors
        Student student1 = new Student("S2023001", "Alice Johnson", "alice@university.edu", "Computer Science", 2023);
        Student student2 = new Student("S2023002", "Bob Wilson", "bob@university.edu", "Engineering", 2023);
        Student student3 = new Student("S2023003", "Carol Brown", "carol@university.edu", "Mathematics", 2023);
        
        insertStudent(student1);
        insertStudent(student2);
        insertStudent(student3);
        
        // Create sample faculty with correct constructors
        Faculty faculty1 = new Faculty("F2024001", "Dr. John Smith", "john.smith@university.edu", "Computer Science", "Associate Professor");
        Faculty faculty2 = new Faculty("F2024002", "Prof. Jane Doe", "jane.doe@university.edu", "Engineering", "Professor");
        
        insertFaculty(faculty1);
        insertFaculty(faculty2);
        
        // Create sample courses with correct constructors
        Course course1 = new Course("CS101", "Introduction to Programming", "Basic programming concepts", 3, "F2024001");
        Course course2 = new Course("CS102", "Data Structures", "Fundamental data structures", 3, "F2024001");
        Course course3 = new Course("MATH101", "Calculus I", "Introduction to calculus", 4, "F2024002");
        
        insertCourse(course1);
        insertCourse(course2);
        insertCourse(course3);
        
        System.out.println("Sample data initialized in database.");
    }
    
    // Close database connection
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}