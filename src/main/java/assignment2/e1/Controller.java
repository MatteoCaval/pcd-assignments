package assignment2.e1;

import assignment2.DocumentResult;
import assignment2.Utils;
import assignment2.View;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Controller implements View.SelectorListener {

    private View view;
    private Vertx vertx = Vertx.vertx();
    private EventBus eventBus;
    private ConcurrentHashMap<String, DocumentResult> singleResults = new ConcurrentHashMap<>();

    public Controller() {
        this.view = new View(this);
        this.eventBus = vertx.eventBus();
        vertx.deployVerticle(new FileVerticle(singleResults));
        eventBus.consumer(BusAddresses.FILE_COMPUTED, message -> {
            if (this.singleResults != null && !this.singleResults.isEmpty()){
                this.view.printResult(singleResults.values().stream().reduce((doc, doc2) -> DocumentResult.merge(doc, doc2)).get().toSortedPair());
            } else {
                this.view.printResult(null);
            }
            Utils.log("Aggiorno view");
        });

    }

    @Override
    public void startPressed(List<String> paths) {
        this.view.printResult(null);
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
        eventBus.publish(BusAddresses.FILE_REMOVED, path);
    }

    @Override
    public void stopPressed() {
        this.singleResults.clear();
    }
}
