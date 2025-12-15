package appDomain;
import java.io.Serializable;
import java.util.ArrayList;

public class Word implements Comparable<Word>, Serializable {

    private static final long serialVersionUID = 1L;

    private String word;
    private ArrayList<WordLocation> locations;

    public Word(String word) {
        this.word = word.toLowerCase();
        this.locations = new ArrayList<>();
    }

    public String getWord() {
        return word;
    }

    public ArrayList<WordLocation> getLocations() {
        return locations;
    }

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

    public int getTotalFrequency() {
        int count = 0;
        for (WordLocation loc : locations) {
            count += loc.getLines().size();
        }
        return count;
    }

    @Override
    public int compareTo(Word other) {
        return this.word.compareTo(other.word);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Word)) return false;
        Word other = (Word) obj;
        return this.word.equals(other.word);
    }

    @Override
    public int hashCode() {
        return word.hashCode();
    }
}
