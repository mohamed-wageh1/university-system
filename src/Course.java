import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Course class representing a university course
 * 
 * Implements proper encapsulation and includes course information
 * such as enrolled students, prerequisites, and schedule.
 */
public class Course implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String courseId;
    private String courseName;
    private String description;
    private int creditHours;
    private String instructorId;
    private List<String> enrolledStudents;
    private List<String> prerequisites;
    private int maxCapacity;
    private String schedule;
    private String classroom;
    private CourseStatus status;
    private String semester;
    private int year;
    
    /**
     * Constructor for creating a new course
     * 
     * @param courseId - unique course identifier
     * @param courseName - name of the course
     * @param description - course description
     * @param creditHours - number of credit hours
     * @param instructorId - ID of the instructor teaching the course
     */
    public Course(String courseId, String courseName, String description, int creditHours, String instructorId) {
        validateCourseId(courseId);
        validateCreditHours(creditHours);
        
        this.courseId = courseId;
        this.courseName = courseName;
        this.description = description;
        this.creditHours = creditHours;
        this.instructorId = instructorId;
        this.enrolledStudents = new ArrayList<>();
        this.prerequisites = new ArrayList<>();
        this.maxCapacity = 30; // Default capacity
        this.status = CourseStatus.OPEN;
        this.year = java.time.Year.now().getValue();
    }
    
    // Validation methods
    private void validateCourseId(String courseId) {
        if (courseId == null || courseId.trim().isEmpty()) {
            throw new IllegalArgumentException("Course ID cannot be null or empty");
        }
    }
    
    private void validateCreditHours(int creditHours) {
        if (creditHours < 1 || creditHours > 6) {
            throw new IllegalArgumentException("Credit hours must be between 1 and 6");
        }
    }
    
    /**
     * Enrolls a student in the course
     * 
     * @param studentId - ID of the student to enroll
     * @return true if enrollment was successful, false otherwise
     */
    public boolean enrollStudent(String studentId) {
        if (status != CourseStatus.OPEN) {
            return false; // Course is not open for enrollment
        }
        
        if (enrolledStudents.size() >= maxCapacity) {
            return false; // Course is at capacity
        }
        
        if (!enrolledStudents.contains(studentId)) {
            enrolledStudents.add(studentId);
            
            // Close course if at capacity
            if (enrolledStudents.size() >= maxCapacity) {
                status = CourseStatus.FULL;
            }
            
            return true;
        }
        
        return false; // Student already enrolled
    }
    
    /**
     * Drops a student from the course
     * 
     * @param studentId - ID of the student to drop
     * @return true if drop was successful, false otherwise
     */
    public boolean dropStudent(String studentId) {
        if (enrolledStudents.remove(studentId)) {
            // Reopen course if it was full
            if (status == CourseStatus.FULL) {
                status = CourseStatus.OPEN;
            }
            return true;
        }
        return false;
    }
    
    /**
     * Adds a prerequisite course
     * 
     * @param prerequisiteCourseId - ID of the prerequisite course
     */
    public void addPrerequisite(String prerequisiteCourseId) {
        if (prerequisiteCourseId != null && !prerequisites.contains(prerequisiteCourseId)) {
            prerequisites.add(prerequisiteCourseId);
        }
    }
    
    /**
     * Removes a prerequisite course
     * 
     * @param prerequisiteCourseId - ID of the prerequisite course to remove
     * @return true if removal was successful
     */
    public boolean removePrerequisite(String prerequisiteCourseId) {
        return prerequisites.remove(prerequisiteCourseId);
    }
    
    /**
     * Checks if the course has available spots
     * 
     * @return true if there are available spots
     */
    public boolean hasAvailableSpots() {
        return enrolledStudents.size() < maxCapacity && status == CourseStatus.OPEN;
    }
    
    /**
     * Gets the number of available spots in the course
     * 
     * @return number of available spots
     */
    public int getAvailableSpots() {
        return Math.max(0, maxCapacity - enrolledStudents.size());
    }
    
    /**
     * Gets the enrollment percentage
     * 
     * @return enrollment percentage
     */
    public double getEnrollmentPercentage() {
        if (maxCapacity == 0) return 0.0;
        return (double) enrolledStudents.size() / maxCapacity * 100.0;
    }
    
    // Getters and Setters
    public String getCourseId() {
        return courseId;
    }
    
    public String getCourseName() {
        return courseName;
    }
    
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getCreditHours() {
        return creditHours;
    }
    
    public void setCreditHours(int creditHours) {
        validateCreditHours(creditHours);
        this.creditHours = creditHours;
    }
    
    public String getInstructorId() {
        return instructorId;
    }
    
    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }
    
    public List<String> getEnrolledStudents() {
        return new ArrayList<>(enrolledStudents);
    }
    
    public List<String> getPrerequisites() {
        return new ArrayList<>(prerequisites);
    }
    
    public int getMaxCapacity() {
        return maxCapacity;
    }
    
    public void setMaxCapacity(int maxCapacity) {
        if (maxCapacity < 1) {
            throw new IllegalArgumentException("Max capacity must be at least 1");
        }
        this.maxCapacity = maxCapacity;
        
        // Update status based on new capacity
        if (enrolledStudents.size() >= maxCapacity) {
            status = CourseStatus.FULL;
        } else if (status == CourseStatus.FULL) {
            status = CourseStatus.OPEN;
        }
    }
    
    public String getSchedule() {
        return schedule;
    }
    
    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }
    
    public String getClassroom() {
        return classroom;
    }
    
    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }
    
    public CourseStatus getStatus() {
        return status;
    }
    
    public void setStatus(CourseStatus status) {
        this.status = status;
    }
    
    public String getSemester() {
        return semester;
    }
    
    public void setSemester(String semester) {
        this.semester = semester;
    }
    
    public int getYear() {
        return year;
    }
    
    public void setYear(int year) {
        this.year = year;
    }
    
    public int getEnrollmentCount() {
        return enrolledStudents.size();
    }
    
    @Override
    public String toString() {
        return String.format("Course{id='%s', name='%s', credits=%d, enrolled=%d/%d, status=%s}", 
                           courseId, courseName, creditHours, enrolledStudents.size(), maxCapacity, status);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Course course = (Course) obj;
        return courseId.equals(course.courseId);
    }
    
    @Override
    public int hashCode() {
        return courseId.hashCode();
    }
}
