package assignment1.concurrent;

public class Counter {

    private int counterValue;
    private final Integer maxValue;

    public Counter(Integer maxValue) {
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
        if (maxValue != null){
            return this.counterValue == this.maxValue;
        } else {
            return false;
        }
    }
 }
