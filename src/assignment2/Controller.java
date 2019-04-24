package assignment2;

import assignment2.e0.TestExecutorCallables;
import javafx.util.Pair;

import java.util.List;

public class Controller implements View.SelectorListener {

    private View view;

    public Controller() {
        this.view = new View(this);
    }

    @Override
    public void startPressed(List<String> paths) {
       /* List<DocumentResult> results = paths.stream().map(p -> DocumentAnalyzer.analyzeDocument(Document.fromPath(p))).collect(Collectors.toList());
        List<Pair<String, Integer>> result = results.stream().reduce(DocumentResult::merge).get().toSortedPair();*/

        List<Pair<String, Integer>> result = new TestExecutorCallables().compute(paths).toSortedPair();
        this.view.printResult(result);
    }

    @Override
    public void fileAdded(String... paths) {

    }

    @Override
    public void fileRemoved(String path) {

    }
}
