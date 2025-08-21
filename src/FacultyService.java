import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * FacultyService handles all faculty-related operations
 * 
 * This service provides functionality for faculty management,
 * course assignments, and faculty information.
 */
public class FacultyService {
    private Map<String, Faculty> faculty;
    private FileManager<Map<String, Faculty>> fileManager;
    private static final String FACULTY_FILE = "data/faculty.dat";
    
    public FacultyService() {
        this.fileManager = new FileManager<>();
        this.faculty = loadFaculty();
    }
    
    /**
     * Loads faculty from file or creates empty collection
     */
    @SuppressWarnings("unchecked")
    private Map<String, Faculty> loadFaculty() {
        try {
            Map<String, Faculty> loadedFaculty = fileManager.loadFromFile(FACULTY_FILE);
            return loadedFaculty != null ? loadedFaculty : new HashMap<>();
        } catch (Exception e) {
            System.err.println("Warning: Could not load faculty from file. Starting with empty faculty collection.");
            return new HashMap<>();
        }
    }
    
    /**
     * Saves faculty to file
     */
    private void saveFaculty() {
        try {
            fileManager.saveToFile(faculty, FACULTY_FILE);
        } catch (Exception e) {
            System.err.println("Error: Could not save faculty to file: " + e.getMessage());
        }
    }
    
    /**
     * Adds a new faculty member to the system
     * 
     * @param facultyMember - faculty member to add
     * @return true if faculty was added successfully, false if faculty ID already exists
     */
    public boolean addFaculty(Faculty facultyMember) {
        if (facultyMember == null || faculty.containsKey(facultyMember.getFacultyId())) {
            return false;
        }
        
        faculty.put(facultyMember.getFacultyId(), facultyMember);
        saveFaculty();
        return true;
    }
    
    /**
     * Updates an existing faculty member's information
     * 
     * @param facultyId - ID of faculty member to update
     * @param updatedFaculty - updated faculty information
     * @return true if update was successful, false if faculty not found
     */
    public boolean updateFaculty(String facultyId, Faculty updatedFaculty) {
        if (facultyId == null || updatedFaculty == null || !faculty.containsKey(facultyId)) {
            return false;
        }
        
        faculty.put(facultyId, updatedFaculty);
        saveFaculty();
        return true;
    }
    
    /**
     * Removes a faculty member from the system
     * 
     * @param facultyId - ID of faculty member to remove
     * @return true if faculty was removed, false if not found
     */
    public boolean removeFaculty(String facultyId) {
        if (facultyId == null) {
            return false;
        }
        
        Faculty removed = faculty.remove(facultyId);
        if (removed != null) {
            saveFaculty();
            return true;
        }
        
        return false;
    }
    
    /**
     * Gets a faculty member by ID
     * 
     * @param facultyId - ID of faculty member to find
     * @return Faculty object or null if not found
     */
    public Faculty getFaculty(String facultyId) {
        return faculty.get(facultyId);
    }
    
    /**
     * Gets all faculty members
     * 
     * @return List of all faculty members
     */
    public List<Faculty> getAllFaculty() {
        return new ArrayList<>(faculty.values());
    }
    
    /**
     * Assigns a course to a faculty member
     * 
     * @param facultyId - ID of faculty member
     * @param courseId - ID of course to assign
     * @return true if assignment was successful, false otherwise
     */
    public boolean assignCourse(String facultyId, String courseId) {
        Faculty facultyMember = faculty.get(facultyId);
        if (facultyMember != null) {
            if (facultyMember.assignCourse(courseId)) {
                saveFaculty();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Removes a course assignment from a faculty member
     * 
     * @param facultyId - ID of faculty member
     * @param courseId - ID of course to remove
     * @return true if removal was successful, false otherwise
     */
    public boolean removeCourseAssignment(String facultyId, String courseId) {
        Faculty facultyMember = faculty.get(facultyId);
        if (facultyMember != null) {
            if (facultyMember.removeCourseAssignment(courseId)) {
                saveFaculty();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Gets faculty members by department
     * 
     * @param department - department to filter by
     * @return List of faculty members in the specified department
     */
    public List<Faculty> getFacultyByDepartment(String department) {
        return faculty.values().stream()
                .filter(f -> department.equalsIgnoreCase(f.getDepartment()))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets faculty members teaching a specific course
     * 
     * @param courseId - ID of course
     * @return List of faculty members teaching the course
     */
    public List<Faculty> getFacultyTeachingCourse(String courseId) {
        return faculty.values().stream()
                .filter(f -> f.getCoursesTaught().contains(courseId))
                .collect(Collectors.toList());
    }
    
    /**
     * Searches faculty by name (partial match)
     * 
     * @param name - name to search for
     * @return List of faculty members whose names contain the search term
     */
    public List<Faculty> searchFacultyByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String searchTerm = name.toLowerCase().trim();
        return faculty.values().stream()
                .filter(f -> f.getFullName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets total number of faculty members
     * 
     * @return number of faculty members
     */
    public int getFacultyCount() {
        return faculty.size();
    }
    
    /**
     * Gets statistics about faculty by department
     * 
     * @return Map of department names to faculty counts
     */
    public Map<String, Integer> getFacultyStatsByDepartment() {
        Map<String, Integer> stats = new HashMap<>();
        for (Faculty f : faculty.values()) {
            String dept = f.getDepartment();
            stats.put(dept, stats.getOrDefault(dept, 0) + 1);
        }
        return stats;
    }
    
    /**
     * Calculates average course load across all faculty
     * 
     * @return average course load
     */
    public double getAverageCourseLoad() {
        return faculty.values().stream()
                .mapToDouble(Faculty::getCourseLoad)
                .average()
                .orElse(0.0);
    }
    
    /**
     * Gets total number of courses assigned to all faculty
     * 
     * @return total courses assigned
     */
    public int getTotalCoursesAssigned() {
        return faculty.values().stream()
                .mapToInt(Faculty::getCourseLoad)
                .sum();
    }
}
