package assignment2.e0.classic;

import assignment2.DocumentResult;
import assignment2.MainView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestExecutors implements FileComputeTask.Updater {


    private ExecutorService executor;
    private Map<String, DocumentResult> singleResults = new HashMap<>();
    private MainView view;

    public TestExecutors(MainView view) {
        this.executor = Executors.newFixedThreadPool(8);
        this.view = view;
    }

    public void compute(String... paths) {
        Arrays.stream(paths).forEach(
                p -> {
                    if (!singleResults.keySet().contains(p)) {
                        executor.submit(new FileComputeTask(p, this));
                    }
                }
        );
    }

    public synchronized void remove(String path) {
        if (singleResults.keySet().contains(path)) {
            singleResults.remove(path);
        } else {
            singleResults.put(path, new DocumentResult());
        }
        this.mergeAndUpdateResult();
    }

    @Override
    synchronized public void submitResult(String path, DocumentResult result) {
        if (this.singleResults.keySet().contains(path)) {
            // significa che la remove l'ha inserito vuoto, e quindi lo devo scartare, oltre che togliere quello vuoto dalla mappa
            this.singleResults.remove(path);
        } else {
            // viene aggiunto ai risultati
            this.singleResults.put(path, result);
        }
        this.mergeAndUpdateResult();
    }

    public void stop() {
        this.executor.shutdownNow();
        this.singleResults.clear();
        this.executor = Executors.newFixedThreadPool(8);
    }

    private void mergeAndUpdateResult() {
        if (!singleResults.isEmpty()) {
            this.view.printResult(singleResults.values().stream().reduce((doc, doc2) -> DocumentResult.merge(doc, doc2)).get().toSortedPair());
        } else {
            this.view.printResult(null);
        }
    }
}
