package assignment1.concurrent;

import assignment1.common.States;
import assignment1.view.ParticleView;

public class MainWorker extends Thread {

    private ConcurrentContext context;
    private int N_THREAD = Runtime.getRuntime().availableProcessors();
    private ParticleView view;
    private final StopFlag stopFlag;
    private Counter numSteps;

    MainWorker(ConcurrentContext context, ParticleView view, StopFlag stopFlag, Counter numSteps) {
        this.context = context;
        this.view = view;
        this.stopFlag = stopFlag;
        this.numSteps = numSteps;
    }

    @Override
    public void run() {
        super.run();

        Barrier barrier = new Barrier(N_THREAD);
        ProceedMonitor proceedMonitor = new ProceedMonitor();

        int particleNumber = context.getParticles().size();

        N_THREAD = Math.min(particleNumber, N_THREAD);

        int particlePerThread = particleNumber / N_THREAD;


        for (int i = 0; i < N_THREAD - 1; i++) {
            new ParticleWorker(particlePerThread * i, particlePerThread + (i * particlePerThread), context, barrier, proceedMonitor, stopFlag).start();
        }

        new ParticleWorker((N_THREAD - 1) * particlePerThread, particleNumber, context, barrier, proceedMonitor, stopFlag).start();

        try {
            while (!stopFlag.isStopped()) {
                if (!numSteps.maxReached()) {
                    barrier.waitAllDone(); //aspetta che tutti i thread abbiamo finito il calcolo forza/aggiornamento posizione

                    System.out.println("All thread done.");

                    context.refreshParticlesList();
                    context.printAllParticles();

                    view.updatePositions(context.getTempPositions());

                    numSteps.inc();
                    view.updateSteps(numSteps.value());

                    Thread.sleep(15);

                    System.out.println("Resume all threads.");

                    proceedMonitor.proceed();

                } else {
                    stopFlag.stop();
                    view.changeState(States.OUT_OF_STEPS);
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
