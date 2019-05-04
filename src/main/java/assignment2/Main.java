package assignment2;

import assignment2.e0.ExecutorController;

public class Main {

    public static void main(String[] args) {
        MainView view = new ViewImpl();
        new ExecutorController(view);
    }

}
