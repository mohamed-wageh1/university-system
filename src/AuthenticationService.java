import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * AuthenticationService handles user authentication and session management
 * 
 * This service provides secure login functionality, user registration,
 * and password management with proper validation.
 * Now uses SQLite database instead of file storage.
 */
public class AuthenticationService {
    private Map<String, User> users;
    private User currentUser;
    private DatabaseHandler dbHandler;
    
    public AuthenticationService() {
        this.dbHandler = new DatabaseHandler();
        this.users = loadUsers();
        this.currentUser = null;
        
        // Initialize sample data if database is empty
        if (users.isEmpty()) {
            dbHandler.initializeSampleData();
            this.users = loadUsers();
        }
    }
    
    /**
     * Loads users from database and converts to map for compatibility
     */
    private Map<String, User> loadUsers() {
        try {
            List<User> userList = dbHandler.getAllUsers();
            Map<String, User> userMap = new HashMap<>();
            for (User user : userList) {
                userMap.put(user.getUsername(), user);
            }
            return userMap;
        } catch (Exception e) {
            System.err.println("Warning: Could not load users from database. Starting with empty user collection.");
            return new HashMap<>();
        }
    }
    
    /**
     * Saves user to database
     */
    private void saveUser(User user) {
        try {
            dbHandler.insertUser(user);
        } catch (Exception e) {
            System.err.println("Error: Could not save user to database: " + e.getMessage());
        }
    }
    
    /**
     * Authenticates a user with username and password
     * 
     * @param username - user's username
     * @param password - user's password
     * @return true if authentication successful, false otherwise
     */
    public boolean login(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        
        User user = dbHandler.findUserByCredentials(username, password);
        if (user != null && user.isActive()) {
            currentUser = user;
            user.updateLastLogin();
            return true;
        }
        
        return false;
    }
    
    /**
     * Logs out the current user
     */
    public void logout() {
        currentUser = null;
    }
    
    /**
     * Registers a new user in the system
     * 
     * @param user - user to register
     * @return true if registration successful, false if username already exists
     */
    public boolean registerUser(User user) {
        if (user == null || users.containsKey(user.getUsername())) {
            return false;
        }
        
        boolean success = dbHandler.insertUser(user);
        if (success) {
            users.put(user.getUsername(), user);
        }
        return success;
    }
    
    /**
     * Changes the current user's password
     * 
     * @param oldPassword - current password
     * @param newPassword - new password
     * @return true if password change successful, false otherwise
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        if (currentUser == null) {
            return false;
        }
        
        if (currentUser.changePassword(oldPassword, newPassword)) {
            // Update user in database
            dbHandler.insertUser(currentUser);
            return true;
        }
        
        return false;
    }
    
    /**
     * Resets a user's password (admin function)
     * 
     * @param username - username of user to reset
     * @param newPassword - new password
     * @return true if reset successful, false otherwise
     */
    public boolean resetPassword(String username, String newPassword) {
        if (currentUser == null || !currentUser.getRole().canManageUsers()) {
            return false; // Only system admins can reset passwords
        }
        
        User user = users.get(username);
        if (user != null) {
            try {
                // Create a new user with updated password
                User updatedUser = new User(user.getUsername(), newPassword, user.getRole(), user.getFullName());
                updatedUser.setActive(user.isActive());
                users.put(username, updatedUser);
                dbHandler.insertUser(updatedUser);
                return true;
            } catch (IllegalArgumentException e) {
                return false; // Invalid password format
            }
        }
        
        return false;
    }
    
    /**
     * Deactivates a user account (admin function)
     * 
     * @param username - username to deactivate
     * @return true if deactivation successful, false otherwise
     */
    public boolean deactivateUser(String username) {
        if (currentUser == null || !currentUser.getRole().canManageUsers()) {
            return false;
        }
        
        User user = users.get(username);
        if (user != null && !user.equals(currentUser)) { // Can't deactivate self
            user.setActive(false);
            dbHandler.insertUser(user);
            return true;
        }
        
        return false;
    }
    
    /**
     * Activates a user account (admin function)
     * 
     * @param username - username to activate
     * @return true if activation successful, false otherwise
     */
    public boolean activateUser(String username) {
        if (currentUser == null || !currentUser.getRole().canManageUsers()) {
            return false;
        }
        
        User user = users.get(username);
        if (user != null) {
            user.setActive(true);
            dbHandler.insertUser(user);
            return true;
        }
        
        return false;
    }
    
    /**
     * Gets a user by username (admin function)
     * 
     * @param username - username to find
     * @return User object or null if not found
     */
    public User getUser(String username) {
        if (currentUser == null || (!currentUser.getRole().isAdmin() && !currentUser.getUsername().equals(username))) {
            return null; // Only admins can view other users, users can view themselves
        }
        
        return users.get(username);
    }
    
    /**
     * Gets all users (admin function)
     * 
     * @return Map of all users or null if not authorized
     */
    public Map<String, User> getAllUsers() {
        if (currentUser == null || !currentUser.getRole().canManageUsers()) {
            return null;
        }
        
        return new HashMap<>(users);
    }
    
    /**
     * Checks if a user is currently logged in
     * 
     * @return true if user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Gets the currently logged in user
     * 
     * @return current user or null if not logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Checks if current user has specific role
     * 
     * @param role - role to check
     * @return true if user has the role, false otherwise
     */
    public boolean hasRole(UserRole role) {
        return currentUser != null && currentUser.getRole() == role;
    }
    
    /**
     * Checks if current user has admin privileges
     * 
     * @return true if user is admin, false otherwise
     */
    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole().isAdmin();
    }
    
    /**
     * Gets the number of registered users
     * 
     * @return number of users
     */
    public int getUserCount() {
        return users.size();
    }
    
    /**
     * Gets the number of active users
     * 
     * @return number of active users
     */
    public int getActiveUserCount() {
        return (int) users.values().stream().filter(User::isActive).count();
    }
}
