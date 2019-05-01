package assignment2;

import assignment2.e0.callable.TestExecutorCallables;
import assignment2.e0.classic.TestExecutors;
import javafx.util.Pair;

import java.util.List;

public class Controller implements View.SelectorListener {

    private View view;
    private TestExecutors test;

    public Controller() {
        this.view = new View(this);
        test = new TestExecutors(view);
    }

    @Override
    public void startPressed(List<String> paths) {
       /* List<DocumentResult> results = paths.stream().map(p -> DocumentAnalyzer.analyzeDocument(Document.fromPath(p))).collect(Collectors.toList());
        List<Pair<String, Integer>> result = results.stream().reduce(DocumentResult::merge).get().toSortedPair();*/

//        List<Pair<String, Integer>> result = new TestExecutorCallables().compute(paths).toSortedPair();
//        this.view.printResult(result);

        test.compute(paths);

    }

    @Override
    public void fileAdded(String... paths) {

    }

    @Override
    public void fileRemoved(String path) {

    }

    @Override
    public void stopPressed() {
        this.test.stop();
    }
}
