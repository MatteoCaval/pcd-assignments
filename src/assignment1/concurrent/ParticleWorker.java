package assignment1.concurrent;

import assignment1.Particle;
import assignment1.ParticleUtils;
import assignment1.common.V2d;

import java.util.ArrayList;
import java.util.List;

public class ParticleWorker extends Thread {

    private final int from, to;

    private ConcurrentContext context;
    private int timeElapsed = 16; //TODO: sistemare better
    private Barrier barrier;
    private ProceedBarrier proceedBarrier;

    public ParticleWorker(int from, int to, ConcurrentContext context, Barrier barrier, ProceedBarrier proceedBarrier) {
        this.from = from;
        this.to = to;
        this.context = context;
        this.barrier = barrier;
        this.proceedBarrier = proceedBarrier;
    }


    @Override
    public void run() {
        while (true) {

            System.out.println(String.format("Calculating forces from %d to %d", from, to));
            calculateForces();

            System.out.println(String.format("Updating positions from %d to %d", from, to));
            updatePosition();

            barrier.inc();

            try {
                proceedBarrier.waitNextRound();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }


    }

    private void calculateForces() {
        ArrayList<Particle> particles = context.getParticles();

        System.out.println("FW from " + from + " to " + to);
        for (int i = from; i < to; i++) {

            V2d particleForce = particles.get(i).getForce();

            for (int j = 0; j < particles.size(); j++) {
                if (i != j) {
                    particleForce = particleForce.sum(ParticleUtils.getForceBetweenParticle(particles.get(i), particles.get(j), context.getkConst()));
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
        ArrayList<Particle> particles = context.getParticles();
        System.out.println("PW from " + from + " to " + to);

        for (int i = from; i < to; i++) {
            Particle particle = particles.get(i);
            context.getTempPositions().get(i).x = particle.getPosition().x + timeElapsed * particle.getSpeed().x;
            context.getTempPositions().get(i).y = particle.getPosition().y + timeElapsed * particle.getSpeed().y;
            particle.getSpeed().x = timeElapsed * particle.getForce().x / particle.getmConst();
            particle.getSpeed().y = timeElapsed * particle.getForce().y / particle.getmConst();
        }

    }


}
