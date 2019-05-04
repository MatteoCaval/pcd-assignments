package assignment2.fileanalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

public class Document {

    private final List<String> lines;

    public Document(List<String> lines) {
        this.lines = lines;
    }

    public List<String> getLines() {
        return this.lines;
    }

    public static Document fromPath(String path) {
        List<String> lines = new LinkedList<String>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(new File(path)));
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new Document(lines);
    }

    @Override
    public String toString() {
        String text = "";
        for (String line : lines) {
            text += line + "\n";
        }
        return text;
    }
}
