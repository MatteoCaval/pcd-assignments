package assignment1.concurrent;

import assignment1.view.ParticleView;

public class MainWorker extends Thread {

    private ConcurrentContext context;
    private int N_THREAD = 8; //TODO: meglio 8 + 1 ? da sustemare in ognu caso con il numero di processori
    private ParticleView view;
    private final StopFlag stopFlag;
    private Counter counter;

    //qua terrei pure referenza alla view e al framerate
    //ci vuole anche un listener per la view

    public MainWorker(ConcurrentContext context, ParticleView view, StopFlag stopFlag, Counter counter) {
        this.context = context;
        this.view = view;
        this.stopFlag = stopFlag;
        this.counter = counter;
    }


    @Override
    public void run() {
        super.run();

        Barrier barrier = new Barrier(N_THREAD);
        ProceedBarrier proceedBarrier = new ProceedBarrier(N_THREAD);

        int particleNumber = context.getParticles().size();

        N_THREAD = Math.min(particleNumber, N_THREAD);

        int particlePerThread = particleNumber / N_THREAD;


        for (int i = 0; i < N_THREAD - 1; i++) {
            new ParticleWorker(particlePerThread * i, particlePerThread + (i * particlePerThread), context, barrier, proceedBarrier).start();
        }

        new ParticleWorker((N_THREAD - 1) * particlePerThread, particleNumber, context, barrier, proceedBarrier).start();

        try {
            while (!stopFlag.isStopped()) {
                if (!counter.maxReached()){
                    barrier.waitAllDone(); //aspetta che tutti i thread abbiamo finito il calcolo forza/aggiornamento posizione

                    System.out.println("All thread done.");

                    //sarebbe da aggiornare le liste correttamente e stampare i risultati
                    context.refreshParticlesList();
                    context.printAllParticles();

                    view.updatePositions(context.getTempPositions());
                    counter.inc();

                    Thread.sleep(15);

                    System.out.println("Resume all threads.");
                    proceedBarrier.proceed();
                } else {
                    stopFlag.stop();
                    view.changeState("Out of steps");
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
