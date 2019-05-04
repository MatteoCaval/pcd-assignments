package assignment2.e2;

import assignment2.*;
import assignment2.e1.BusAddresses;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RxController implements ViewImpl.SelectorListener {

    private MainView view;
    private Map<String, DocumentResult> singleResults = new ConcurrentHashMap();
    private RxBus bus;
    private CompositeDisposable compositeDisposable;

    public RxController() {
        bus = RxBus.getInstace();
        this.view = new ViewImpl(this);
        this.compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void startPressed(List<String> paths) {
        this.singleResults.clear();

//        bus.getEvents()
//                .observeOn(Schedulers.computation())
//                .filter(e -> e.getKey().equals(BusAddresses.FILE_ADDED))
//                .map(e -> e.getValue())
//                .map(e -> new Pair<>(e, DocumentAnalyzer.analyzeDocument(Document.fromPath(e))))
//                .subscribe(new Observer<Pair<String, DocumentResult>>() {
//                    @Override
//                    public void onCompleted() {
//                        Utils.log("COMPLETED");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onNext(Pair<String, DocumentResult> p) {
//                        Utils.log("Computed element " + p.getKey());
//                        singleResults.put(p.getKey(), p.getValue());
//                        view.printResult(singleResults.values().stream().reduce((doc, doc2) -> DocumentResult.merge(doc, doc2)).get().toSortedPair());
//                    }
//                });

        this.compositeDisposable.add(bus.getEvents()
                .filter(e -> e.getKey().equals(BusAddresses.FILE_ADDED))
                .map(e -> e.getValue())
                .flatMap(name -> Observable.just(name)
                        .subscribeOn(Schedulers.computation())
                        .map(path -> new Pair<>(path, DocumentAnalyzer.analyzeDocument(Document.fromPath(path)))))
                .subscribe(p -> {
                            Utils.log("Computed element " + p.getKey());

                            if (singleResults.keySet().contains(p.getKey())) {
                                singleResults.remove(p.getKey());

                            } else {
                                singleResults.put(p.getKey(), p.getValue());
                            }

                            view.printResult(singleResults.values().stream().reduce((doc, doc2) -> DocumentResult.merge(doc, doc2)).get().toSortedPair());
                            if (view.getInputSize() == singleResults.size()) {
                                this.view.notifyComputationCompleted();
                                Utils.log("FINEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
                            }
                        }
                ));

        this.compositeDisposable.add(bus.getEvents()
                .observeOn(Schedulers.io())
                .filter(e -> e.getKey().equals(BusAddresses.FILE_REMOVED))
                .map(e -> e.getValue())
                .subscribe(path -> {
                    Utils.log("Removing " + path);
                    if (singleResults.keySet().contains(path)) {
                        singleResults.remove(path);
                        view.printResult(singleResults.values().stream().reduce((doc, doc2) -> DocumentResult.merge(doc, doc2)).get().toSortedPair());

                    } else {
                        singleResults.put(path, new DocumentResult());
                    }


                }));

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
        this.compositeDisposable.clear();
    }
}
