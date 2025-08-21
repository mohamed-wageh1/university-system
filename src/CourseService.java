import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * CourseService handles all course-related operations
 * 
 * This service provides functionality for course management,
 * enrollment, and course information.
 */
public class CourseService {
    private Map<String, Course> courses;
    private FileManager<Map<String, Course>> fileManager;
    private static final String COURSES_FILE = "data/courses.dat";
    
    public CourseService() {
        this.fileManager = new FileManager<>();
        this.courses = loadCourses();
    }
    
    /**
     * Loads courses from file or creates empty collection
     */
    @SuppressWarnings("unchecked")
    private Map<String, Course> loadCourses() {
        try {
            Map<String, Course> loadedCourses = fileManager.loadFromFile(COURSES_FILE);
            return loadedCourses != null ? loadedCourses : new HashMap<>();
        } catch (Exception e) {
            System.err.println("Warning: Could not load courses from file. Starting with empty course collection.");
            return new HashMap<>();
        }
    }
    
    /**
     * Saves courses to file
     */
    private void saveCourses() {
        try {
            fileManager.saveToFile(courses, COURSES_FILE);
        } catch (Exception e) {
            System.err.println("Error: Could not save courses to file: " + e.getMessage());
        }
    }
    
    /**
     * Adds a new course to the system
     * 
     * @param course - course to add
     * @return true if course was added successfully, false if course ID already exists
     */
    public boolean addCourse(Course course) {
        if (course == null || courses.containsKey(course.getCourseId())) {
            return false;
        }
        
        courses.put(course.getCourseId(), course);
        saveCourses();
        return true;
    }
    
    /**
     * Updates an existing course's information
     * 
     * @param courseId - ID of course to update
     * @param updatedCourse - updated course information
     * @return true if update was successful, false if course not found
     */
    public boolean updateCourse(String courseId, Course updatedCourse) {
        if (courseId == null || updatedCourse == null || !courses.containsKey(courseId)) {
            return false;
        }
        
        courses.put(courseId, updatedCourse);
        saveCourses();
        return true;
    }
    
    /**
     * Removes a course from the system
     * 
     * @param courseId - ID of course to remove
     * @return true if course was removed, false if not found
     */
    public boolean removeCourse(String courseId) {
        if (courseId == null) {
            return false;
        }
        
        Course removed = courses.remove(courseId);
        if (removed != null) {
            saveCourses();
            return true;
        }
        
        return false;
    }
    
    /**
     * Gets a course by ID
     * 
     * @param courseId - ID of course to find
     * @return Course object or null if not found
     */
    public Course getCourse(String courseId) {
        return courses.get(courseId);
    }
    
    /**
     * Gets all courses
     * 
     * @return List of all courses
     */
    public List<Course> getAllCourses() {
        return new ArrayList<>(courses.values());
    }
    
    /**
     * Gets courses available for enrollment
     * 
     * @return List of courses with status OPEN
     */
    public List<Course> getAvailableCourses() {
        return courses.values().stream()
                .filter(course -> course.getStatus() == CourseStatus.OPEN)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets courses by status
     * 
     * @param status - status to filter by
     * @return List of courses with the specified status
     */
    public List<Course> getCoursesByStatus(CourseStatus status) {
        return courses.values().stream()
                .filter(course -> course.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets courses taught by a specific instructor
     * 
     * @param instructorId - ID of instructor
     * @return List of courses taught by the instructor
     */
    public List<Course> getCoursesByInstructor(String instructorId) {
        return courses.values().stream()
                .filter(course -> instructorId.equals(course.getInstructorId()))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets courses by credit hours
     * 
     * @param creditHours - number of credit hours
     * @return List of courses with the specified credit hours
     */
    public List<Course> getCoursesByCreditHours(int creditHours) {
        return courses.values().stream()
                .filter(course -> course.getCreditHours() == creditHours)
                .collect(Collectors.toList());
    }
    
    /**
     * Searches courses by name (partial match)
     * 
     * @param name - name to search for
     * @return List of courses whose names contain the search term
     */
    public List<Course> searchCoursesByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String searchTerm = name.toLowerCase().trim();
        return courses.values().stream()
                .filter(course -> course.getCourseName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }
    
    /**
     * Enrolls a student in a course
     * 
     * @param courseId - ID of course
     * @param studentId - ID of student
     * @return true if enrollment was successful, false otherwise
     */
    public boolean enrollStudent(String courseId, String studentId) {
        Course course = courses.get(courseId);
        if (course != null) {
            if (course.enrollStudent(studentId)) {
                saveCourses();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Drops a student from a course
     * 
     * @param courseId - ID of course
     * @param studentId - ID of student
     * @return true if drop was successful, false otherwise
     */
    public boolean dropStudent(String courseId, String studentId) {
        Course course = courses.get(courseId);
        if (course != null) {
            if (course.dropStudent(studentId)) {
                saveCourses();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Gets course statistics
     * 
     * @return Map containing various course statistics
     */
    public Map<String, Object> getCourseStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalCourses", courses.size());
        stats.put("activeCourses", getCoursesByStatus(CourseStatus.IN_PROGRESS).size());
        stats.put("availableCourses", getCoursesByStatus(CourseStatus.OPEN).size());
        stats.put("fullCourses", getCoursesByStatus(CourseStatus.FULL).size());
        
        int totalEnrollments = courses.values().stream()
                .mapToInt(Course::getEnrollmentCount)
                .sum();
        stats.put("totalEnrollments", totalEnrollments);
        
        double averageEnrollment = courses.isEmpty() ? 0.0 : (double) totalEnrollments / courses.size();
        stats.put("averageEnrollment", averageEnrollment);
        
        return stats;
    }
    
    /**
     * Gets total number of courses
     * 
     * @return number of courses
     */
    public int getCourseCount() {
        return courses.size();
    }
}
