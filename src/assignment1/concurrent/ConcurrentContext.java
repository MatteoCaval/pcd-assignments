package assignment1.concurrent;


import assignment1.Boundary;
import assignment1.Particle;
import assignment1.ParticleUtils;
import assignment1.common.V2d;

import java.util.ArrayList;
import java.util.Random;

public class ConcurrentContext {

    private static final int K_CONST = 1;
    private static final double OVERLAPPED_X_DELTA = 0.05;
    private static final double OVERLAPPED_Y_DELTA = 0.05;

    private final Boundary boundary;
    private final ArrayList<Particle> particles;
    private ArrayList<ForceCalculatorWorker> forceCalculatorWorkers;
    private ArrayList<UpdatePositionWorker> updatePositionWorkers;

    public ConcurrentContext() {
        this.boundary = new Boundary(0, 0, 10, 10);
        this.particles = new ArrayList<>();
    }

    public void start() throws InterruptedException {

        createParticle(1, 2);
        createParticle(4, 2);

        printAllParticles();

        doAndPrint();

        Thread.sleep(100);

        doAndPrint();


        Thread.sleep(100);
        doAndPrint();

        Thread.sleep(100);
        doAndPrint();

    }


    /*
    PROBLEMA: se faccio start-join su i 2 thread e poi rifaccio partire si piant perchè non posso richiamare 2 volte start sullo stesso thread
    serve meccanismo per fare in modo che tutti i forcecalculatore eseguano - si fermini - tutti i poscalculator - si fermini  eccc...
     */
    private void doAndPrint() throws InterruptedException {


        ForceCalculatorWorker forceWorker1 = new ForceCalculatorWorker(0, 1, particles, K_CONST);
        ForceCalculatorWorker forceWorker2 = new ForceCalculatorWorker(1, 1, particles, K_CONST);

        UpdatePositionWorker positionWorker1 = new UpdatePositionWorker(0, 1, particles);
        UpdatePositionWorker positionWorker2 = new UpdatePositionWorker(1, 1, particles);

        forceWorker1.start();
        forceWorker2.start();

        forceWorker1.join();
        forceWorker2.join();

        positionWorker1.start();
        positionWorker2.start();

        positionWorker2.join();
        positionWorker2.join();


        printAllParticles();
    }


    // region particle factories

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


    public void printAllParticles() {
        particles.stream().forEach(p -> System.out.println(p));
        System.out.println();
    }


}
