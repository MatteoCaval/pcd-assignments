package assignment1.performance;

import assignment1.concurrent.ConcurrentContext;
import assignment1.performance.conc.MainWorkerConcur;
import assignment1.performance.seq.SequentialWorker;

public class PerformanceTest {
    private final static int N_PARTICLES = 5000;
    private final static Integer N_STEPS = 1;

    public static void main(String[] args) throws InterruptedException {
        ConcurrentContext context = new ConcurrentContext(N_PARTICLES);
        MainWorkerConcur workerParal = new MainWorkerConcur(context, N_STEPS, 8);
        SequentialWorker sequentialWorker = new SequentialWorker(context, N_STEPS);

        sequentialWorker.start();
        sequentialWorker.join();

        workerParal.start();
        workerParal.join();

        System.exit(0);
    }
}
