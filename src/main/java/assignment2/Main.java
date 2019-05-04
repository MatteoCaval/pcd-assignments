package assignment2;

import assignment2.e0.ExecutorController;
import assignment2.e1.VertxController;
import assignment2.e2.RxController;

public class Main {

    public static void main(String[] args) {
        new SelectionView((mode, parallel) -> {
            MainView view = new ViewImpl();
            switch (mode) {
                case ES0:
                    new ExecutorController(view, parallel);
                    view.setFrameTitle("Assignment 2 - Tasks and executors");
                    break;
                case ES1:
                    new VertxController(view, parallel);
                    view.setFrameTitle("Assignment 2 - Async in the event loop");
                    break;
                case ES2:
                    new RxController(view, parallel);
                    view.setFrameTitle("Assignment 2 - Reactive streams");
                    break;
            }
        });
    }
}
