package assignment1.concurrent;

import assignment1.view.InputListener;
import assignment1.view.ParticleView;

public class Controller implements InputListener {

    private ParticleView view;
    private StopFlag stopFlag;
    private ConcurrentContext context;
    private Counter counter;

    public Controller(ParticleView view, ConcurrentContext context, Counter counter) {
        this.view = view;
        this.context = context;
        this.counter = counter;
        this.stopFlag = new StopFlag();
    }

    public void start() {
        stopFlag.start();
        new MainWorker(context, view, stopFlag, counter).start();
    }

    @Override
    public void startPressed() {
        if (this.stopFlag.isStopped()) {
           this.start();
        }
    }

    @Override
    public void stopPressed() {
        this.stopFlag.stop();
    }
}
