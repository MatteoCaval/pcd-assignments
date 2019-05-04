package assignment2.e1;

import assignment2.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

import java.util.Arrays;
import java.util.Map;

public class FileVerticle extends AbstractVerticle {

    private ComputationResults singleResults;

    public FileVerticle(ComputationResults singleResults) {
        this.singleResults = singleResults;
    }

    @Override
    public void start() throws Exception {
        super.start();

        EventBus eventBus = this.vertx.eventBus();

        eventBus.consumer(BusAddresses.FILE_ADDED, message -> {
            String path = message.body().toString();

            Utils.log("File added: " + path);
            vertx.fileSystem().readFile(path, buffer -> {
                vertx.executeBlocking(future -> {
                    Utils.log("Obtained " + path + " result ");
                    this.singleResults.addResult(path, DocumentAnalyzer.analyzeDocument(new Document(Arrays.asList(buffer.result().toString()))));
                    future.complete();

                }, false, res -> {
                    eventBus.publish(BusAddresses.FILE_COMPUTED, path);
                    Utils.log("Finished " + path + " result ");
                });
            });
        });

        eventBus.consumer(BusAddresses.FILE_REMOVED, message -> {
            String path = message.body().toString();
            Utils.log("File removed: " + path);
            this.singleResults.removeResult(path);
            eventBus.publish(BusAddresses.FILE_COMPUTED, path);
        });

       /* eventBus.consumer(BusAddresses.STOP, message -> {
            Utils.log("Undeploy verticle");
            this.vertx.undeploy(this.deploymentID());
        });*/

    }


}
