package assignment2;

import assignment2.e1.Controller;
import assignment2.e2.RxController;

public class Main {

    public static void main(String[] args) {
//        new assignment2.e1.Controller();
        MainView view = new ViewImpl();
        new Controller(view);
    }

}
