/**
 * Enumeration defining the different user roles in the university system
 * 
 * Each role has specific permissions and access levels within the system.
 */
public enum UserRole {
    STUDENT("Student", "Can view courses, grades, and manage enrollment"),
    FACULTY("Faculty", "Can manage courses, grades, and view student information"),
    ADMIN_STAFF("Administrative Staff", "Can manage students, courses, and generate reports"),
    SYSTEM_ADMIN("System Administrator", "Full system access and user management");
    
    private final String displayName;
    private final String description;
    
    UserRole(String displayName, String description) {
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
     * Checks if this role has administrative privileges
     * 
     * @return true if role is admin staff or system admin
     */
    public boolean isAdmin() {
        return this == ADMIN_STAFF || this == SYSTEM_ADMIN;
    }
    
    /**
     * Checks if this role can manage users
     * 
     * @return true if role is system admin
     */
    public boolean canManageUsers() {
        return this == SYSTEM_ADMIN;
    }
    
    /**
     * Checks if this role can manage courses
     * 
     * @return true if role is faculty, admin staff, or system admin
     */
    public boolean canManageCourses() {
        return this == FACULTY || this == ADMIN_STAFF || this == SYSTEM_ADMIN;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
