package assignment2;

public class DocumentAnalyzer {

    private static final int MIN_LENGHT = 4;

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

    private static String[] wordInLine(String line) {
        return line.trim().split("\\s+");
    }

}
