package assignment2.e1;

import assignment2.BaseController;
import assignment2.fileanalysis.ComputationResults;
import assignment2.IOMessage;
import assignment2.view.MainView;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;

public class VertxController extends BaseController {

    private Vertx vertx = Vertx.vertx(/*new VertxOptions().setWorkerPoolSize(8)*/);
    private EventBus eventBus;
    private ComputationResults singleResults;
    private boolean parallel;

    public VertxController(MainView view, boolean parallel) {
        super(view);
        this.singleResults = new ComputationResults();
        this.eventBus = vertx.eventBus();
        this.parallel = parallel;
        vertx.deployVerticle(new FileVerticle(singleResults, parallel));
        eventBus.consumer(IOMessage.FILE_COMPUTED, message -> {
            this.view.printResult(singleResults.getGlobalOrderedResult());
            if (this.singleResults.checkComputationEnded(this.view.getInputSize())) {
                this.view.notifyComputationCompleted();
                this.view.setComputationTime(this.crono.stop().getTime());
            }
        });
    }

    @Override
    public void startPressed(List<String> paths) {
        super.startPressed(paths);
        this.singleResults.clear();
        this.filesAdded(paths.toArray(new String[paths.size()]));
    }

    @Override
    public void filesAdded(String... filePaths) {
        Arrays.stream(filePaths).forEach(p ->
                eventBus.publish(IOMessage.FILE_ADDED, p)
        );
    }

    @Override
    public void fileRemoved(String path) {
        eventBus.publish(IOMessage.FILE_REMOVED, path);
    }

    @Override
    public void stopPressed() {
        super.stopPressed();
        this.vertx.deploymentIDs().forEach(vertx::undeploy);
        vertx.deployVerticle(new FileVerticle(singleResults, parallel));
    }
}
