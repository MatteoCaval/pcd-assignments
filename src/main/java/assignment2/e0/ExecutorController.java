package assignment2.e0;

import assignment2.BaseController;
import assignment2.SelectorListener;
import assignment2.ViewImpl;
import assignment2.MainView;

import java.util.List;

public class ExecutorController extends BaseController {

    private TestExecutors test;


    public ExecutorController(MainView view) {
        super(view);
        test = new TestExecutors(view);
    }

    @Override
    public void startPressed(List<String> paths) {
        super.startPressed(paths);
       /* List<DocumentResult> results = paths.stream().map(p -> DocumentAnalyzer.analyzeDocument(Document.fromPath(p))).collect(Collectors.toList());
        List<Pair<String, Integer>> result = results.stream().reduce(DocumentResult::merge).get().toSortedPair();*/

//        List<Pair<String, Integer>> result = new TestExecutorCallables().compute(paths).toSortedPair();
//        this.view.printResult(result);
        this.view.printResult(null);
        this.filesAdded(paths.toArray(new String[paths.size()]));
    }

    @Override
    public void filesAdded(String... filePath) {
        test.compute(filePath);
    }

    @Override
    public void fileRemoved(String path) {
        test.remove(path);
    }

    @Override
    public void stopPressed() {
        super.stopPressed();
        this.test.stop();
    }
}
