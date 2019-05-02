package assignment2.e2;

import javafx.util.Pair;
import rx.Observable;
import rx.subjects.PublishSubject;

public class RxBus {

    private static RxBus instance;

    private PublishSubject<Pair<String, String>> subject = PublishSubject.create();

    private RxBus() {
    }

    public static RxBus getInstace() {
        if (instance == null) {
            instance = new RxBus();
        }
        return instance;
    }

    public void putEvent(Pair<String, String> event) {
        subject.onNext(event);
    }

    public Observable<Pair<String, String>> getEvents() {
        return subject;
    }

}
