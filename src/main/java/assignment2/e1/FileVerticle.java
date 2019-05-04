package assignment2.e1;

import assignment2.*;
import assignment2.fileanalysis.ComputationResults;
import assignment2.fileanalysis.Document;
import assignment2.fileanalysis.DocumentAnalyzer;
import assignment2.view.MainView;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

import java.util.Arrays;

public class FileVerticle extends AbstractVerticle {

    private ComputationResults singleResults;
    private boolean ordered;

    public FileVerticle(ComputationResults singleResults, boolean parallel) {
        this.singleResults = singleResults;
        this.ordered = !parallel;
    }

    @Override
    public void start() throws Exception {
        super.start();

        EventBus eventBus = this.vertx.eventBus();

        eventBus.consumer(IOMessage.FILE_ADDED, message -> {
            String path = message.body().toString();

            Utils.log("File added: " + path);
            vertx.fileSystem().readFile(path, buffer -> {
                vertx.executeBlocking(future -> {
                    Utils.log("Obtained " + path + " result ");
                    this.singleResults.addResult(path, DocumentAnalyzer.analyzeDocument(new Document(Arrays.asList(buffer.result().toString()))));
                    future.complete();

                }, ordered, res -> {
                    eventBus.publish(IOMessage.FILE_COMPUTED, path);
                    Utils.log("Finished " + path + " result ");
                });
            });
        });

        eventBus.consumer(IOMessage.FILE_REMOVED, message -> {
            String path = message.body().toString();
            Utils.log("File removed: " + path);
            this.singleResults.removeResult(path);
            eventBus.publish(IOMessage.FILE_COMPUTED, path);
        });

       /* eventBus.consumer(BusAddresses.STOP, message -> {
            Utils.log("Undeploy verticle");
            this.vertx.undeploy(this.deploymentID());
        });*/

    }


}
