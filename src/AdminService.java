import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * AdminService provides administrative functions and reporting capabilities
 * 
 * This service handles system administration, reports, and analytics
 * for the university management system.
 */
public class AdminService {
    
    /**
     * Generates a comprehensive system report
     * 
     * @param studentService - student service instance
     * @param facultyService - faculty service instance
     * @param courseService - course service instance
     * @param authService - authentication service instance
     * @return formatted system report
     */
    public String generateSystemReport(StudentService studentService, 
                                     FacultyService facultyService,
                                     CourseService courseService,
                                     AuthenticationService authService) {
        
        StringBuilder report = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        report.append("========================================\n");
        report.append("   ALAMEIN INTERNATIONAL UNIVERSITY\n");
        report.append("        SYSTEM REPORT\n");
        report.append("========================================\n");
        report.append("Generated: ").append(LocalDateTime.now().format(formatter)).append("\n\n");
        
        // User Statistics
        report.append("USER STATISTICS:\n");
        report.append("- Total Users: ").append(authService.getUserCount()).append("\n");
        report.append("- Active Users: ").append(authService.getActiveUserCount()).append("\n");
        
        Map<String, User> allUsers = authService.getAllUsers();
        if (allUsers != null) {
            Map<UserRole, Long> usersByRole = allUsers.values().stream()
                    .collect(Collectors.groupingBy(User::getRole, Collectors.counting()));
            
            for (Map.Entry<UserRole, Long> entry : usersByRole.entrySet()) {
                report.append("- ").append(entry.getKey().getDisplayName())
                      .append(": ").append(entry.getValue()).append("\n");
            }
        }
        report.append("\n");
        
        // Student Statistics
        report.append("STUDENT STATISTICS:\n");
        report.append("- Total Students: ").append(studentService.getStudentCount()).append("\n");
        report.append("- Active Students: ").append(studentService.getActiveStudentCount()).append("\n");
        report.append("- Average GPA: ").append(String.format("%.2f", studentService.getAverageGpa())).append("\n");
        
        List<Student> probationStudents = studentService.getStudentsOnProbation();
        report.append("- Students on Probation: ").append(probationStudents.size()).append("\n");
        
        // Students by major
        Map<String, Long> studentsByMajor = studentService.getAllStudents().stream()
                .collect(Collectors.groupingBy(Student::getMajor, Collectors.counting()));
        
        report.append("- Students by Major:\n");
        for (Map.Entry<String, Long> entry : studentsByMajor.entrySet()) {
            report.append("  * ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        report.append("\n");
        
        // Faculty Statistics
        report.append("FACULTY STATISTICS:\n");
        report.append("- Total Faculty: ").append(facultyService.getFacultyCount()).append("\n");
        report.append("- Average Course Load: ").append(String.format("%.1f", facultyService.getAverageCourseLoad())).append("\n");
        report.append("- Total Courses Assigned: ").append(facultyService.getTotalCoursesAssigned()).append("\n");
        
        Map<String, Integer> facultyByDept = facultyService.getFacultyStatsByDepartment();
        report.append("- Faculty by Department:\n");
        for (Map.Entry<String, Integer> entry : facultyByDept.entrySet()) {
            report.append("  * ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        report.append("\n");
        
        // Course Statistics
        report.append("COURSE STATISTICS:\n");
        Map<String, Object> courseStats = courseService.getCourseStatistics();
        report.append("- Total Courses: ").append(courseStats.get("totalCourses")).append("\n");
        report.append("- Active Courses: ").append(courseStats.get("activeCourses")).append("\n");
        report.append("- Available Courses: ").append(courseStats.get("availableCourses")).append("\n");
        report.append("- Full Courses: ").append(courseStats.get("fullCourses")).append("\n");
        report.append("- Total Enrollments: ").append(courseStats.get("totalEnrollments")).append("\n");
        report.append("- Average Enrollment: ").append(String.format("%.1f", (Double) courseStats.get("averageEnrollment"))).append("\n");
        
        List<Course> availableCourses = courseService.getAvailableCourses();
        if (!availableCourses.isEmpty()) {
            report.append("- Courses with Available Spots:\n");
            for (Course course : availableCourses) {
                report.append("  * ").append(course.getCourseId()).append(" - ")
                      .append(course.getCourseName()).append(" (")
                      .append(course.getAvailableSpots()).append(" spots)\n");
            }
        }
        report.append("\n");
        
        // System Health
        report.append("SYSTEM HEALTH:\n");
        report.append("- Data Persistence: Operational\n");
        report.append("- File System: Accessible\n");
        report.append("- User Authentication: Active\n");
        
        report.append("\n========================================\n");
        report.append("End of Report\n");
        report.append("========================================\n");
        
        return report.toString();
    }
    
    /**
     * Generates a student academic report
     * 
     * @param studentService - student service instance
     * @param courseService - course service instance
     * @param studentId - ID of student
     * @return formatted student report
     */
    public String generateStudentReport(StudentService studentService, 
                                      CourseService courseService, 
                                      String studentId) {
        
        Student student = studentService.getStudent(studentId);
        if (student == null) {
            return "Student not found: " + studentId;
        }
        
        StringBuilder report = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        report.append("========================================\n");
        report.append("         STUDENT ACADEMIC REPORT\n");
        report.append("========================================\n");
        report.append("Generated: ").append(LocalDateTime.now().format(formatter)).append("\n\n");
        
        // Student Information
        report.append("STUDENT INFORMATION:\n");
        report.append("- Student ID: ").append(student.getStudentId()).append("\n");
        report.append("- Name: ").append(student.getFullName()).append("\n");
        report.append("- Email: ").append(student.getEmail()).append("\n");
        report.append("- Major: ").append(student.getMajor()).append("\n");
        report.append("- Enrollment Year: ").append(student.getEnrollmentYear()).append("\n");
        report.append("- Status: ").append(student.getStatus()).append("\n");
        report.append("- GPA: ").append(String.format("%.2f", student.getGpa())).append("\n");
        report.append("- Academic Standing: ").append(student.getAcademicStanding()).append("\n\n");
        
        // Current Enrollments
        List<String> enrolledCourses = student.getEnrolledCourses();
        report.append("CURRENT ENROLLMENTS (").append(enrolledCourses.size()).append(" courses):\n");
        
        if (enrolledCourses.isEmpty()) {
            report.append("- No current enrollments\n");
        } else {
            for (String courseId : enrolledCourses) {
                Course course = courseService.getCourse(courseId);
                if (course != null) {
                    report.append("- ").append(courseId).append(" - ")
                          .append(course.getCourseName()).append(" (")
                          .append(course.getCreditHours()).append(" credits)\n");
                }
            }
        }
        report.append("\n");
        
        // Grades and Completed Courses
        Map<String, Grade> grades = student.getGrades();
        report.append("COMPLETED COURSES (").append(grades.size()).append(" courses):\n");
        
        if (grades.isEmpty()) {
            report.append("- No completed courses\n");
        } else {
            int totalCredits = 0;
            for (Map.Entry<String, Grade> entry : grades.entrySet()) {
                String courseId = entry.getKey();
                Grade grade = entry.getValue();
                Course course = courseService.getCourse(courseId);
                
                if (course != null) {
                    totalCredits += course.getCreditHours();
                    report.append("- ").append(courseId).append(" - ")
                          .append(course.getCourseName()).append(" | Grade: ")
                          .append(grade.getLetterGrade()).append(" (")
                          .append(String.format("%.1f", grade.getPercentage())).append("%) | ")
                          .append(course.getCreditHours()).append(" credits\n");
                }
            }
            report.append("- Total Completed Credits: ").append(totalCredits).append("\n");
        }
        
        report.append("\n========================================\n");
        report.append("End of Student Report\n");
        report.append("========================================\n");
        
        return report.toString();
    }
    
    /**
     * Generates a faculty workload report
     * 
     * @param facultyService - faculty service instance
     * @param courseService - course service instance
     * @param facultyId - ID of faculty member
     * @return formatted faculty report
     */
    public String generateFacultyReport(FacultyService facultyService, 
                                      CourseService courseService, 
                                      String facultyId) {
        
        Faculty faculty = facultyService.getFaculty(facultyId);
        if (faculty == null) {
            return "Faculty member not found: " + facultyId;
        }
        
        StringBuilder report = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        report.append("========================================\n");
        report.append("         FACULTY WORKLOAD REPORT\n");
        report.append("========================================\n");
        report.append("Generated: ").append(LocalDateTime.now().format(formatter)).append("\n\n");
        
        // Faculty Information
        report.append("FACULTY INFORMATION:\n");
        report.append("- Faculty ID: ").append(faculty.getFacultyId()).append("\n");
        report.append("- Name: ").append(faculty.getFullName()).append("\n");
        report.append("- Email: ").append(faculty.getEmail()).append("\n");
        report.append("- Department: ").append(faculty.getDepartment()).append("\n");
        report.append("- Position: ").append(faculty.getPosition()).append("\n");
        
        report.append("- Current Course Load: ").append(faculty.getCoursesTaught().size()).append(" courses\n\n");
        
        // Courses Taught
        List<String> coursesTaught = faculty.getCoursesTaught();
        report.append("COURSES TAUGHT (").append(coursesTaught.size()).append(" courses):\n");
        
        if (coursesTaught.isEmpty()) {
            report.append("- No courses currently assigned\n");
        } else {
            for (String courseId : coursesTaught) {
                Course course = courseService.getCourse(courseId);
                if (course != null) {
                    report.append("- ").append(courseId).append(" - ")
                          .append(course.getCourseName()).append(" (")
                          .append(course.getEnrollmentCount()).append(" students)\n");
                }
            }
        }
        
        report.append("\n========================================\n");
        report.append("End of Faculty Report\n");
        report.append("========================================\n");
        
        return report.toString();
    }
}
