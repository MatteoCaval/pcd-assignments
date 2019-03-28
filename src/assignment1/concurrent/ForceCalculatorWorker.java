package assignment1.concurrent;

import assignment1.Particle;
import assignment1.ParticleUtils;
import assignment1.common.V2d;

import java.util.List;

public class ForceCalculatorWorker extends ParticleWorker {

    private double k_const;


    public ForceCalculatorWorker(int from, int to, List<Particle> particles, double k_const, Object forceLock, Object positionLock, Counter counter) {
        super(from, to, particles, forceLock, positionLock, counter);
        this.k_const = k_const;
    }


    @Override
    public void run() {
        System.out.println("FW from " + from + " to " + to);
        for (int i = from; i < to; i++) {

            V2d particleForce = particles.get(i).getForce();

            for (int j = 0; j < particles.size(); j++) {
                if (i != j) {
                    particleForce = particleForce.sum(ParticleUtils.getForceBetweenParticle(particles.get(i), particles.get(j), k_const ));
                }
            }

            Particle currentParticle = particles.get(i);

            //  Aggiunte la forza di attrito
            particleForce.x -= currentParticle.getFriction() * currentParticle.getSpeed().x;
            particleForce.y -= currentParticle.getFriction() * currentParticle.getSpeed().y;

            currentParticle.setForce(particleForce);

        }


    }

}
