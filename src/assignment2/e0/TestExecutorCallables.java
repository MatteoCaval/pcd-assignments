package assignment2.e0;

import assignment2.DocumentResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestExecutorCallables {

    private ExecutorService executor;

    public TestExecutorCallables() {
        executor = Executors.newFixedThreadPool(8);
    }

    public DocumentResult compute(List<String> paths) {
        Set<Future<DocumentResult>> futures = new HashSet<>();

        for (String path : paths) {
            futures.add(executor.submit(new FileComputeTask(path)));
        }

        DocumentResult result = new DocumentResult();
        for (Future<DocumentResult> f : futures) {
            try {
                result = DocumentResult.merge(result, f.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
        return result;


    }

}
