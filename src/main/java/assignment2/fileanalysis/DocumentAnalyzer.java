package assignment2.fileanalysis;

import assignment2.Config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class DocumentAnalyzer {

    public static DocumentResult resultFromPath(String path) {
        DocumentResult result = new DocumentResult();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(new File(path)));
            String line = reader.readLine();
            while (line != null) {

                for (String word : wordInLine(line)) {
                    if (word.length() >= Config.MIN_LENGHT) {
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

    private static String[] wordInLine(String line) {
        return line.trim().split("\\s+");
    }

}
