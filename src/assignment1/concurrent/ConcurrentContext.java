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
    private ArrayList<P2d> tempPositions = new ArrayList<>();

    public ConcurrentContext(int numOfParticles) {
        this.boundary = new Boundary(200, 200, 300, 300);
        this.particles = new ArrayList<>();
        createNParticles(numOfParticles);
    }

    // region Public methods

    public void refreshParticlesList() {
        for (int i = 0; i < particles.size(); i++) {
            this.particles.get(i).getPosition().x = tempPositions.get(i).x;
            this.particles.get(i).getPosition().y = tempPositions.get(i).y;
        }
    }

    public void printAllParticles() {
        particles.stream().forEach(p -> System.out.println(p));
        System.out.println();
    }

    // endregion

    // region Particle factories

    public void createParticle(double x, double y) {
        Particle particle = new Particle(x, y);
        this.particles.add(particle);
    }

    public void createNParticles(int numberOfParticlesToCreate) {
        for (int i = 0; i < numberOfParticlesToCreate; i++) {
            createRandomParticle();
            this.tempPositions.add(new P2d(0, 0));
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

    public ArrayList<P2d> getTempPositions() {
        return tempPositions;
    }

    public int getkConst() {
        return K_CONST;
    }

    // endregion

}
