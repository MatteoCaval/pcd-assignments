package assignment1;

import assignment1.concurrent.ConcurrentContext;
import assignment1.concurrent.Controller;
import assignment1.view.ParticleView;

public class SmartPositioning {
    private final static int WIDTH = 1200;
    private final static int HEIGHT = 800;

    public static void main(String[] args) throws InterruptedException {
        ConcurrentContext context1 = new ConcurrentContext(500, WIDTH, HEIGHT);
        ParticleView view = new ParticleView(WIDTH, HEIGHT);

        Controller controller = new Controller(view, context1); //controller da passare alla view per comunicare pressione startPressed/stopPressed
        view.setInputListener(controller);

        view.display();
    }
}
