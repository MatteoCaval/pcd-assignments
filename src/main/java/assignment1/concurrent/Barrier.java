package assignment1.concurrent;

/**
 * used to wait threads after force and posistion calculation, in order to updsate ui
 */
public class Barrier {

    private int count = 0;
    private final int nthread;

    public Barrier(int nthread) {
        this.nthread = nthread;
    }

    public synchronized void inc() {
        this.count++;
        notify(); //wakes the main thead, waiting inside che loop of the waitAllDone function
    }

    public synchronized void waitAllDone() throws InterruptedException {
        while (this.count < this.nthread) {
            wait();
        }
        this.resetBarrier();
    }

    private void resetBarrier() {
        this.count = 0;
    }
}
