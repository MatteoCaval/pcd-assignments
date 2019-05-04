package assignment2.view;

import java.util.List;

public interface SelectorListener {

    void startPressed(List<String> paths);

    void filesAdded(String... paths);

    void fileRemoved(String path);

    void stopPressed();

}
