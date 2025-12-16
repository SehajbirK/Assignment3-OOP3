package appDomain;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents the occurrence details of a word within a single file.
 * <p>
 * A {@code WordLocation} tracks:
 * <ul>
 *   <li>The name of the file in which the word appears</li>
 *   <li>The line numbers where the word occurs</li>
 *   <li>The total frequency of the word in that file</li>
 * </ul>
 *
 * This class is serializable.
 */
public class WordLocation implements Serializable {

	private static final long serialVersionUID = -2432575218627058167L;
	
	private String filename;
    private ArrayList<Integer> lines;
    private int frequency;

    public WordLocation(String filename) {
        this.filename = filename;
        this.lines = new ArrayList<>();
        this.frequency = 0;
    }

    public String getFilename() {
        return filename;
    }

    public ArrayList<Integer> getLines() {
        return lines;
    }

    public int getFrequency() { 
    	return frequency; 
    }
    

    /**
     * Records a new occurrence of the word at the specified line number.
     * <p>
     * The frequency count is incremented and the line number is added
     * to the list of recorded lines.
     *
     * @param line the line number where the word occurs
     */
    public void addLine(int line) {
    	 frequency++;
    	 lines.add(line);
    }
}
