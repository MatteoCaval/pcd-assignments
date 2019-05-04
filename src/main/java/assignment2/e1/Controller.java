package assignment2.e1;

import assignment2.BaseController;
import assignment2.ComputationResults;
import assignment2.MainView;
import assignment2.Utils;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;

public class Controller extends BaseController{

    private Vertx vertx = Vertx.vertx();
    private EventBus eventBus;
    private ComputationResults singleResults;

    public Controller(MainView view) {
        super(view);
        this.singleResults = new ComputationResults();
        this.eventBus = vertx.eventBus();
    }

    @Override
    public void startPressed(List<String> paths) {
        super.startPressed(paths);

        vertx.deployVerticle(new FileVerticle(singleResults));
        eventBus.consumer(BusAddresses.FILE_COMPUTED, message -> {
            this.view.printResult(singleResults.getGlobalOrderedResult());
            if (this.singleResults.checkComputationEnded(this.view.getInputSize())) {
                this.view.notifyComputationCompleted();
                this.view.setComputationTime(this.crono.stop().getTime());
            }
            Utils.log("Aggiorno view");
        });


        this.filesAdded(paths.toArray(new String[paths.size()]));

    }

    @Override
    public void filesAdded(String... filePaths) {
        Arrays.stream(filePaths).forEach(p ->
                eventBus.publish(BusAddresses.FILE_ADDED, p)
        );
    }

    @Override
    public void fileRemoved(String path) {
        eventBus.publish(BusAddresses.FILE_REMOVED, path);
    }

    @Override
    public void stopPressed() {
        super.stopPressed();
        eventBus.publish(BusAddresses.STOP, null);
    }
}
