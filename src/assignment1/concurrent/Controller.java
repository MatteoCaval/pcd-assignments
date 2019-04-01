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

        new MainWorker(context, view).start();
    }
}
