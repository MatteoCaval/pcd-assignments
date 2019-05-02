package assignment2.e2;

import assignment2.DocumentResult;
import assignment2.View;
import assignment2.e1.BusAddresses;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class RxController implements View.SelectorListener {

    private View view;
    private ConcurrentHashMap<String, DocumentResult> singleResults = new ConcurrentHashMap<>();
    private RxBus bus;

    public RxController() {
        bus = RxBus.getInstace();
        this.view = new View(this);
        new ReactiveAnalyzer();
    }

    @Override
    public void startPressed(List<String> paths) {
        bus.putEvent(new Pair(BusAddresses.START, null));
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
