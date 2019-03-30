package assignment1;

import assignment1.common.P2d;
import assignment1.concurrent.ConcurrentContext;
import assignment1.concurrent.MainWorker;
import assignment1.view.ParticleView;

public class SmartPositioning {
    public static void main(String[] args) throws InterruptedException {
        int pixel_w = 1200;
        int pixel_h = 800;

        ConcurrentContext context1 = new ConcurrentContext();

        new MainWorker(context1).start();
        ParticleView view = new ParticleView(pixel_w, pixel_h);
        view.display();

        /*P2d[] array = new P2d[]{new P2d(-1,0), new P2d(-1,-1), new P2d(1,1)};
        view.updatePositions(array);*/

    }
}
