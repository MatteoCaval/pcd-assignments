package assignment2.e1;

import assignment2.Document;
import assignment2.DocumentResult;
import assignment2.Utils;
import assignment2.View;
import assignment2.e0.classic.TestExecutors;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Controller implements View.SelectorListener {

    private View view;
    private TestExecutors test;
    private Vertx vertx = Vertx.vertx();
    private EventBus eventBus;
    private DocumentResult result = new DocumentResult();
    private ConcurrentHashMap<String, DocumentResult> singleResults = new ConcurrentHashMap<>();

    public Controller() {
        this.view = new View(this);
        test = new TestExecutors(view);
        this.eventBus = vertx.eventBus();
        vertx.deployVerticle(new FileVerticle(singleResults));
        eventBus.consumer(BusAddresses.FILE_COMPUTED, message -> {
            this.view.printResult(singleResults.values().stream().reduce((doc, doc2) -> DocumentResult.merge(doc, doc2)).get().toSortedPair());
            Utils.log("Aggiorno view");
        });

    }

    @Override
    public void startPressed(List<String> paths) {
       /* List<DocumentResult> results = paths.stream().map(p -> DocumentAnalyzer.analyzeDocument(Document.fromPath(p))).collect(Collectors.toList());
        List<Pair<String, Integer>> result = results.stream().reduce(DocumentResult::merge).get().toSortedPair();*/

//        List<Pair<String, Integer>> result = new TestExecutorCallables().compute(paths).toSortedPair();
//        this.view.printResult(result);

//        test.compute(paths);


        this.filesAdded(paths.toArray(new String[paths.size()]));

    }

    @Override
    public void filesAdded(String... filePaths) {
        Arrays.stream(filePaths).forEach(p ->
                eventBus.publish(BusAddresses.FILE_ADDED, p)
        );

    }

    @Override
    public void fileRemoved(String path) {

    }

    @Override
    public void stopPressed() {
        this.test.stop();
    }
}
