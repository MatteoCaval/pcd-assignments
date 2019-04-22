package assignment2;

import java.util.List;

public class Controller implements View.SelectorListener {

    public void start() {
        new View(this);
    }

    @Override
    public void startPressed(List<String> paths) {
        System.out.println(Document.fromPath(paths.get(0)).getLines().get(0));
    }
}
