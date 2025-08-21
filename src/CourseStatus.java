/**
 * Enumeration defining course status in the university system
 */
public enum CourseStatus {
    OPEN("Open", "Course is open for enrollment"),
    FULL("Full", "Course has reached maximum capacity"),
    CLOSED("Closed", "Course is closed for enrollment"),
    CANCELLED("Cancelled", "Course has been cancelled"),
    IN_PROGRESS("In Progress", "Course is currently being taught"),
    COMPLETED("Completed", "Course has been completed");
    
    private final String displayName;
    private final String description;
    
    CourseStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Checks if students can enroll in courses with this status
     * 
     * @return true if enrollment is allowed
     */
    public boolean allowsEnrollment() {
        return this == OPEN;
    }
    
    /**
     * Checks if the course is active (students are attending)
     * 
     * @return true if course is active
     */
    public boolean isActive() {
        return this == IN_PROGRESS || this == OPEN || this == FULL;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
