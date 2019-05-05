package assignment2.fileanalysis;

import assignment2.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class DocumentAnalyzer {

    public static final int MIN_LENGHT = 4;

    public static DocumentResult analyzeDocument(Document document) {
        Utils.log("Analyzing document");
        DocumentResult result = new DocumentResult();
        for (String line : document.getLines()) {
            for (String word : wordInLine(line)) {
                if (word.length() >= MIN_LENGHT) {
                    result.insert(word);
                }
            }
        }
        Utils.log("Stop analyzing document");
        return result;
    }

    public static DocumentResult resultFromPath(String path) {
        DocumentResult result = new DocumentResult();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(new File(path)));
            String line = reader.readLine();
            while (line != null) {

                for (String word : wordInLine(line)) {
                    if (word.length() >= MIN_LENGHT) {
                        result.insert(word);
                    }
                }

                line = reader.readLine();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static String[] wordInLine(String line) {
        return line.trim().split("\\s+");
    }

}
