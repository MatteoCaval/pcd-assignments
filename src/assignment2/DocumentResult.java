package assignment2;

import java.util.HashMap;
import java.util.Map;

public class DocumentResult {

    private Map<String, Integer> result;

    public DocumentResult() {
        result = new HashMap<>();
    }

    public void insert(String word) {
        if (result.containsKey(word)) {
            result.put(word, result.get(word) + 1);
        } else {
            result.put(word, 1);
        }
    }

    public Map<String, Integer> getResult() {
        return result;
    }

    @Override
    public String toString() {
        String text = "";
        for (String k : result.keySet()) {
            text += k + " -> " + result.get(k) + "\n";
        }
        return "Result:\n" + text;
    }
}
