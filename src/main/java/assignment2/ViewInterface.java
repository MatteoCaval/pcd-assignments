package assignment2;

import javafx.util.Pair;

import java.util.List;

public interface ViewInterface {

    int getInputSize();

    void printResult(List<Pair<String, Integer>> result);

    void notifyComputationCompleted();

}
