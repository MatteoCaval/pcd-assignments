package assignment2.e0.classic;

import assignment2.DocumentResult;
import assignment2.View;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestExecutors implements FileComputeTask.Updater {


    private ExecutorService executor;
    private DocumentResult globalResult = new DocumentResult();
    private Map<String, DocumentResult> results = new HashMap<>();
    private View view;

    public TestExecutors(View view) {
        this.executor = Executors.newFixedThreadPool(8);
        this.view = view;
    }

    public void compute(List<String> paths) {
        paths.stream().forEach(
                p -> {
                    if (!results.keySet().contains(p)) {
                        results.put(p, null);
                        executor.submit(new FileComputeTask(p, this));
                    }
                }
        );
    }

    @Override
    synchronized public void submitResult(String path, DocumentResult result) {
        this.results.put(path, result);
        globalResult.merge(result);
        view.printResult(this.globalResult.toSortedPair());
    }

    public void stop() {
        this.executor.shutdownNow();
        this.globalResult.clear();
        this.results.clear();
        this.executor = Executors.newFixedThreadPool(8);
    }
}
