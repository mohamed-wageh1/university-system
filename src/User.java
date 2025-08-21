import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * User class representing a system user with authentication capabilities
 * 
 * This class implements proper encapsulation and includes validation
 * for user data according to system requirements.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String username;
    private String password;
    private UserRole role;
    private String fullName;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private boolean isActive;
    
    /**
     * Constructor for creating a new user
     * 
     * @param username - unique username (minimum 3 characters)
     * @param password - password (minimum 6 characters)
     * @param role - user role in the system
     * @param fullName - full name of the user
     */
    public User(String username, String password, UserRole role, String fullName) {
        validateUsername(username);
        validatePassword(password);
        
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }
    
    // Input validation methods
    private void validateUsername(String username) {
        if (username == null || username.trim().length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters long");
        }
    }
    
    private void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
    }
    
    /**
     * Verifies if the provided password matches the user's password
     * 
     * @param password - password to verify
     * @return true if password matches, false otherwise
     */
    public boolean verifyPassword(String password) {
        return this.password != null && this.password.equals(password);
    }
    
    /**
     * Updates the last login timestamp
     */
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }
    
    /**
     * Changes the user's password with validation
     * 
     * @param oldPassword - current password for verification
     * @param newPassword - new password to set
     * @return true if password was changed successfully, false otherwise
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        if (!verifyPassword(oldPassword)) {
            return false;
        }
        
        try {
            validatePassword(newPassword);
            this.password = newPassword;
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
    }
    
    @Override
    public String toString() {
        return String.format("User{username='%s', role=%s, fullName='%s', active=%s}", 
                           username, role, fullName, isActive);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return username.equals(user.username);
    }
    
    @Override
    public int hashCode() {
        return username.hashCode();
    }
}
