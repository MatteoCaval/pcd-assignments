package assignment1.concurrent;

public class MainWorker extends Thread {

    private ConcurrentContext context;
    private int N_THREAD = 4;

    //qua terrei pure referenza alla view e al framerate
    //ci vuole anche un listener per la view

    public MainWorker(ConcurrentContext context) {
        this.context = context;
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
            while (true) {


                barrier.waitAllDone(); //aspetta che tutti i thread abbiamo finito il calcolo forza/aggiornamento posizione

                System.out.println("All thread done.");

                //sarebbe da aggiornare le liste correttamente e stampare i risultati

                Thread.sleep(1000);

                System.out.println("Resume all threads.");
                proceedBarrier.proceed();


            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
