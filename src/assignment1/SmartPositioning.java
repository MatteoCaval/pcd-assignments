package assignment1;

import assignment1.concurrent.ConcurrentContext;
import assignment1.concurrent.Controller;
import assignment1.concurrent.Counter;
import assignment1.view.ParticleView;

public class SmartPositioning {
    public final static boolean IS_DEBUG = false;
    private final static int WIDTH = 1200;
    private final static int HEIGHT = 700;
    private final static int N_PARTICLES = 1000;
    private final static Integer N_STEPS = null;


    public static void main(String[] args) {
        ConcurrentContext context = new ConcurrentContext(N_PARTICLES);
        ParticleView view = new ParticleView(WIDTH, HEIGHT);

        Counter counter = new Counter(N_STEPS);
        Controller controller = new Controller(view, context, counter); //controller da passare alla view per comunicare pressione startPressed/stopPressed

        view.setInputListener(controller);
        view.setPartcilesNumber(N_PARTICLES);
        view.display();
    }
}
