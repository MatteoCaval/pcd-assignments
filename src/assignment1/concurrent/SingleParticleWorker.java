package assignment1.concurrent;

import assignment1.Particle;
import assignment1.ParticleUtils;
import assignment1.common.V2d;

import java.util.List;

public class SingleParticleWorker extends ParticleWorker {

    private double k_const;
    private int timeElapsed = 2; //TODO: sistemare better

    public SingleParticleWorker(int from, int to, List<Particle> particles, double k_const, Object forceLock, Object positionLock, Counter counter) {
        super(from, to, particles, forceLock, positionLock, counter);
        this.k_const = k_const;
    }


    @Override
    public void run() {
        while (true) {


            calculateForces();

            synchronized (this) {
                this.counter.inc();
                if (this.counter.maxReached()) {
                    System.out.println("All forces done");
                    synchronized (this.forceLock) {
                        this.forceLock.notifyAll();
                    }
                    this.counter.reset();
                } else {
                    try {
                        synchronized (this.forceLock) {
                            this.forceLock.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("Force " + this.getName() + " done");

            updatePosition();

            synchronized (this) {
                this.counter.inc();
                if (this.counter.maxReached()) {
                    System.out.println("All positions done");
                    synchronized (this.forceLock) {
                        this.forceLock.notifyAll();
                    }
                    this.counter.reset();
                } else {
                    try {
                        synchronized (this.forceLock) {
                            this.forceLock.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("Position " + this.getName() + " done");


        }


    }

    private void calculateForces() {
        System.out.println("FW from " + from + " to " + to);
        for (int i = from; i < to; i++) {

            V2d particleForce = particles.get(i).getForce();

            for (int j = 0; j < particles.size(); j++) {
                if (i != j) {
                    particleForce = particleForce.sum(ParticleUtils.getForceBetweenParticle(particles.get(i), particles.get(j), k_const));
                }
            }

            Particle currentParticle = particles.get(i);

            //  Aggiunte la forza di attrito
            particleForce.x -= currentParticle.getFriction() * currentParticle.getSpeed().x;
            particleForce.y -= currentParticle.getFriction() * currentParticle.getSpeed().y;

            currentParticle.setForce(particleForce);
        }
    }

    private void updatePosition() {
        System.out.println("PW from " + from + " to " + to);

        particles.subList(from, to).forEach(particle -> {
            particle.getPosition().x += timeElapsed * particle.getSpeed().x;
            particle.getPosition().y += timeElapsed * particle.getSpeed().y;
            particle.getSpeed().x += timeElapsed * particle.getForce().x / particle.getmConst();
            particle.getSpeed().y += timeElapsed * particle.getForce().y / particle.getmConst();
        });
    }

}
