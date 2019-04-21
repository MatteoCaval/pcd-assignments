package assignment2;

import java.io.File;

public class Controller implements View.SelectorListener {

    public void start() {
        new View(this);
    }

    @Override
    public void directorySelected(File file) {
        System.out.println(file.getPath());
    }
}
