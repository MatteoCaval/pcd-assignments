package assignment2.fileanalysis;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentResult {

    private Map<String, Integer> result;

    public DocumentResult() {
        result = new HashMap<>();
    }

    public void insert(String word) {
        insert(word, 1);
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

    /**
     * @return ordered structure of word - num occurrences
     */
    public List<Pair<String, Integer>> toSortedPair() {
        List<Pair<String, Integer>> list = new ArrayList<>();
        result.forEach((k, v) -> list.add(new Pair<>(k, v)));
        list.sort((o1, o2) -> o2.getValue() - o1.getValue());
        return list;
    }

    public void merge(DocumentResult d2) {
        d2.getResult().forEach((k, v) -> this.insert(k, v));
    }

    public void clear() {
        this.result.clear();
    }

    // region Private methods

    /**
     * @param word word found inside the file
     * @param n    word occurrences inside the file
     */
    private void insert(String word, int n) {
        if (result.containsKey(word)) {
            result.put(word, result.get(word) + n);
        } else {
            result.put(word, n);
        }
    }

    // endregion

    // region Static methods

    public static DocumentResult merge(DocumentResult d1, DocumentResult d2) {
        DocumentResult document = new DocumentResult();
        d2.getResult().forEach(((k, v) -> document.insert(k, v)));
        d1.getResult().forEach((k, v) -> document.insert(k, v));
        return document;
    }

    // endregion

}
