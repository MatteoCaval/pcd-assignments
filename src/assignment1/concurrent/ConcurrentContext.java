package assignment1.concurrent;


import assignment1.Boundary;
import assignment1.Particle;
import assignment1.common.Cron;

import java.util.ArrayList;
import java.util.Random;

public class ConcurrentContext {

    private static final int K_CONST = 1;

    private final Boundary boundary;
    private final ArrayList<Particle> particles;
    private ArrayList<ForceCalculatorWorker> forceCalculatorWorkers = new ArrayList<>();
    private ArrayList<UpdatePositionWorker> updatePositionWorkers = new ArrayList<>();

    public ConcurrentContext() {
        this.boundary = new Boundary(0, 0, 80, 80);
        this.particles = new ArrayList<>();
    }

    public void start() throws InterruptedException {

        int numOfParticle = 10;
        int nThread = 4;
        createNParticles(numOfParticle);

        Cron cron = new Cron();

        nThread = Math.min(numOfParticle, nThread);

        int particlePerThread = numOfParticle / nThread;

        printAllParticles();

        cron.start();

        calculateForcesAndPrint(nThread, particlePerThread);
        updatePositionsAndPrint(nThread, particlePerThread);

        cron.stop();

        System.out.println("Time: " + cron.getTime());

    }


    /*
    FIXME :
    se faccio start-join su i 2 thread e poi rifaccio partire si piant perchè non posso richiamare 2 volte start sullo stesso thread
    serve meccanismo per fare in modo che tutti i forcecalculatore eseguano - si fermini - tutti i poscalculator - si fermini  eccc...
     */
    private void calculateForcesAndPrint(int nthread, int particlePerThread) throws InterruptedException {
        for (int i = 0; i < nthread - 1; i++) {
            forceCalculatorWorkers.add(new ForceCalculatorWorker(particlePerThread * i, particlePerThread + (i * particlePerThread), particles, K_CONST));
            forceCalculatorWorkers.get(forceCalculatorWorkers.size() - 1).start();
        }

        forceCalculatorWorkers.add(new ForceCalculatorWorker((nthread - 1) * particlePerThread, particles.size(), particles, K_CONST));
        forceCalculatorWorkers.get(forceCalculatorWorkers.size() - 1).start();

        forceCalculatorWorkers.stream().forEach(w -> {
            try {
                w.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        forceCalculatorWorkers.clear();

        printAllParticles();
    }

    private void updatePositionsAndPrint(int nthread, int particlePerThread) {

        for (int i = 0; i < nthread - 1; i++) {
            updatePositionWorkers.add(new UpdatePositionWorker(particlePerThread * i, particlePerThread + (i * particlePerThread), particles));
            updatePositionWorkers.get(updatePositionWorkers.size() - 1).start();
        }

        updatePositionWorkers.add(new UpdatePositionWorker((nthread - 1) * particlePerThread, particles.size(), particles));
        updatePositionWorkers.get(updatePositionWorkers.size() - 1).start();

        updatePositionWorkers.stream().forEach(w -> {
            try {
                w.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        updatePositionWorkers.clear();

        printAllParticles();
    }

    private void printAllParticles() {
        particles.stream().forEach(p -> System.out.println(p));
        System.out.println();
    }

    // region Particle factories

    public void createParticle(double x, double y) {
        Particle particle = new Particle(x, y);
        this.particles.add(particle);
    }

    public void createNParticles(int numberOfParticlesToCreate) {
        for (int i = 0; i < numberOfParticlesToCreate; i++) {
            createRandomParticle();
        }
    }

    private void createRandomParticle() {
        Random r = new Random();
        double randomX = (this.boundary.getX0() + (this.boundary.getX1() - this.boundary.getX0()) * r.nextDouble());
        double randomY = this.boundary.getY0() + (this.boundary.getY1() - this.boundary.getY0()) * r.nextDouble();
        this.createParticle(randomX, randomY);
    }

    // endregion



}
