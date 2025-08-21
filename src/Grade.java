import java.io.Serializable;

/**
 * Grade class representing a student's grade in a course
 * 
 * Includes percentage score, letter grade, and grade points
 * for GPA calculation.
 */
public class Grade implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private double percentage;
    private String letterGrade;
    private double gradePoints;
    
    /**
     * Constructor for creating a new grade
     * 
     * @param percentage - percentage score (0-100)
     */
    public Grade(double percentage) {
        validatePercentage(percentage);
        this.percentage = percentage;
        this.letterGrade = calculateLetterGrade(percentage);
        this.gradePoints = calculateGradePoints(letterGrade);
    }
    
    /**
     * Constructor for creating a grade with letter grade
     * 
     * @param letterGrade - letter grade (A, B, C, D, F)
     */
    public Grade(String letterGrade) {
        validateLetterGrade(letterGrade);
        this.letterGrade = letterGrade.toUpperCase();
        this.gradePoints = calculateGradePoints(this.letterGrade);
        this.percentage = calculatePercentageFromLetter(this.letterGrade);
    }
    
    // Validation methods
    private void validatePercentage(double percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }
    }
    
    private void validateLetterGrade(String letterGrade) {
        if (letterGrade == null || !letterGrade.matches("^[ABCDF][+-]?$")) {
            throw new IllegalArgumentException("Invalid letter grade format");
        }
    }
    
    /**
     * Calculates letter grade from percentage
     * 
     * @param percentage - percentage score
     * @return corresponding letter grade
     */
    private String calculateLetterGrade(double percentage) {
        if (percentage >= 97) return "A+";
        if (percentage >= 93) return "A";
        if (percentage >= 90) return "A-";
        if (percentage >= 87) return "B+";
        if (percentage >= 83) return "B";
        if (percentage >= 80) return "B-";
        if (percentage >= 77) return "C+";
        if (percentage >= 73) return "C";
        if (percentage >= 70) return "C-";
        if (percentage >= 67) return "D+";
        if (percentage >= 63) return "D";
        if (percentage >= 60) return "D-";
        return "F";
    }
    
    /**
     * Calculates grade points from letter grade
     * 
     * @param letterGrade - letter grade
     * @return corresponding grade points
     */
    private double calculateGradePoints(String letterGrade) {
        switch (letterGrade) {
            case "A+": case "A": return 4.0;
            case "A-": return 3.7;
            case "B+": return 3.3;
            case "B": return 3.0;
            case "B-": return 2.7;
            case "C+": return 2.3;
            case "C": return 2.0;
            case "C-": return 1.7;
            case "D+": return 1.3;
            case "D": return 1.0;
            case "D-": return 0.7;
            case "F": return 0.0;
            default: return 0.0;
        }
    }
    
    /**
     * Calculates approximate percentage from letter grade
     * 
     * @param letterGrade - letter grade
     * @return corresponding percentage
     */
    private double calculatePercentageFromLetter(String letterGrade) {
        switch (letterGrade) {
            case "A+": return 98.0;
            case "A": return 95.0;
            case "A-": return 91.5;
            case "B+": return 88.5;
            case "B": return 85.0;
            case "B-": return 81.5;
            case "C+": return 78.5;
            case "C": return 75.0;
            case "C-": return 71.5;
            case "D+": return 68.5;
            case "D": return 65.0;
            case "D-": return 61.5;
            case "F": return 50.0;
            default: return 0.0;
        }
    }
    
    /**
     * Checks if this is a passing grade
     * 
     * @return true if grade is D- or better
     */
    public boolean isPassing() {
        return gradePoints >= 0.7;
    }
    
    /**
     * Gets the quality level of the grade
     * 
     * @return quality description
     */
    public String getQualityLevel() {
        if (gradePoints >= 3.7) return "Excellent";
        if (gradePoints >= 3.0) return "Good";
        if (gradePoints >= 2.0) return "Satisfactory";
        if (gradePoints >= 1.0) return "Below Average";
        return "Failing";
    }
    
    // Getters and Setters
    public double getPercentage() {
        return percentage;
    }
    
    public String getLetterGrade() {
        return letterGrade;
    }
    
    public double getGradePoints() {
        return gradePoints;
    }
    
    @Override
    public String toString() {
        return String.format("Grade{letter='%s', percentage=%.1f, points=%.1f}", 
                           letterGrade, percentage, gradePoints);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Grade grade = (Grade) obj;
        return Double.compare(grade.percentage, percentage) == 0 &&
               letterGrade.equals(grade.letterGrade);
    }
    
    @Override
    public int hashCode() {
        return letterGrade.hashCode();
    }
}
