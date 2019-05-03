package assignment2.e2;

import assignment2.*;
import assignment2.e1.BusAddresses;
import javafx.util.Pair;
import rx.Observer;
import rx.schedulers.Schedulers;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class RxController implements View.SelectorListener {

    private View view;
    private ConcurrentHashMap<String, DocumentResult> singleResults = new ConcurrentHashMap();
    private RxBus bus;

    public RxController() {
        bus = RxBus.getInstace();
        this.view = new View(this);
    }

    @Override
    public void startPressed(List<String> paths) {
        bus.putEvent(new Pair(BusAddresses.START, null));

        bus.getEvents()
                .observeOn(Schedulers.io())
                .filter(e -> e.getKey().equals(BusAddresses.FILE_ADDED))
                .map(e -> e.getValue())
                .map(e -> new Pair<>(e, DocumentAnalyzer.analyzeDocument(Document.fromPath(e))))
                .subscribe(new Observer<Pair<String, DocumentResult>>() {
                    @Override
                    public void onCompleted() {
                        Utils.log("COMPLETED");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Pair<String, DocumentResult> p) {
                        Utils.log("Computed element " + p.getKey());
                        singleResults.put(p.getKey(), p.getValue());
                        view.printResult(singleResults.values().stream().reduce((doc, doc2) -> DocumentResult.merge(doc, doc2)).get().toSortedPair());
                    }
                });


        filesAdded(paths.toArray(new String[paths.size()]));
    }

    @Override
    public void filesAdded(String... filePaths) {
        Arrays.stream(filePaths).forEach(p -> {
            bus.putEvent(new Pair(BusAddresses.FILE_ADDED, p));
        });

    }

    @Override
    public void fileRemoved(String path) {
        bus.putEvent(new Pair(BusAddresses.FILE_REMOVED, path));
    }

    @Override
    public void stopPressed() {
        bus.putEvent(new Pair(BusAddresses.STOP, null));
    }
}
