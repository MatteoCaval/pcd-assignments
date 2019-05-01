package assignment2;

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
    private Vertx vertx = Vertx.vertx();
    private EventBus eventBus;

    public Controller() {
        this.view = new View(this);
        test = new TestExecutors(view);
        this.eventBus = vertx.eventBus();
        vertx.deployVerticle(new FileVerticle());
    }

    @Override
    public void startPressed(List<String> paths) {
       /* List<DocumentResult> results = paths.stream().map(p -> DocumentAnalyzer.analyzeDocument(Document.fromPath(p))).collect(Collectors.toList());
        List<Pair<String, Integer>> result = results.stream().reduce(DocumentResult::merge).get().toSortedPair();*/

//        List<Pair<String, Integer>> result = new TestExecutorCallables().compute(paths).toSortedPair();
//        this.view.printResult(result);

//        test.compute(paths);


    }

    @Override
    public void fileAdded(String filePath) {
        eventBus.publish(BusAddresses.FILE_ADDED, filePath);
    }

    @Override
    public void fileRemoved(String path) {

    }

    @Override
    public void stopPressed() {
        this.test.stop();
    }
}
