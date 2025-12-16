package appDomain;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a unique word found in one or more text files.
 * <p>
 * A {@code Word} object tracks:
 * <ul>
 *   <li>The original word as it appears in the text</li>
 *   <li>A normalized lowercase key used for comparison and equality</li>
 *   <li>The locations (files and line numbers) where the word occurs</li>
 * </ul>
 *
 * Words are considered equal if their lowercase keys match.
 * This class is comparable, allowing words to be sorted
 * alphabetically in a case-insensitive manner. This class is 
 * also serializable to support persistent storage.
 */
public class Word implements Comparable<Word>, Serializable {

	private static final long serialVersionUID = -8242754647364841405L;

	// The original word as it appears in the text
    private String word;
    
    // Lowercase key used for comparison and equality
    private String wordKey;
    
    //List of locations where the word occurs
    private ArrayList<WordLocation> locations;

    public Word(String word) {
        this.word = word;
        this.wordKey = word.toLowerCase();
        this.locations = new ArrayList<>();
    }

    public String getWord() {
        return word;
    }

    public ArrayList<WordLocation> getLocations() {
        return locations;
    }
    
    /**
     * Records an occurrence of this word in a file at a specific line number.
     * <p>
     * If the file already exists in the location list, the line number
     * is added to that location. Otherwise, a new location is created.
     *
     * @param filename the name of the file
     * @param lineNumber the line number where the word appears
     */
    public void addOccurrence(String filename, int lineNumber) {
        for (WordLocation loc : locations) {
            if (loc.getFilename().equals(filename)) {
                loc.addLine(lineNumber);
                return;
            }
        }
        WordLocation newLoc = new WordLocation(filename);
        newLoc.addLine(lineNumber);
        locations.add(newLoc);
    }
    
    /**
     * Returns the total number of times this word appears
     * across all files.
     *
     * @return the total frequency of the word
     */
    public int getTotalFrequency() {
        int count = 0;
        for (WordLocation loc : locations) {
            count += loc.getFrequency();
        }
        return count;
    }

    @Override
    public int compareTo(Word other) {
        return this.wordKey.compareTo(other.wordKey);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Word)) return false;
        Word other = (Word) obj;
        return this.wordKey.equals(other.wordKey);
    }

    @Override
    public int hashCode() {
        return wordKey.hashCode();
    }
}
