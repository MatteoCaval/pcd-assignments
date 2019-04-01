package assignment1.concurrent;

import assignment1.view.InputListener;
import assignment1.view.ParticleView;

public class Controller implements InputListener {

    private ParticleView view;
    private StopFlag stopFlag;
    private ConcurrentContext context;

    public Controller(ParticleView view, ConcurrentContext context) {
        this.view = view;
        this.context = context;
        stopFlag = new StopFlag();
        stopFlag.stop();
    }

    public void start() {
        stopFlag.start();
        new MainWorker(context, view, stopFlag).start();
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
