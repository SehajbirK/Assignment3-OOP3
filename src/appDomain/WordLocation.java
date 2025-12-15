package appDomain;
import java.io.Serializable;
import java.util.ArrayList;

public class WordLocation implements Serializable {

    private static final long serialVersionUID = 1L;

    private String filename;
    private ArrayList<Integer> lines;

    public WordLocation(String filename) {
        this.filename = filename;
        this.lines = new ArrayList<>();
    }

    public String getFilename() {
        return filename;
    }

    public ArrayList<Integer> getLines() {
        return lines;
    }

    public void addLine(int line) {
        lines.add(line);
    }
}
