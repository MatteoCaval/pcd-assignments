package assignment2;

import javafx.util.Pair;

import java.util.List;
import java.util.stream.Collectors;

public class Controller implements View.SelectorListener {

    private View view;

    public Controller() {
        this.view = new View(this);
    }

    @Override
    public void startPressed(List<String> paths) {
        List<DocumentResult> results = paths.stream().map(p -> DocumentAnalyzer.analyzeDocument(Document.fromPath(p))).collect(Collectors.toList());
        List<Pair<String, Integer>> result = results.stream().reduce(DocumentResult::merge).get().toSortedPair();
        this.view.printResult(result);
    }

    @Override
    public void fileAdded(String... paths) {

    }

    @Override
    public void fileRemoved(String path) {

    }
}
