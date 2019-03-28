package assignment1.concurrent;

public class Counter {

    private int counterValue;
    private final int maxValue;

    public Counter(int maxValue) {
        this.counterValue = 0;
        this.maxValue = maxValue;
    }

    public synchronized void inc() {
        counterValue++;
    }

    public synchronized void reset() {
        counterValue = 0;
    }

    public synchronized boolean maxReached() {
        return this.counterValue == this.maxValue;
    }
 }
