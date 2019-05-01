package assignment1.performance.conc;

import assignment1.Particle;
import assignment1.ParticleUtils;
import assignment1.common.V2d;
import assignment1.concurrent.Barrier;
import assignment1.concurrent.ConcurrentContext;
import assignment1.concurrent.ProceedMonitor;

import java.util.ArrayList;

public class ParticleWorkerConcur extends Thread {

    private final int from, to;

    private ConcurrentContext context;
    private int timeElapsed = 2;
    private Barrier barrier;
    private ProceedMonitor proceedMonitor;


    public ParticleWorkerConcur(int from, int to, ConcurrentContext context, Barrier barrier, ProceedMonitor proceedMonitor) {
        this.from = from;
        this.to = to;
        this.context = context;
        this.barrier = barrier;
        this.proceedMonitor = proceedMonitor;
    }


    @Override
    public void run() {
        while (true) {

            calculateForces();

            updatePosition();

            barrier.inc();

            try {
                proceedMonitor.waitNextRound();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void calculateForces() {
        ArrayList<Particle> particles = context.getParticles();

        for (int i = from; i < to; i++) {

            V2d particleForce = particles.get(i).getForce();

            for (int j = 0; j < particles.size(); j++) {
                if (i != j) {
                    particleForce = particleForce.sum(ParticleUtils.getForceBetweenParticle(particles.get(i), particles.get(j), context.getKConst()));
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

        for (int i = from; i < to; i++) {
            Particle particle = particles.get(i);
            context.getTempPositions().get(i).x = particle.getPosition().x + timeElapsed * particle.getSpeed().x;
            context.getTempPositions().get(i).y = particle.getPosition().y + timeElapsed * particle.getSpeed().y;
            particle.getSpeed().x = timeElapsed * particle.getForce().x / particle.getmConst();
            particle.getSpeed().y = timeElapsed * particle.getForce().y / particle.getmConst();
        }

    }


}
