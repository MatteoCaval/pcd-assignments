package assignment1;

import assignment1.common.V2d;

import java.util.ArrayList;
import java.util.Random;

public class Context {

    private static final int K_CONST = 1;
    private static final double OVERLAPPED_X_DELTA = 0.05;
    private static final double OVERLAPPED_Y_DELTA = 0.05;

    private final Boundary boundary;
    private final ArrayList<Particle> particles;

    public Context() {
        this.boundary = new Boundary(0, 0, 10, 10);
        this.particles = new ArrayList<>();
    }

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

    private V2d getParticleForce(Particle particle, ArrayList<Particle> particleList) {
        return particleList.stream().map(p -> ParticleUtils.getForceBetweenParticle(particle, p, K_CONST)).reduce(V2d::sum).get();
    }

    private ArrayList<Particle> getOtherParticles(Particle particle) {
        ArrayList<Particle> reamainingParticles = new ArrayList<>();
        for (Particle part : particles) {
            if (!part.equals(particle)) {
                reamainingParticles.add(part);
            }
        }
        return reamainingParticles;
    }

    public void calculateForces() {
        this.particles.stream().forEach(p -> p.setForce(getParticleForce(p, getOtherParticles(p))));
    }

    public void printAllParticles() {
        particles.stream().forEach(p -> System.out.println(p));
        System.out.println();
    }

    public void doStep(int timeElapsed) {
        calculateForces();
        this.particles.stream().forEach(particle -> {
            particle.getPosition().x += timeElapsed * particle.getSpeed().x;
            particle.getPosition().y += timeElapsed * particle.getSpeed().y;
            particle.getSpeed().x += timeElapsed * particle.getForce().x / particle.getmConst();
            particle.getSpeed().y += timeElapsed * particle.getForce().y / particle.getmConst();

        });

    }

}
