package assignment1.concurrent.performance.seq;

import assignment1.Particle;
import assignment1.ParticleUtils;
import assignment1.common.Cron;
import assignment1.common.V2d;
import assignment1.concurrent.ConcurrentContext;

import java.util.ArrayList;

public class SequentialWorker extends Thread {

    private ConcurrentContext context;
    private int steps;

    private int timeElapsed = 2;

    public SequentialWorker(ConcurrentContext context, int nSteps) {
        this.context = context;
        this.steps = nSteps;
    }

    @Override
    public void run() {
        super.run();
        Cron cron = new Cron();
        cron.start();
        for (int i = 0; i < steps; i++) {
            this.calculateForces();
            this.updatePosition();
            context.refreshParticlesList();
            context.printAllParticles();
        }
        cron.stop();

        System.out.println(String.format("Sequential time for %d steps with %d particles: %d", steps, context.getParticles().size(), cron.getTime()));
    }


    private void calculateForces() {
        ArrayList<Particle> particles = context.getParticles();
        for (int i = 0; i < particles.size(); i++) {

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

        for (int i = 0; i < particles.size(); i++) {
            Particle particle = particles.get(i);
            context.getTempPositions().get(i).x = particle.getPosition().x + timeElapsed * particle.getSpeed().x;
            context.getTempPositions().get(i).y = particle.getPosition().y + timeElapsed * particle.getSpeed().y;
            particle.getSpeed().x = timeElapsed * particle.getForce().x / particle.getmConst();
            particle.getSpeed().y = timeElapsed * particle.getForce().y / particle.getmConst();
        }

    }
}
