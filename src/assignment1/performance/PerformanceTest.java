package assignment1.concurrent.performance;

import assignment1.concurrent.ConcurrentContext;
import assignment1.concurrent.performance.parallel.MainWorkerParal;
import assignment1.concurrent.performance.seq.SequentialWorker;

public class PerformanceTest {
    private final static int N_PARTICLES = 1240;  // a 1240 circa si equivalgono. Poi vince abbestia il parallelo
    private final static Integer N_STEPS = 1;

    public static void main(String[] args) throws InterruptedException {
        ConcurrentContext context = new ConcurrentContext(N_PARTICLES);
        MainWorkerParal workerParal = new MainWorkerParal(context, N_STEPS);
        SequentialWorker sequentialWorker = new SequentialWorker(context, N_STEPS);

        sequentialWorker.start();
        sequentialWorker.join();

        workerParal.start();
        workerParal.join();

        System.exit(0);
    }
}
