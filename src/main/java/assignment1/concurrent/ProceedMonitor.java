package assignment1.concurrent;

public class ProceedMonitor {


    public synchronized void proceed() {
        notifyAll();
    }

    /*
    used by the particle threads, waiting for the next round
     */
    public synchronized void waitNextRound() throws InterruptedException {
        wait();
    }
}
