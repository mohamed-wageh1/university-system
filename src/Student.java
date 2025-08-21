import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Student class representing a university student
 * 
 * Contains student information including enrolled courses,
 * grades, and academic standing.
 */
public class Student implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String studentId;
    private String fullName;
    private String email;
    private String major;
    private int enrollmentYear;
    private StudentStatus status;
    private List<String> enrolledCourses;
    private Map<String, Grade> grades;
    private double gpa;
    
    /**
     * Constructor for creating a new student
     * 
     * @param studentId - unique student identifier
     * @param fullName - student's full name
     * @param email - student's email address
     * @param major - student's major
     * @param enrollmentYear - year of enrollment
     */
    public Student(String studentId, String fullName, String email, String major, int enrollmentYear) {
        validateStudentId(studentId);
        validateEmail(email);
        
        this.studentId = studentId;
        this.fullName = fullName;
        this.email = email;
        this.major = major;
        this.enrollmentYear = enrollmentYear;
        this.status = StudentStatus.ACTIVE;
        this.enrolledCourses = new ArrayList<>();
        this.grades = new HashMap<>();
        this.gpa = 0.0;
    }
    
    // Validation methods
    private void validateStudentId(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be null or empty");
        }
    }
    
    private void validateEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address");
        }
    }
    
    /**
     * Enrolls the student in a course
     * 
     * @param courseId - ID of the course to enroll in
     * @return true if enrollment was successful
     */
    public boolean enrollInCourse(String courseId) {
        if (courseId != null && !enrolledCourses.contains(courseId) && status.canEnroll()) {
            enrolledCourses.add(courseId);
            return true;
        }
        return false;
    }
    
    /**
     * Drops the student from a course
     * 
     * @param courseId - ID of the course to drop
     * @return true if drop was successful
     */
    public boolean dropCourse(String courseId) {
        return enrolledCourses.remove(courseId);
    }
    
    /**
     * Adds a grade for a completed course
     * 
     * @param courseId - ID of the course
     * @param grade - grade received
     */
    public void addGrade(String courseId, Grade grade) {
        if (enrolledCourses.contains(courseId)) {
            grades.put(courseId, grade);
            enrolledCourses.remove(courseId); // Move from enrolled to completed
            calculateGPA();
        } else {
            throw new IllegalStateException("Student is not enrolled in course: " + courseId);
        }
    }
    
    /**
     * Calculates the student's GPA based on completed courses
     */
    private void calculateGPA() {
        if (grades.isEmpty()) {
            gpa = 0.0;
            return;
        }
        
        double totalGradePoints = grades.values().stream()
                .mapToDouble(Grade::getGradePoints)
                .sum();
        
        gpa = totalGradePoints / grades.size();
    }
    
    /**
     * Gets the student's academic standing based on GPA
     * 
     * @return academic standing description
     */
    public String getAcademicStanding() {
        if (gpa >= 3.5) return "Dean's List";
        if (gpa >= 3.0) return "Good Standing";
        if (gpa >= 2.5) return "Satisfactory";
        if (gpa >= 2.0) return "Academic Warning";
        return "Academic Probation";
    }
    
    /**
     * Gets the number of completed courses
     * 
     * @return number of completed courses
     */
    public int getCompletedCourses() {
        return grades.size();
    }
    
    // Getters and Setters
    public String getStudentId() {
        return studentId;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        validateEmail(email);
        this.email = email;
    }
    
    public String getMajor() {
        return major;
    }
    
    public void setMajor(String major) {
        this.major = major;
    }
    
    public int getEnrollmentYear() {
        return enrollmentYear;
    }
    
    public void setEnrollmentYear(int enrollmentYear) {
        this.enrollmentYear = enrollmentYear;
    }
    
    public StudentStatus getStatus() {
        return status;
    }
    
    public void setStatus(StudentStatus status) {
        this.status = status;
    }
    
    public List<String> getEnrolledCourses() {
        return new ArrayList<>(enrolledCourses);
    }
    
    public Map<String, Grade> getGrades() {
        return new HashMap<>(grades);
    }
    
    public double getGpa() {
        return gpa;
    }
    
    @Override
    public String toString() {
        return String.format("Student{id='%s', name='%s', major='%s', status=%s, gpa=%.2f}", 
                           studentId, fullName, major, status, gpa);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Student student = (Student) obj;
        return studentId.equals(student.studentId);
    }
    
    @Override
    public int hashCode() {
        return studentId.hashCode();
    }
}
