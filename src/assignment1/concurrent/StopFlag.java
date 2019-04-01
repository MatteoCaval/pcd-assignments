package assignment1.concurrent;

public class StopFlag {

    private boolean isStopped;

    public StopFlag() {
        this.isStopped = false;
    }

    public synchronized void stop() {
        this.isStopped = true;
    }

    public synchronized void start() {
        this.isStopped = false;
    }

    public synchronized boolean isStopped() {
        return this.isStopped;
    }


}
