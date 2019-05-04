package assignment2.e0;

import assignment2.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorController extends BaseController implements FileComputeTask.Updater {

    private ExecutorService executor;
    private ComputationResults singleResults = new ComputationResults();

    public ExecutorController(MainView view, boolean parallel) {
        super(view);
    }

    // region MainView

    @Override
    public void startPressed(List<String> paths) {
        this.initExecutor();
        this.singleResults.clear();
        super.startPressed(paths);
        this.filesAdded(paths.toArray(new String[paths.size()]));
    }

    @Override
    public void filesAdded(String... paths) {
        Arrays.stream(paths).forEach(
                p -> {
                    executor.submit(new FileComputeTask(p, this));
                }
        );
    }

    @Override
    public void fileRemoved(String path) {
        Utils.log("Remove " + path);
        singleResults.removeResult(path);
        this.update();
    }

    @Override
    public void stopPressed() {
        super.stopPressed();
        this.executor.shutdownNow();
    }

    // endregion

    // region FileComputeTask.Updater

    @Override
    public void submitResult(String path, DocumentResult result) {
        singleResults.addResult(path, result);
        this.update();
    }

    // endregion

    // region Private methods

    private void initExecutor() {
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    private void update() {
        this.view.printResult(this.singleResults.getGlobalOrderedResult());
        if (singleResults.checkComputationEnded(view.getInputSize())) {
            this.stopPressed();
            this.view.notifyComputationCompleted();
        }
    }

    // endregion


}
