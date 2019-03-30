package assignment1;

import assignment1.concurrent.ConcurrentContext;
import assignment1.concurrent.MainWorker;

public class SmartPositioning {
    public static void main(String[] args) throws InterruptedException {
        
        ConcurrentContext context1 = new ConcurrentContext();

        new MainWorker(context1).start();

    }
}
