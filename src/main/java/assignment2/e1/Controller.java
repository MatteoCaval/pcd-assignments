package assignment2.e1;

import assignment2.Document;
import assignment2.DocumentResult;
import assignment2.View;
import assignment2.e0.classic.TestExecutors;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controller implements View.SelectorListener {

    private View view;
    private TestExecutors test;
    private Vertx vertx = Vertx.vertx();
    private EventBus eventBus;
    private DocumentResult result = new DocumentResult();
    private Map<String, DocumentResult> singleResults = new HashMap<>();

    public Controller() {
        this.view = new View(this);
        test = new TestExecutors(view);
        this.eventBus = vertx.eventBus();
        vertx.deployVerticle(new FileVerticle(singleResults));

    }

    @Override
    public void startPressed(List<String> paths) {
       /* List<DocumentResult> results = paths.stream().map(p -> DocumentAnalyzer.analyzeDocument(Document.fromPath(p))).collect(Collectors.toList());
        List<Pair<String, Integer>> result = results.stream().reduce(DocumentResult::merge).get().toSortedPair();*/

//        List<Pair<String, Integer>> result = new TestExecutorCallables().compute(paths).toSortedPair();
//        this.view.printResult(result);

//        test.compute(paths);
        eventBus.consumer(BusAddresses.FILE_COMPUTED, message -> {
            this.view.printResult(singleResults.values().stream().reduce((doc, doc2) -> DocumentResult.merge(doc, doc2)).get().toSortedPair());
        });

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
