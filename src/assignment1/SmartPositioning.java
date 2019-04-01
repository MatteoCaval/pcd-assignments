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

        ConcurrentContext context1 = new ConcurrentContext(500);
        ParticleView view = new ParticleView(pixel_w, pixel_h);
        view.display();

        Controller controller = new Controller(view, context1); //controller da passare alla view per comunicare pressione start/stop

        view.setController(controller);
        controller.start();

    }
}
