package assignment1.performance.parallel;

import assignment1.common.Cron;
import assignment1.concurrent.*;

public class MainWorkerParal extends Thread {

    private ConcurrentContext context;
    private int nthreads = 1;
    private int numSteps;
    private int curStep;

    public MainWorkerParal(ConcurrentContext context, int numSteps, int nthreads) {
        this.context = context;
        this.numSteps = numSteps;
        this.curStep = 0;
        this.nthreads = nthreads;
    }

    @Override
    public void run() {
        super.run();

        Cron cron = new Cron();

        int particleNumber = context.getParticles().size();

        nthreads = Math.min(particleNumber, nthreads);

        Barrier barrier = new Barrier(nthreads);
        ProceedMonitor proceedMonitor = new ProceedMonitor();

        int particlePerThread = particleNumber / nthreads;

        cron.start();

        for (int i = 0; i < nthreads - 1; i++) {
            new ParticleWorkerParal(particlePerThread * i, particlePerThread + (i * particlePerThread), context, barrier, proceedMonitor).start();
        }

        new ParticleWorkerParal((nthreads - 1) * particlePerThread, particleNumber, context, barrier, proceedMonitor).start();

        try {
            while (curStep < numSteps) {
                barrier.waitAllDone(); //aspetta che tutti i thread abbiamo finito il calcolo forza/aggiornamento posizione

                context.refreshParticlesList();
                context.printAllParticles();

                curStep++;

                Thread.sleep(15);

                proceedMonitor.proceed();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        cron.stop();
        System.out.println(String.format("Parallel time for %d steps, %d threads with %d particles: %d", numSteps, nthreads, context.getParticles().size(), cron.getTime()));
    }
}
