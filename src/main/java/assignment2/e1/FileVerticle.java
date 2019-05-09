package assignment2.e1;

import assignment2.*;
import assignment2.fileanalysis.ComputationResults;
import assignment2.fileanalysis.DocumentAnalyzer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

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
//            vertx.fileSystem().readFile(path, buffer -> {
//                vertx.executeBlocking(future -> {
//                    Utils.log("Obtained " + path + " result ");
//                    this.singleResults.addResult(path, DocumentAnalyzer.analyzeDocument(new Document(Arrays.asList(buffer.result().toString()))));
//                    future.complete();
//
//                }, ordered, res -> {
//                    eventBus.publish(IOMessage.FILE_COMPUTED, path);
//                    Utils.log("Finished " + path + " result ");
//                });
//            });

//            vertx.fileSystem().open(path, new OpenOptions(), buffer -> {
//
//                DocumentResult result = new DocumentResult();
//
//                buffer.result().handler(fileBuffer -> {
//
//
//                    //senza il blocking ordine corretto ma risultato finale leggermente scorretto rispetto agli altri
//                    //rimane sempre più lento nei vari casi
//                    vertx.executeBlocking(future -> {
////
//                        for (String word : DocumentAnalyzer.wordInLine(fileBuffer.toString())) {
//                            if (word.length() >= DocumentAnalyzer.MIN_LENGHT) {
//                                result.insert(word);
//                            }
//                        }
//
////                        System.out.println("Size of " + path + ": " + result.getResult().size());
////                        this.view.printResult(singleResults.getGlobalOrderedResult());
//                        future.complete();
//
//                    }, ordered, res -> {
//
//                    });
//
//
//                });
//
//
//                buffer.result().endHandler(handler -> {
//                    this.singleResults.addResult(path, result);
//                    this.view.printResult(singleResults.getGlobalOrderedResult());
//                    eventBus.publish(IOMessage.FILE_COMPUTED, path);
//                    Utils.log("Finished " + path + " result with size " + result.getResult().size());
//                });
//
//
//            });

            vertx.executeBlocking(future -> {
                this.singleResults.addResult(path, DocumentAnalyzer.resultFromPath(path));
                future.complete(singleResults.getGlobalOrderedResult());
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

       /* eventBus.consumer(BusAddresses.STOP, message -> {
            Utils.log("Undeploy verticle");
            this.vertx.undeploy(this.deploymentID());
        });*/

    }


}
