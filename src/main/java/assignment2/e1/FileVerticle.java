package assignment2.e1;

import assignment2.Document;
import assignment2.DocumentAnalyzer;
import assignment2.DocumentResult;
import assignment2.Utils;
import io.vertx.core.AbstractVerticle;
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

            vertx.fileSystem().readFile(path, buffer -> {
                vertx.executeBlocking(future -> {
                    Utils.log("Obtained " + path + " result ");
                    if (this.singleResults.keySet().contains(path)){
                        // significa che la remove l'ha inserito vuoto, e quindi lo devo scartare, oltre che togliere quello vuoto dalla mappa
                        this.singleResults.remove(path);
                    } else {
                        // viene aggiunto ai risultati
                        singleResults.put(path, DocumentAnalyzer.analyzeDocument(new Document(Arrays.asList(buffer.toString()))));
                    }
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

            if(singleResults.keySet().contains(path)){
                singleResults.remove(path);
            } else {
                singleResults.put(path, new DocumentResult());
            }

            eventBus.publish(BusAddresses.FILE_COMPUTED, path);
        });
    }
}
