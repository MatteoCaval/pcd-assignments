package assignment1.concurrent;


import assignment1.Boundary;
import assignment1.Particle;
import assignment1.common.Cron;
import assignment1.common.P2d;

import java.util.ArrayList;
import java.util.Random;

public class ConcurrentContext {

    private static final int K_CONST = 1;

    private final Boundary boundary;
    private final ArrayList<Particle> particles;
    private final ArrayList<P2d> tempPositions = new ArrayList<>();
    private int nThread = 4;

    public ConcurrentContext() {
        this.boundary = new Boundary(0, 0, 80, 80);
        this.particles = new ArrayList<>();
        int numOfParticle = 10;
        createNParticles(numOfParticle);
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

    // region Getter

    public ArrayList<Particle> getParticles() {
        return particles;
    }

    public int getkConst() {
        return K_CONST;
    }

    // endregion

}
