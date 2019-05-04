package assignment2.fileanalysis;

import javafx.util.Pair;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ComputationResults {

    private ConcurrentHashMap<String, DocumentResult> singleResults = new ConcurrentHashMap();

    public void addResult(String path, DocumentResult result) {
        this.addOrRemove(path, result);
    }

    public void removeResult(String path) {
        this.addOrRemove(path, new DocumentResult());
    }

    public List<Pair<String, Integer>> getGlobalOrderedResult() {
        return singleResults.values().stream().reduce((doc, doc2) -> DocumentResult.merge(doc, doc2)).get().toSortedPair();
    }

    public void clear() {
        this.singleResults.clear();
    }

    public boolean checkComputationEnded(int inputSize) {
        return inputSize == singleResults.size();
    }

    private synchronized void addOrRemove(String path, DocumentResult result) {
        if (singleResults.keySet().contains(path)) {
            singleResults.remove(path);

        } else {
            singleResults.put(path, result);
        }
    }

}
