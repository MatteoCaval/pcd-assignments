package assignment2;

import javafx.util.Pair;

import java.util.List;

public interface MainView {

    int getInputSize();

    void printResult(List<Pair<String, Integer>> result);

    void notifyComputationCompleted();

    void setComputationTime(long time);

    void clearComputationTime();

    void setListener(SelectorListener listener);

    void setFrameTitle(String title);
}
