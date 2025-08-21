import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * StudentService handles all student-related operations
 * 
 * This service provides functionality for student management,
 * enrollment, grades, and academic records.
 */
public class StudentService {
    private Map<String, Student> students;
    private FileManager<Map<String, Student>> fileManager;
    private static final String STUDENTS_FILE = "data/students.dat";
    
    public StudentService() {
        this.fileManager = new FileManager<>();
        this.students = loadStudents();
    }
    
    /**
     * Loads students from file or creates empty collection
     */
    @SuppressWarnings("unchecked")
    private Map<String, Student> loadStudents() {
        try {
            Map<String, Student> loadedStudents = fileManager.loadFromFile(STUDENTS_FILE);
            return loadedStudents != null ? loadedStudents : new HashMap<>();
        } catch (Exception e) {
            System.err.println("Warning: Could not load students from file. Starting with empty student collection.");
            return new HashMap<>();
        }
    }
    
    /**
     * Saves students to file
     */
    private void saveStudents() {
        try {
            fileManager.saveToFile(students, STUDENTS_FILE);
        } catch (Exception e) {
            System.err.println("Error: Could not save students to file: " + e.getMessage());
        }
    }
    
    /**
     * Adds a new student to the system
     * 
     * @param student - student to add
     * @return true if student was added successfully, false if student ID already exists
     */
    public boolean addStudent(Student student) {
        if (student == null || students.containsKey(student.getStudentId())) {
            return false;
        }
        
        students.put(student.getStudentId(), student);
        saveStudents();
        return true;
    }
    
    /**
     * Updates an existing student's information
     * 
     * @param studentId - ID of student to update
     * @param updatedStudent - updated student information
     * @return true if update was successful, false if student not found
     */
    public boolean updateStudent(String studentId, Student updatedStudent) {
        if (studentId == null || updatedStudent == null || !students.containsKey(studentId)) {
            return false;
        }
        
        students.put(studentId, updatedStudent);
        saveStudents();
        return true;
    }
    
    /**
     * Removes a student from the system
     * 
     * @param studentId - ID of student to remove
     * @return true if student was removed, false if not found
     */
    public boolean removeStudent(String studentId) {
        if (studentId == null) {
            return false;
        }
        
        Student removed = students.remove(studentId);
        if (removed != null) {
            saveStudents();
            return true;
        }
        
        return false;
    }
    
    /**
     * Gets a student by ID
     * 
     * @param studentId - ID of student to find
     * @return Student object or null if not found
     */
    public Student getStudent(String studentId) {
        return students.get(studentId);
    }
    
    /**
     * Gets all students
     * 
     * @return List of all students
     */
    public List<Student> getAllStudents() {
        return new ArrayList<>(students.values());
    }
    
    /**
     * Enrolls a student in a course
     * 
     * @param studentId - ID of student
     * @param courseId - ID of course
     * @return true if enrollment was successful, false otherwise
     */
    public boolean enrollStudentInCourse(String studentId, String courseId) {
        Student student = students.get(studentId);
        if (student != null && student.getStatus().canEnroll()) {
            if (student.enrollInCourse(courseId)) {
                saveStudents();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Drops a student from a course
     * 
     * @param studentId - ID of student
     * @param courseId - ID of course
     * @return true if drop was successful, false otherwise
     */
    public boolean dropStudentFromCourse(String studentId, String courseId) {
        Student student = students.get(studentId);
        if (student != null) {
            if (student.dropCourse(courseId)) {
                saveStudents();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Adds a grade for a student in a course
     * 
     * @param studentId - ID of student
     * @param courseId - ID of course
     * @param grade - grade to assign
     * @return true if grade was added successfully, false otherwise
     */
    public boolean addGrade(String studentId, String courseId, Grade grade) {
        Student student = students.get(studentId);
        if (student != null) {
            try {
                student.addGrade(courseId, grade);
                saveStudents();
                return true;
            } catch (IllegalStateException e) {
                return false; // Student not enrolled in course
            }
        }
        return false;
    }
    
    /**
     * Gets students by major
     * 
     * @param major - major to filter by
     * @return List of students in the specified major
     */
    public List<Student> getStudentsByMajor(String major) {
        return students.values().stream()
                .filter(student -> major.equalsIgnoreCase(student.getMajor()))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets students by enrollment year
     * 
     * @param year - enrollment year to filter by
     * @return List of students enrolled in the specified year
     */
    public List<Student> getStudentsByEnrollmentYear(int year) {
        return students.values().stream()
                .filter(student -> student.getEnrollmentYear() == year)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets students by status
     * 
     * @param status - status to filter by
     * @return List of students with the specified status
     */
    public List<Student> getStudentsByStatus(StudentStatus status) {
        return students.values().stream()
                .filter(student -> student.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets students enrolled in a specific course
     * 
     * @param courseId - ID of course
     * @return List of students enrolled in the course
     */
    public List<Student> getStudentsInCourse(String courseId) {
        return students.values().stream()
                .filter(student -> student.getEnrolledCourses().contains(courseId))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets students with GPA above a threshold
     * 
     * @param gpaThreshold - minimum GPA
     * @return List of students with GPA above threshold
     */
    public List<Student> getStudentsWithGpaAbove(double gpaThreshold) {
        return students.values().stream()
                .filter(student -> student.getGpa() >= gpaThreshold)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets students on academic probation (GPA below 2.0)
     * 
     * @return List of students on probation
     */
    public List<Student> getStudentsOnProbation() {
        return getStudentsWithGpaBetween(0.0, 2.0);
    }
    
    /**
     * Gets students with GPA between specified range
     * 
     * @param minGpa - minimum GPA
     * @param maxGpa - maximum GPA
     * @return List of students within GPA range
     */
    public List<Student> getStudentsWithGpaBetween(double minGpa, double maxGpa) {
        return students.values().stream()
                .filter(student -> student.getGpa() >= minGpa && student.getGpa() <= maxGpa)
                .collect(Collectors.toList());
    }
    
    /**
     * Updates a student's status
     * 
     * @param studentId - ID of student
     * @param status - new status
     * @return true if status was updated, false otherwise
     */
    public boolean updateStudentStatus(String studentId, StudentStatus status) {
        Student student = students.get(studentId);
        if (student != null) {
            student.setStatus(status);
            saveStudents();
            return true;
        }
        return false;
    }
    
    /**
     * Searches students by name (partial match)
     * 
     * @param name - name to search for
     * @return List of students whose names contain the search term
     */
    public List<Student> searchStudentsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String searchTerm = name.toLowerCase().trim();
        return students.values().stream()
                .filter(student -> student.getFullName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets total number of students
     * 
     * @return number of students
     */
    public int getStudentCount() {
        return students.size();
    }
    
    /**
     * Gets number of active students
     * 
     * @return number of active students
     */
    public int getActiveStudentCount() {
        return (int) students.values().stream()
                .filter(student -> student.getStatus() == StudentStatus.ACTIVE)
                .count();
    }
    
    /**
     * Calculates average GPA of all students
     * 
     * @return average GPA
     */
    public double getAverageGpa() {
        return students.values().stream()
                .mapToDouble(Student::getGpa)
                .average()
                .orElse(0.0);
    }
}
