package assignment1.concurrent;

public class ProceedBarrier {

    private boolean canProceed = false;
    private final int nthread;
    private int count = 0;

    public ProceedBarrier(int nthread) {
        this.nthread = nthread;
    }

    public synchronized void proceed() {
        this.canProceed = true;
        notifyAll();
    }

    /*
    used by the particle threads, waiting for the next round
     */
    public synchronized void waitNextRound() throws InterruptedException {
        while (!this.canProceed) {
            wait();
        }
        this.count++;
        if (this.count == this.nthread) { //only the last thread reset the values, otherwise it would have stopped all other threads
            this.resetBarrier();
        }

    }

    private void resetBarrier() {
        this.count = 0;
        this.canProceed = false;
    }
}
