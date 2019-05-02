package assignment2.e0;

import assignment2.View;
import assignment2.e0.callable.TestExecutorCallables;
import assignment2.e0.classic.TestExecutors;
import assignment2.e1.BusAddresses;
import assignment2.e1.FileVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
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
        this.view.printResult(null);
        this.filesAdded(paths.toArray(new String[paths.size()]));
    }

    @Override
    public void filesAdded(String... filePath) {
        test.compute(filePath);
    }

    @Override
    public void fileRemoved(String path) {
        test.remove(path);
    }

    @Override
    public void stopPressed() {
        this.test.stop();
    }
}
