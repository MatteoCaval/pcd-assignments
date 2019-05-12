package assignment2.e1;

import assignment2.*;
import assignment2.fileanalysis.ComputationResults;
import assignment2.fileanalysis.DocumentAnalyzer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.eventbus.EventBus;

import java.util.UUID;

public class FileVerticle extends AbstractVerticle {

    private ComputationResults singleResults;
    private boolean ordered;
    WorkerExecutor executor;

    public FileVerticle(ComputationResults singleResults, boolean parallel) {
        this.singleResults = singleResults;
        this.ordered = !parallel;
    }

    @Override
    public void start() throws Exception {
        super.start();

        EventBus eventBus = this.vertx.eventBus();
        executor = vertx.createSharedWorkerExecutor(UUID.randomUUID().toString());

        eventBus.consumer(IOMessage.FILE_ADDED, message -> {
            String path = message.body().toString();
            Utils.log("File added: " + path);
            executor.executeBlocking(future -> {
                this.singleResults.addResult(path, DocumentAnalyzer.resultFromPath(path));
                future.complete();
            }, ordered, res -> {
                eventBus.publish(IOMessage.FILE_COMPUTED, path);
            });
        });

        eventBus.consumer(IOMessage.FILE_REMOVED, message -> {
            String path = message.body().toString();
            Utils.log("File removed: " + path);
            this.singleResults.removeResult(path);
            eventBus.publish(IOMessage.FILE_COMPUTED, path);
        });
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        executor.close();

    }
}
