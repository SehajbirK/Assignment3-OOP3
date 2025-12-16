package appDomain;

import implementations.BSTree;
import implementations.BSTreeNode;
import java.io.*;
import utilities.Iterator;


/**
 * The {@code WordTracker} application scans and parses text files to track word occurrences
 * and generates reports in multiple formats.
 * <p>
 * The program:
 * <ol>
 *   <li>Loads a previously saved word repository in a binary file</li>
 *   <li>Scans an input text file and updates word occurrences</li>
 *   <li>Persists the updated repository using serialization</li>
 *   <li>Generates a report based on a user-specified option</li>
 * </ol>
 *
 * The repository is stored between runs using object serialization.
 */
public class WordTracker {

    private static final String REPO_FILE = "repository.ser";

    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Usage: java -jar WordTracker.jar <input.txt> -pf|-pl|-po [-foutput.txt]");
            return;
        }

        String inputFile = null;
        String option = null;
        String outputFile = null;

        // Parse arguments
        for (String arg : args) {
            if (arg.startsWith("-f")) {
                outputFile = arg.substring(2);
            } else if (arg.equals("-pf") || arg.equals("-pl") || arg.equals("-po")) {
                option = arg;
            } else {
                inputFile = arg;
            }
        }

        if (inputFile == null || option == null) {
            System.out.println("Error: Missing input file or option (-pf/-pl/-po).");
            return;
        }

        // Load previous tree
        BSTree<Word> tree = loadRepository();

        // Scan new file
        scanFile(inputFile, tree);

        // Save updated tree
        saveRepository(tree);

        // Write report
        try (PrintWriter writer = (outputFile != null)
                ? new PrintWriter(new FileWriter(outputFile))
                : new PrintWriter(System.out)) {

            if (outputFile != null) {
                System.out.println("Writing output to: " + new File(outputFile).getAbsolutePath());
            }

            generateReport(tree, option, writer);
            writer.flush();

        } catch (IOException e) {
            System.out.println("Error writing output.");
            e.printStackTrace();
        }
    }

    // -------------------- FILE SCANNING --------------------
    /**
     * Scans a text file and records word occurrences in the provided tree.
     * <p>
     * Words are normalized by removing non-alphabetic characters
     * and are treated case-insensitively.
     *
     * @param filename the file to scan
     * @param tree the binary search tree storing {@code Word} objects
     */
    private static void scanFile(String filename, BSTree<Word> tree) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {

            String line;
            int lineNumber = 0;
            
            // Read the file line by line
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                // Split the line into individual tokens using whitespace
                String[] tokens = line.split("\\s+");

                for (String t : tokens) {
                    if (t.isEmpty()) continue;
                    
                    // Remove non-alphabetic characters from the token
                    String cleaned = t.replaceAll("[^a-zA-Z]", "");
                    if (cleaned.isEmpty()) continue;
                    
                    // Create a temporary Word object for searching
                    Word temp = new Word(cleaned);
                    
                    // If the word already exists, update its occurrence
                    if (tree.contains(temp)) {
                        BSTreeNode<Word> node = tree.search(temp);
                        node.getElement().addOccurrence(filename, lineNumber);
                    } else {
                        temp.addOccurrence(filename, lineNumber);
                        tree.add(temp);
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading input file: " + filename);
            e.printStackTrace();
        }
    }

    // -------------------- REPORT GENERATION --------------------
    /**
     * Generates a formatted report of all words stored in the tree.
     * <p>
     * Output is generated in alphabetical order using an in-order traversal.
     *
     * @param tree the binary search tree containing words
     * @param option the output format option (pf, pl, or po)
     * @param writer the output destination
     */
    private static void generateReport(BSTree<Word> tree, String option, PrintWriter writer) {
        writer.println("Displaying " + option + " format");
        
        // Traverse the BST in alphabetical order
        Iterator<Word> it = tree.inorderIterator();

        while (it.hasNext()) {
            Word word = it.next();

            if (option.equals("-pf")) {
            	// Print the word and each file it appears in
                for (WordLocation loc : word.getLocations()) {
                    writer.println("Key : ===" + word.getWord() + "=== found in file: " + loc.getFilename());
                }
                continue;
            }

            if (option.equals("-pl")) {
                StringBuilder sb = new StringBuilder();
                
                sb.append("Key : ===").append(word.getWord()).append("=== ");
                
                // Append file names and line numbers
                for (WordLocation loc : word.getLocations()) {
                    sb.append("found in file: ").append(loc.getFilename()).append(" on lines: ");
                    
                    // Append each line number
                    for (int i = 0; i < loc.getLines().size(); i++) {
                        sb.append(loc.getLines().get(i));
                        if (i < loc.getLines().size() - 1) sb.append(",");
                    }

                    sb.append(", ");
                }
                
                // Remove trailing comma and space
                if (sb.length() >= 2 && sb.substring(sb.length() - 2).equals(", ")) {
                    sb.setLength(sb.length() - 1);
                }

                writer.println(sb.toString());
                continue;
            }

            if (option.equals("-po")) {
            	// Get total frequency across all files
                int totalEntries = word.getTotalFrequency();

                StringBuilder sb = new StringBuilder();
                sb.append("Key : ===").append(word.getWord()).append("===  ");
                sb.append("number of entries: ").append(totalEntries).append(" ");
                
                // Append file names and line numbers
                for (WordLocation loc : word.getLocations()) {
                    sb.append("found in file: ").append(loc.getFilename()).append(" on lines: ");
                    
                    // Append each line number
                    for (int i = 0; i < loc.getLines().size(); i++) {
                        sb.append(loc.getLines().get(i));
                        if (i < loc.getLines().size() - 1) sb.append(",");
                    }

                    sb.append(", ");
                }
                
                // Remove trailing comma and space
                if (sb.length() >= 2 && sb.substring(sb.length() - 2).equals(", ")) {
                    sb.setLength(sb.length() - 1);
                }

                writer.println(sb.toString());
            }
        }
    }


    // -------------------- SERIALIZATION --------------------
    /**
     * Loads the word repository from disk.
     *
     * @return the deserialized {@code BSTree} or a new empty tree if loading fails
     */
    @SuppressWarnings("unchecked")
    private static BSTree<Word> loadRepository() {
        File file = new File(REPO_FILE);
        if (!file.exists()) return new BSTree<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (BSTree<Word>) in.readObject();
        } catch (Exception e) {
            return new BSTree<>();
        }
    }
    
    /**
     * Saves the repository to disk using serialization.
     *
     * @param tree the binary search tree to save
     */
    private static void saveRepository(BSTree<Word> tree) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(REPO_FILE))) {
            out.writeObject(tree);
        } catch (IOException e) {
            System.out.println("Error saving repository.");
            e.printStackTrace();
        }
    }
}
