import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * FileManager handles file I/O operations for data persistence
 * 
 * This class provides generic methods for saving and loading
 * serializable objects to/from files.
 */
public class FileManager<T> {
    
    /**
     * Saves an object to a file using serialization
     * 
     * @param data - object to save
     * @param filename - name of the file to save to
     * @throws IOException if file operation fails
     */
    public void saveToFile(T data, String filename) throws IOException {
        // Create directory if it doesn't exist
        Path filePath = Paths.get(filename);
        Path parentDir = filePath.getParent();
        
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
        
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filename))) {
            oos.writeObject(data);
        }
    }
    
    /**
     * Loads an object from a file using deserialization
     * 
     * @param filename - name of the file to load from
     * @return the loaded object, or null if file doesn't exist or is empty
     * @throws IOException if file operation fails
     * @throws ClassNotFoundException if class cannot be found during deserialization
     */
    @SuppressWarnings("unchecked")
    public T loadFromFile(String filename) throws IOException, ClassNotFoundException {
        File file = new File(filename);
        
        // Return null if file doesn't exist or is empty
        if (!file.exists() || file.length() == 0) {
            return null;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filename))) {
            return (T) ois.readObject();
        }
    }
    
    /**
     * Checks if a file exists
     * 
     * @param filename - name of the file to check
     * @return true if file exists, false otherwise
     */
    public boolean fileExists(String filename) {
        return Files.exists(Paths.get(filename));
    }
    
    /**
     * Deletes a file
     * 
     * @param filename - name of the file to delete
     * @return true if file was deleted, false otherwise
     */
    public boolean deleteFile(String filename) {
        try {
            return Files.deleteIfExists(Paths.get(filename));
        } catch (IOException e) {
            System.err.println("Error deleting file: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Creates a backup of a file
     * 
     * @param filename - name of the file to backup
     * @param backupFilename - name of the backup file
     * @throws IOException if backup operation fails
     */
    public void backupFile(String filename, String backupFilename) throws IOException {
        if (fileExists(filename)) {
            Files.copy(Paths.get(filename), Paths.get(backupFilename));
        }
    }
}
