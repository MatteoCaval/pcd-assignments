package assignment1;

import assignment1.common.P2d;
import assignment1.concurrent.ConcurrentContext;
import assignment1.concurrent.Controller;
import assignment1.concurrent.MainWorker;
import assignment1.view.ParticleView;

import java.util.ArrayList;

public class SmartPositioning {
    public static void main(String[] args) throws InterruptedException {
        int pixel_w = 1200;
        int pixel_h = 800;

        ConcurrentContext context1 = new ConcurrentContext(5000);
        ParticleView view = new ParticleView(pixel_w, pixel_h);
        view.display();
        view.updatePositions(new ArrayList<>());

        Controller controller = new Controller(view, context1); //controller da passare alla view per comunicare pressione start/stop
        controller.start();

    }
}
