package appDomain;

import implementations.BSTree;
import implementations.BSTreeNode;
import java.io.*;
import utilities.Iterator;

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
    private static void scanFile(String filename, BSTree<Word> tree) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                String[] words = line.split("[^a-zA-Z]+");

                for (String w : words) {
                    if (w.isEmpty()) continue;

                    Word temp = new Word(w);

                    if (tree.contains(temp)) {
                        BSTreeNode<Word> node = tree.search(temp);
                        Word existing = node.getElement();
                        existing.addOccurrence(filename, lineNumber);
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
    private static void generateReport(BSTree<Word> tree, String option, PrintWriter writer) {
        Iterator<Word> it = tree.inorderIterator();

        while (it.hasNext()) {
            Word word = it.next();
            writer.print(word.getWord() + " : ");

            for (WordLocation loc : word.getLocations()) {
                writer.print(loc.getFilename());
                if (!option.equals("-pf")) {
                    writer.print(" " + loc.getLines());
                }
                if (option.equals("-po")) {
                    writer.print(" (" + loc.getLines().size() + ")");
                }
                writer.print(" | ");
            }
            writer.println();
        }
    }

    // -------------------- SERIALIZATION --------------------
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

    private static void saveRepository(BSTree<Word> tree) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(REPO_FILE))) {
            out.writeObject(tree);
        } catch (IOException e) {
            System.out.println("Error saving repository.");
            e.printStackTrace();
        }
    }
}
