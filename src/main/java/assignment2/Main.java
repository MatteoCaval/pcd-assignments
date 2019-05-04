package assignment2;

import assignment2.e0.ExecutorController;
import assignment2.e1.VertxController;
import assignment2.e2.RxController;
import assignment2.view.MainView;
import assignment2.view.SelectionView;
import assignment2.view.ViewImpl;

public class Main {

    public static void main(String[] args) {
        new SelectionView((mode, parallel) -> {
            MainView view = new ViewImpl();
            String paralString = parallel ? "Parallel" : "Sequential";
            switch (mode) {
                case ES0:
                    new ExecutorController(view, parallel);
                    view.setFrameTitle("Assignment 2 - Tasks and executors - " + paralString);
                    break;
                case ES1:
                    new VertxController(view, parallel);
                    view.setFrameTitle("Assignment 2 - Async in the event loop - " + paralString);
                    break;
                case ES2:
                    new RxController(view, parallel);
                    view.setFrameTitle("Assignment 2 - Reactive streams - " + paralString);
                    break;
            }
        });
    }
}
