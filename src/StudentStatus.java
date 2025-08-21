/**
 * Enumeration defining student status in the university system
 */
public enum StudentStatus {
    ACTIVE("Active", "Currently enrolled and attending classes"),
    INACTIVE("Inactive", "Not currently attending classes"),
    GRADUATED("Graduated", "Successfully completed degree requirements"),
    SUSPENDED("Suspended", "Temporarily suspended from the university"),
    EXPELLED("Expelled", "Permanently removed from the university"),
    ON_LEAVE("On Leave", "Temporarily away from studies");
    
    private final String displayName;
    private final String description;
    
    StudentStatus(String displayName, String description) {
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
     * Checks if the student can enroll in courses
     * 
     * @return true if student can enroll in courses
     */
    public boolean canEnroll() {
        return this == ACTIVE;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
