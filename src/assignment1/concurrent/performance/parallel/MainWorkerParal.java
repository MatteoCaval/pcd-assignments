package assignment1.concurrent.performance;

import assignment1.common.Cron;
import assignment1.concurrent.*;

public class MainWorkerParal extends Thread {

    private ConcurrentContext context;
    private int N_THREAD = Runtime.getRuntime().availableProcessors();
    private Counter numSteps;

    public MainWorkerParal(ConcurrentContext context, Counter numSteps) {
        this.context = context;
        this.numSteps = numSteps;
    }

    @Override
    public void run() {
        super.run();
        Cron cron = new Cron();
        cron.start();
        Barrier barrier = new Barrier(N_THREAD);
        ProceedMonitor proceedMonitor = new ProceedMonitor();

        int particleNumber = context.getParticles().size();

        N_THREAD = Math.min(particleNumber, N_THREAD);

        int particlePerThread = particleNumber / N_THREAD;


        for (int i = 0; i < N_THREAD - 1; i++) {
            new ParticleWorkerParal(particlePerThread * i, particlePerThread + (i * particlePerThread), context, barrier, proceedMonitor).start();
        }

        new ParticleWorkerParal((N_THREAD - 1) * particlePerThread, particleNumber, context, barrier, proceedMonitor).start();

        try {
            while (!numSteps.maxReached()) {
                barrier.waitAllDone(); //aspetta che tutti i thread abbiamo finito il calcolo forza/aggiornamento posizione

                context.refreshParticlesList();
                context.printAllParticles();

                numSteps.inc();

                Thread.sleep(15);

                proceedMonitor.proceed();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        cron.stop();
        System.out.println(String.format("Parallel time for %d steps with %d particles: %d", numSteps.value(), context.getParticles().size(), cron.getTime()));
    }
}
