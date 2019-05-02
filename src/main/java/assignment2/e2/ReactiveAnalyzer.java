package assignment2.e2;

import assignment2.Utils;
import io.reactivex.observers.DisposableObserver;
import rx.Scheduler;
import rx.schedulers.Schedulers;

public class ReactiveAnalyzer {

    public ReactiveAnalyzer() {

        RxBus.getInstace().getEvents()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(event ->
                        Utils.log("Event: " + event.getKey() + " -- " + event.getValue()));


    }
}
