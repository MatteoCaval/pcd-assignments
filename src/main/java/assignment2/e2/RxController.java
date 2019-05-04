package assignment2.e2;

import assignment2.*;
import assignment2.e1.BusAddresses;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.List;

public class RxController extends BaseController {

    private ComputationResults singleResults;
    private RxBus bus;
    private CompositeDisposable compositeDisposable;
    private boolean parallel;

    public RxController(MainView view, boolean parallel) {
        super(view);
        bus = RxBus.getInstace();
        this.compositeDisposable = new CompositeDisposable();
        this.singleResults = new ComputationResults();
        this.parallel = parallel;
    }

    @Override
    public void startPressed(List<String> paths) {
        super.startPressed(paths);
        this.singleResults.clear();

        this.subscribeAtFileAddition(parallel);

        this.compositeDisposable.add(bus.getEvents()
                .observeOn(Schedulers.io())
                .filter(e -> e.getKey().equals(BusAddresses.FILE_REMOVED))
                .map(e -> e.getValue())
                .subscribe(path -> {
                    Utils.log("Removing " + path);
                    this.singleResults.removeResult(path);
                }));

        filesAdded(paths.toArray(new String[0]));
    }

    @Override
    public void filesAdded(String... filePaths) {
        Arrays.stream(filePaths).forEach(p -> {
            bus.putEvent(new Pair<>(BusAddresses.FILE_ADDED, p));
        });

    }

    @Override
    public void fileRemoved(String path) {
        bus.putEvent(new Pair<>(BusAddresses.FILE_REMOVED, path));
    }

    @Override
    public void stopPressed() {
        super.stopPressed();
        this.compositeDisposable.clear();
    }

    private void subscribeAtFileAddition(boolean parallel) {
        Disposable disposable;
        if (parallel) {
            disposable = bus.getEvents()
                    .filter(e -> e.getKey().equals(BusAddresses.FILE_ADDED))
                    .map(e -> e.getValue())
                    .flatMap(name -> Observable.just(name)
                            .subscribeOn(Schedulers.computation())
                            .map(path -> new Pair<>(path, DocumentAnalyzer.resultFromPath(path))))
                    .subscribe(p -> {
                                Utils.log("Computed element " + p.getKey());

                                this.singleResults.addResult(p.getKey(), p.getValue());
                                view.printResult(this.singleResults.getGlobalOrderedResult());
                                if (this.singleResults.checkComputationEnded(this.view.getInputSize())) {
                                    this.view.notifyComputationCompleted();
                                    this.view.setComputationTime(this.crono.stop().getTime());
                                }
                            }
                    );

        } else {
            disposable = bus.getEvents()
                    .observeOn(Schedulers.computation())
                    .filter(e -> e.getKey().equals(BusAddresses.FILE_ADDED))
                    .map(e -> e.getValue())
                    .map(e -> new Pair<>(e, DocumentAnalyzer.resultFromPath(e)))
                    .subscribe(p -> {
                                Utils.log("Computed element " + p.getKey());
                                this.singleResults.addResult(p.getKey(), p.getValue());
                                view.printResult(this.singleResults.getGlobalOrderedResult());
                                if (this.singleResults.checkComputationEnded(this.view.getInputSize())) {
                                    this.view.notifyComputationCompleted();
                                    this.view.setComputationTime(this.crono.stop().getTime());
                                }
                            }
                    );
        }

        this.compositeDisposable.add(disposable);


    }
}
