package assignment2.e1;

import assignment2.Document;
import assignment2.DocumentAnalyzer;
import assignment2.DocumentResult;
import assignment2.Utils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;

import java.util.Arrays;
import java.util.Map;

public class FileVerticle extends AbstractVerticle {

    private Map<String, DocumentResult> singleResults;

    public FileVerticle(Map<String, DocumentResult> singleResults) {
        this.singleResults = singleResults;
    }

    @Override
    public void start() throws Exception {
        super.start();

        EventBus eventBus = this.vertx.eventBus();

        eventBus.consumer(BusAddresses.FILE_ADDED, message -> {
            String path = message.body().toString();

            Utils.log("File added: " + path);

            Future<Void> failFuture = Future.future();
            Future<Buffer> future = Future.future();

            vertx.fileSystem().readFile(path, future);
            future.compose(result -> {
                Utils.log("Obtained " + path + " result ");
                singleResults.put(path, DocumentAnalyzer.analyzeDocument(new Document(Arrays.asList(result.toString()))));
                eventBus.publish(BusAddresses.FILE_COMPUTED, path);

            }, failFuture);

        });
    }

}
