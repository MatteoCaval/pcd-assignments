package assignment1.concurrent;

import assignment1.view.ParticleView;

public class Controller /*implements InputListener */{

    private ParticleView view;
    private StopFlag stopFlag;
    private ConcurrentContext context;

    public Controller(ParticleView view, ConcurrentContext context) {
        this.view = view;
        this.context = context;
    }

    public void start() {
        stopFlag = new StopFlag();
        new MainWorker(context, view, stopFlag).start();
    }

    public void startPressed(){
        if(this.stopFlag.isStopped()){
            this.stopFlag.start();
        }
    }

    public void stopPressed(){
        if (!stopFlag.isStopped()){
            this.stopFlag.stop();
        }
    }
}
