import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Faculty class representing a university faculty member
 * 
 * Contains faculty information including courses taught,
 * department, and academic position.
 */
public class Faculty implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String facultyId;
    private String fullName;
    private String email;
    private String department;
    private String position;
    private List<String> coursesTaught;
    private String officeLocation;
    private String phoneNumber;
    
    /**
     * Constructor for creating a new faculty member
     * 
     * @param facultyId - unique faculty identifier
     * @param fullName - faculty member's full name
     * @param email - faculty member's email address
     * @param department - department of the faculty member
     * @param position - academic position
     */
    public Faculty(String facultyId, String fullName, String email, String department, String position) {
        validateFacultyId(facultyId);
        validateEmail(email);
        
        this.facultyId = facultyId;
        this.fullName = fullName;
        this.email = email;
        this.department = department;
        this.position = position;
        this.coursesTaught = new ArrayList<>();
    }
    
    // Validation methods
    private void validateFacultyId(String facultyId) {
        if (facultyId == null || facultyId.trim().isEmpty()) {
            throw new IllegalArgumentException("Faculty ID cannot be null or empty");
        }
    }
    
    private void validateEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address");
        }
    }
    
    /**
     * Assigns a course to the faculty member
     * 
     * @param courseId - ID of the course to assign
     * @return true if assignment was successful
     */
    public boolean assignCourse(String courseId) {
        if (courseId != null && !coursesTaught.contains(courseId)) {
            coursesTaught.add(courseId);
            return true;
        }
        return false;
    }
    
    /**
     * Removes a course assignment from the faculty member
     * 
     * @param courseId - ID of the course to remove
     * @return true if removal was successful
     */
    public boolean removeCourseAssignment(String courseId) {
        return coursesTaught.remove(courseId);
    }
    
    /**
     * Gets the current course load (number of courses taught)
     * 
     * @return number of courses currently taught
     */
    public int getCourseLoad() {
        return coursesTaught.size();
    }
    
    // Getters and Setters
    public String getFacultyId() {
        return facultyId;
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
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getPosition() {
        return position;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
    
    public List<String> getCoursesTaught() {
        return new ArrayList<>(coursesTaught);
    }
    
    public String getOfficeLocation() {
        return officeLocation;
    }
    
    public void setOfficeLocation(String officeLocation) {
        this.officeLocation = officeLocation;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    @Override
    public String toString() {
        return String.format("Faculty{id='%s', name='%s', department='%s', position='%s', courses=%d}", 
                           facultyId, fullName, department, position, coursesTaught.size());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Faculty faculty = (Faculty) obj;
        return facultyId.equals(faculty.facultyId);
    }
    
    @Override
    public int hashCode() {
        return facultyId.hashCode();
    }
}
